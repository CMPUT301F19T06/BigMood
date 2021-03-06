package com.example.bigmood;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuInflater;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedEvent;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedListener;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.util.ListChangedEvent;
import com.esri.arcgisruntime.util.ListChangedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.internal.Platform;

import static com.esri.arcgisruntime.symbology.PictureMarkerSymbol.createAsync;


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

public class GpsActivity extends AppCompatActivity{

    private String userId; //The Current user's id

    private double lastLong;
    private double lastLat;
    private double tempLong = -113.52705;
    private double tempLat = 53.52679;
    //spatial reference for map points
    private SpatialReference wgs84 = SpatialReferences.getWgs84();
    ///////////////////
    private String TAG = "GpsActivity";


    //hashmap for mood ID and map points
    private HashMap<String, Point> userPoints;

    private HashMap<String, Mood> userMoods;

    private HashMap<String, Point> personalPoints;
    private HashMap<String, Mood> personalMoods;
    private HashMap<String, Point> followedPoints;
    private HashMap<String, Mood> followedMoods;

    private boolean personal;


    private FirebaseFirestore db;
    private CollectionReference moodCollection;

    private ArrayList<String> followedUsers;

    //using esri mapView
    private MapView mMapView;
    private GraphicsOverlay graphicsOverlay;
    private android.graphics.Point newPoint;
    private Point selectedPoint;
    private String selectedID;
    private Mood selectedMood;
    private LocationListener locationListener;
    private Criteria criteria;
    private LocationManager locationManager;
    private Looper looper;

    //private BitmapDrawable emoji;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        //get last location
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //mloc = location;
                Log.d("Location Changes", location.toString());
                lastLong = location.getLongitude();
                lastLat = location.getLatitude();
                mMapView.setViewpoint(new Viewpoint(new Point(lastLong, lastLat, wgs84), 3000));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Status Changed", String.valueOf(status));
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Provider Enabled", provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Provider Disabled", provider);
            }
        };

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        looper = null;
        ////////////////////////////////


        this.userId = getIntent().getExtras().getString("USER_ID");
        String mode = getIntent().getExtras().getString("MODE");


        this.db = FirebaseFirestore.getInstance();
        this.moodCollection = db.collection("Moods");

        this.userPoints = new HashMap<>();
        this.userMoods = new HashMap<>();
        this.personalPoints = new HashMap<>();
        this.personalMoods = new HashMap<>();
        this.followedPoints = new HashMap<>();
        this.followedMoods = new HashMap<>();

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
                Log.d(TAG, "Friends in listener: " + String.valueOf(followedUsers.size()));
                retrieveFollowedMoods();
            }
        });

        displayMap();
        Log.d(TAG, "Friends out of listener: " + String.valueOf(followedUsers.size()));

        db.collection("Mood").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for( QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    retrieveUserMoods();
                    retrieveFollowedMoods();
                }
            }
        });

        retrieveUserMoods();

        if (mode.equals("USER")){
            switchUserMoods();
        }
        else{
            switchFollowedMoods();
        }

        final FloatingActionButton modeButton = findViewById(R.id.gps_button_mode);

        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: When the mode button is pressed
                PopupMenu modemenu = new PopupMenu(getApplicationContext(), view);
                modemenu.inflate(R.menu.gps_mode_menu);
                modemenu.show();

                modemenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    Activity#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.
                            ActivityCompat.requestPermissions(GpsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                        }
                        switch (item.getItemId()){

                            case R.id.gps_mode_menu_user:

                                switchUserMoods();
                                //Toast.makeText(GpsActivity.this, String.valueOf(userPoints.size()), Toast.LENGTH_LONG).show();
                                return true;
                            case R.id.gps_mode_menu_followed:
                                switchFollowedMoods();
                                return true;
                            default:
                                return false;

                        }
                    }
                });
            }
        });

        //call to display map


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(GpsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }
        locationManager.requestSingleUpdate(criteria, locationListener, looper);
        //setGraphics();
        /////////////////////
    }

    private void switchUserMoods(){
        userPoints = personalPoints;
        userMoods = personalMoods;
        personal = true;
        for (Mood mood : userMoods.values()){
            Log.d(TAG, "Mood in map: " + mood.getMoodID());
        }
        setGraphics();
    }

    private void switchFollowedMoods(){
        userPoints = followedPoints;
        userMoods = followedMoods;
        personal = false;
        for (Mood mood : userMoods.values()){
            Log.d(TAG, "Mood in map: " + mood.getMoodID());
        }
        setGraphics();
    }

    private void retrieveUserMoods(){
        //TODO: Retrieve the users moods

        personalPoints.clear();
        personalMoods.clear();
        moodCollection.whereEqualTo("moodCreator",userId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            personalPoints.put(doc.getId(), new Point(doc.getDouble("longitude"), doc.getDouble("latitude"), wgs84));
                            personalMoods.put(doc.getId(), Mood.getFromDoc(doc));

                        }
                    }
                } else{
                    Log.d(TAG, "Failed to get user moods");
                }
                Log.d(TAG, "User moods retrieved:");
                setGraphics();
            }
        });
    }

    private void retrieveFollowedMoods(){
        //TODO: retrieve followed moods
        followedPoints.clear();
        followedMoods.clear();
        if (!followedUsers.isEmpty()){
            userPoints.clear();
            userMoods.clear();
            for (String user : followedUsers){
                moodCollection.whereEqualTo("moodCreator",user)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (!task.getResult().isEmpty()) {
                                ArrayList<DocumentSnapshot> temp = new ArrayList<>();
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    temp.add(doc);
                                }
                                temp.sort(new Comparator<DocumentSnapshot>() {
                                    @Override
                                    public int compare(DocumentSnapshot doc1, DocumentSnapshot doc2) {
                                        return ((Timestamp)doc2.get("moodDate")).compareTo((Timestamp)doc1.get("moodDate"));
                                    }
                                });
                                followedPoints.put(temp.get(0).getId(), new Point(temp.get(0).getDouble("longitude"), temp.get(0).getDouble("latitude"), wgs84));
                                followedMoods.put(temp.get(0).getId(), Mood.getFromDoc(temp.get(0)));
                            }
                            //setGraphics();
                        } else{
                            Log.d(TAG, "Failed to get friend moods");
                        }
                        //userPoints.clear();
                        //userMoods.clear();
                        Log.d(TAG, "Followed moods retrieved:");
                        setGraphics();
                    }
                });
            }
            //setGraphics();
        }
    }

    /**
     *  Inflate and work with the map view
     */
    private void displayMap(){
        mMapView = findViewById(R.id.mapView);
        ArcGISMap map = new ArcGISMap(Basemap.Type.STREETS, lastLat, lastLong, 30);
        //inflate map
        mMapView.setMap(map);

        map.setMaxScale(1000);
        map.setMinScale(25000);

        graphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(graphicsOverlay);

        mMapView.setOnTouchListener(new mapOnTouchCustom(this, mMapView));
    }

    /**
     * show gps points of moods on map
     */
    private void setGraphics(){
        Log.d(TAG, "setGraphics: " + String.valueOf(userPoints.size()));

        graphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().set(0,graphicsOverlay);

        graphicsOverlay.getGraphics().addListChangedListener(new ListChangedListener<Graphic>() {
            @Override
            public void listChanged(ListChangedEvent<Graphic> listChangedEvent) {
                Log.d(TAG, "Graphics changed: " + listChangedEvent.getItems().size());
            }
        });

        for(Map.Entry<String, Point> entry : userPoints.entrySet()){
            Log.d(TAG, "setGraphics: entry: " + entry.getKey());
            Mood temp = userMoods.get(entry.getKey());
            Log.d(TAG, "setGraphics: entry (lat long): " + entry.getValue().toString());
            SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.parseColor(temp.getMoodColor()), 16);
            TextSymbol textSymbol = new TextSymbol(16, temp.getMoodUsername() + ": " + temp.getMoodTitle(), Color.BLUE, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);

            if (personal){
                textSymbol = new TextSymbol(16, temp.getMoodTitle(),Color.BLUE, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
            }
            Graphic graphic = new Graphic(entry.getValue(), symbol);
            Graphic txtGraphic = new Graphic(entry.getValue(), textSymbol);

            graphicsOverlay.getGraphics().add(graphic);
            graphicsOverlay.getGraphics().add(txtGraphic);
        }
    }

    /**
     * Identify which point user clicked
     */
    private void identifyGraphics(){
        ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphic =
                mMapView.identifyGraphicsOverlayAsync(graphicsOverlay, newPoint, 10, false, 1);


        identifyGraphic.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try{

                    IdentifyGraphicsOverlayResult overlayResult = identifyGraphic.get();
                    //get list of graphics
                    List<Graphic> graphic = overlayResult.getGraphics();
                    if (!graphic.isEmpty()){

                        selectedMood = null;
                        selectedID = null;
                        selectedPoint = null;

                        for (Graphic g : graphic){
                            selectedPoint = (Point) g.getGeometry();
                            getSelectedMoodID();
                            getSelectedMood();
                            displayMood();

                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        });
    }

    /**
     *  find mood ID from user selected point
     */
    private void getSelectedMoodID(){
        for(Map.Entry<String, Point> entry : userPoints.entrySet()){
            if(Objects.equals(selectedPoint, entry.getValue())){
                selectedID = entry.getKey();
                return;
            }
        }
    }

    /**
     *  get mood object for information to display
     */
    private void getSelectedMood(){
        selectedMood = userMoods.get(selectedID);
        return;
    }

    //this might work
    private void displayMood(){

        Intent intent = new Intent(GpsActivity.this, ActivityMoodView.class);
        intent.putExtra("USER_ID", selectedMood.getMoodUsername());
        //Mood mood = new Mood();
        //mood.setMoodUsername(getUsername());
        intent.putExtra("Mood",selectedMood);
        startActivity(intent);
    }

    /**
     * delete the map when user leaves gps mode
     */
    @Override
    protected void onDestroy(){
        if(mMapView != null){
            mMapView.dispose();
        }
        super.onDestroy();
    }

    /**
     * Custom on touch listener for map view
     */
    class mapOnTouchCustom extends DefaultMapViewOnTouchListener{

        public mapOnTouchCustom(Context context, MapView mapView){
            super(context, mapView);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event){
            newPoint = new android.graphics.Point((int) event.getX(), (int) event.getY());
            identifyGraphics();
            return true;
        }
    }
}