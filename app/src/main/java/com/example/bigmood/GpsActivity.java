package com.example.bigmood;


import android.content.Context;
import android.os.Bundle;
import android.view.MenuInflater;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * The GpsActivity
 *
 * This activity is called from the sidebar and from the userview to display mood locations dependent
 * on the mode of operation.
 *
 * It also implements OnMneuItemClickListener for interacting with the mode popupmenu.
 *
 * Passed in through intent:
 *
 * "USER_ID" :  the id of the current user
 *
 * "MODE" : A String choosing the mode in which the activity is initially called in:
 *  - "USER": Show the moods of the user's history
 *  - "FOLLOW": Show the moods of those that the user is following
 */

public class GpsActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private String userId; //The Current user's id
    private String mode; // The mode of operation
    //used to get last known location for now
    private FusedLocationProviderClient fusedLocationClient;
    private double lastLong;
    private double lastLat;
    private double tempLong = -113.52705;
    private double tempLat = 53.52679;
    //spatial reference for map points
    private SpatialReference wgs84 = SpatialReferences.getWgs84();
    ///////////////////
    private String TAG = "GpsActivity";

    /*
    The arraylist for the user's moods, initially null and only loaded from the database if
    the mode is set to "USER" and it is still null
     */
    //private ArrayList<Point> userPoints = null;

    //hashmap for mood ID and map points
    private HashMap<String, Point> userPoints;


    /*
    The arraylist holding the followed moods, this will need to be refreshed every time
    that "FOLLOW" mode is selected.
     */
    /*
    private ArrayList<Point> followedPoints;
     */

    private FirebaseFirestore db;
    private CollectionReference moodCollection;

    private ArrayList<String> followedUsers;

    //using esri mapView
    private MapView mMapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        //get last location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lastLong = location.getLongitude();
                            lastLat = location.getLatitude();
                        }
                    }
                });
        ////////////////////////////////


        this.userId = getIntent().getExtras().getString("USER_ID");
        this.mode = getIntent().getExtras().getString("MODE");


        this.db = FirebaseFirestore.getInstance();
        this.moodCollection = db.collection("Moods");

        this.userPoints = new HashMap<>();

        //Initialize the friend list
        followedUsers = new ArrayList<>();
        db.collection("Users").whereEqualTo("userId",this.userId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot Doc : (task.getResult().getDocuments())){
                        if(Doc.get("userId").equals(userId)){
                            for (String friend : ((ArrayList<String>) Doc.get("userFriends"))){
                                followedUsers.add(friend);
                                Log.d(TAG, friend);
                            }
                            break;
                        }
                    }
                } else{
                    Log.d(TAG, "Failed to get user friends");
                }
            }
        });

        if (this.mode.equals("USER")){
            retrieveUserMoods();
        }
        else{
            retrieveFollowedMoods();
        }

        //call to display map
        displayMap();
        /////////////////////


        final FloatingActionButton modeButton = findViewById(R.id.gps_button_mode);
        FloatingActionButton zoominButton = findViewById(R.id.gps_button_zoomin);
        FloatingActionButton zoomoutButton = findViewById(R.id.gps_button_zoomout);

        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: When the mode button is pressed
                PopupMenu modemenu = new PopupMenu(getApplicationContext(), modeButton);
                modemenu.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) getParent());
                modemenu.inflate(R.menu.gps_mode_menu);
                modemenu.show();
            }
        });

        zoominButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: When the zoom in button is pressed
            }
        });

        zoomoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: When the zoom out button is pressed
            }
        });


    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.gps_mode_menu_user:

                if(userPoints == null){
                    retrieveUserMoods();
                }
                //TODO: change display to user moods
                Toast.makeText(this, "Display Users", Toast.LENGTH_SHORT).show();
                displayMap();
                return true;
            case R.id.gps_mode_menu_followed:
                retrieveFollowedMoods();
                //TODO: change display to followed moods
                Toast.makeText(this, "Display Followed", Toast.LENGTH_SHORT).show();
                displayMap();
                return true;
            default:
                return false;

        }

    }

    private void retrieveUserMoods(){
        //TODO: Retrieve the users moods
        followedUsers.clear();
        moodCollection.whereEqualTo("moodCreator",userId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            userPoints.put(doc.getId(), new Point(doc.getDouble("longitude"), doc.getDouble("latitude"), wgs84));
                        }
                    }
                } else{
                    Log.d(TAG, "Failed to get user moods");
                }
            }
        });
        // Point point = new Point(long, lat, SpatialReferences.getWgs84())
    }

    private void retrieveFollowedMoods(){
        //TODO: retrieve followed moods
        followedUsers.clear();
        if (!followedUsers.isEmpty()){
            for (String user : followedUsers){
                moodCollection.whereEqualTo("moodCreator",user)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    userPoints.put(doc.getId(), new Point(doc.getDouble("longitude"), doc.getDouble("latitude"), wgs84));
                                }
                            }
                        } else{
                            Log.d(TAG, "Failed to get user moods");
                        }
                    }
                });
            }
        }
    }


    private void displayMap(){
        mMapView = findViewById(R.id.mapView);
        ArcGISMap map =new ArcGISMap(Basemap.Type.STREETS, tempLat, tempLong, 30);
        mMapView.setMap(map);

        //set center of map
        mMapView.setViewpoint(new Viewpoint(new Point(tempLong, tempLat, wgs84), 3000));

        //init graphics overlay
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(graphicsOverlay);

        //symbol type for map marker
        SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10);

        /*
        Point point = new Point(lastLong, lastLat, wgs84);


        Graphic graphic = new Graphic(point, symbol);
        graphicsOverlay.getGraphics().add(graphic);

        */
    }

    @Override
    protected void onDestroy(){
        if(mMapView != null){
            mMapView.dispose();
        }
        super.onDestroy();
    }

}