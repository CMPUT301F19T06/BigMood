package com.example.bigmood;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.util.ArrayList;

public class DashboardActivity extends BaseDrawerActivity {
    private static final String TAG = "DASHBOARDACTIVITY";
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<Mood> moodObjects = new ArrayList<>();
    private String userID;
    FloatingActionButton fab;
    Button moodViewButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_dashboard, frameLayout);
        toolbar.setTitle("Dashboard");

        this.userID = getIntent().getExtras().getString("USER_ID");

        //TODO: get items from database using USER_ID

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Cool!", Toast.LENGTH_SHORT).show();

                Intent testActivity = new Intent(DashboardActivity.this, com.example.bigmood.testActivity.class);
                startActivity(testActivity);
            }
        });
        this.recyclerView = findViewById(R.id.dashboard_recyclerview);

        moodViewButton = (Button) findViewById(R.id.button);
        moodViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Cool!", Toast.LENGTH_SHORT).show();
                Intent testView = new Intent(DashboardActivity.this, testActivity.class);
                startActivity(testView);
            }
        });

        initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void initRecyclerView() {
        //TODO: Load in Moods from Online.

        Log.d(TAG, "initRecyclerView: init recyclerview");
        recyclerView = findViewById(R.id.dashboard_recyclerview);
        adapter = new RecyclerViewAdapter(moodObjects, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}