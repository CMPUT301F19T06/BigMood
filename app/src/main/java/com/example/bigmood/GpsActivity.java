package com.example.bigmood;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    /*
    The arraylist for the user's moods, initially null and only loaded from the database if
    the mode is set to "USER" and it is still null
     */
    private ArrayList<Point> userPoints = null;

    /*
    The arraylist holding the followed moods, this will need to be refreshed every time
    that "FOLLOW" mode is selected.
     */
    private ArrayList<Point> followedPoints;

    private FirebaseFirestore db;
    private CollectionReference moodCollection;

    //using esri mapView
    private MapView mMapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        this.userId = getIntent().getExtras().getString("USER_ID");
        this.mode = getIntent().getExtras().getString("MODE");


        this.db = FirebaseFirestore.getInstance();
        this.moodCollection = db.collection("Moods");

        if (this.mode.equals("USER")){
            retrieveUserMoods();
        }
        else{
            retrieveFollowedMoods();
        }

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
                return true;
            case R.id.gps_mode_menu_followed:
                retrieveFollowedMoods();
                //TODO: change display to followed moods
                Toast.makeText(this, "Display Followed", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;

        }

    }

    private void retrieveUserMoods(){
        //TODO: Retrieve the users moods
        // Point point = new Point(long, lat, SpatialReferences.getWgs84())
    }

    private void retrieveFollowedMoods(){
        //TODO: retrieve followed moods
    }
}
