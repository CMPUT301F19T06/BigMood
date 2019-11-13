/*
* This Activity is the layout of the whole app.
* In the center is a FrameLayout that inflates each Activity when it is pressed on the sidebar.
* */
package com.example.bigmood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class BaseDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public FrameLayout frameLayout;
    public DrawerLayout drawer;
    public Toolbar toolbar;
    public NavigationView navigationView;
    private String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_drawer);

        this.userID = getIntent().getExtras().getString("USER_ID");

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

        //Start the app inflating the DashboardActivity
        if(toolbar.getTitle().toString() == getString(R.string.app_name)){
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            intent.putExtra("USER_ID", userID);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Toast.makeText(BaseDrawerActivity.this, "Not implemented yet! Your profile", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_friends) {
            Toast.makeText(BaseDrawerActivity.this, "Not implemented yet! Friends List", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(getApplicationContext(), GpsActivity.class);
            intent.putExtra("USER_ID", userID);
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
        return true;
    }

    //Handles the input to the Overflow menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}