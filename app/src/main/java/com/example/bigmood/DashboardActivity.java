package com.example.bigmood;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DASHBOARDACTIVITY";
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<moodObject> moodObjects = new ArrayList<>();
    FloatingActionButton fab;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSideMenu();
            }
        });
        this.recyclerView = findViewById(R.id.dashboard_recyclerview);

        initRecyclerView();
    }

    private void startSideMenu() {
        //TODO: trigger side menu fragment
    }

    private void initRecyclerView(){
        //TODO: Load in Moods from Online.

        Log.d(TAG, "initRecyclerView: init recyclerview");
        recyclerView = findViewById(R.id.dashboard_recyclerview);
        adapter = new RecyclerViewAdapter(moodObjects, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
