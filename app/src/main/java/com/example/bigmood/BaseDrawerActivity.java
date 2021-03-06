/*
* This Activity is the layout of the whole app.
* In the center is a FrameLayout that inflates each Activity when it is pressed on the sidebar.
* */
package com.example.bigmood;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


/**
 *  Class for implementing a sidemenu for navigation. Connections friends, dashboard, My Profile, and map
 *  Requires USER_ID and User_Name as extras in intents.
 *
 *  USER_ID: User id of current user
 *  User_Name: Display name of current user
 */
public class BaseDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public FrameLayout frameLayout;
    public DrawerLayout drawer;
    public Toolbar toolbar;
    public NavigationView navigationView;
    public TextView nav_Username;
    public TextView nav_UserId;

    private String userID, username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_drawer);

        this.userID = getIntent().getExtras().getString("USER_ID");
        this.username = getIntent().getExtras().getString("User_Name");
        //Starts the toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.getTitle();
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.content_frame);

        //Build the navigation drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        nav_Username = headerView.findViewById(R.id.profile_username);
        nav_Username.setText(username);

        nav_UserId = headerView.findViewById(R.id.profile_userID);
        nav_UserId.setText(userID);

        //Start the app inflating the DashboardActivity
        if(toolbar.getTitle().toString().compareTo("BigMood") == 0){
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            intent.putExtra("USER_ID", userID);
            intent.putExtra("User_Name", username);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    //Handles what happens when a certain MenuItem is pressed
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        //To prevent current item select over and over
        if (menuItem.isChecked()){
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }

        if (id == R.id.nav_dashboard) {
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            intent.putExtra("USER_ID", userID);
            intent.putExtra("User_Name", username);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(getApplicationContext(), UserViewActivity.class);
            intent.putExtra("USER_ID", userID);
            intent.putExtra("TARGET_ID", userID);
            intent.putExtra("User_Name", username);
            startActivity(intent);
        } else if (id == R.id.nav_friends) {
            Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
            intent.putExtra("USER_ID", userID);
            intent.putExtra("User_Name", username);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(getApplicationContext(), GpsActivity.class);
            intent.putExtra("USER_ID", userID);
            intent.putExtra("User_Name", username);
            intent.putExtra("MODE", "USER");
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Handles when the back button is pressed
    @Override
    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Creates the overflow menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Handles the input to the Overflow menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.action_search:
                Intent intent = new Intent(getApplicationContext(), SearchUserActivity.class);
                intent.putExtra("USER_ID", userID);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}