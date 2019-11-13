package com.example.bigmood;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;

import static java.sql.Types.NULL;

public class DashboardActivity extends BaseDrawerActivity {
    private static final String TAG = "DASHBOARDACTIVITY";
    private FirebaseFirestore db;
    private CollectionReference userCollectionReference;
    private CollectionReference moodCollectionReference;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<Mood> moodObjects = new ArrayList<>();
    private String userId;
    private int startingIndex = 0;
    final private int queryLimit = 25;
    ImageView deleteMood;
    FloatingActionButton fab;
    public static int index;

    public DashboardActivity() {
        this.db = FirebaseFirestore.getInstance();
        this.userCollectionReference = db.collection("Users");
        this.moodCollectionReference = db.collection("Moods");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_dashboard, frameLayout);
        toolbar.setTitle("Dashboard");

        this.userId = getIntent().getExtras().getString("USER_ID");

        //TODO: get items from database using USER_ID

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Cool!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DashboardActivity.this, ActivityAddMood.class);
                intent.putExtra("USER_ID", userId);
                Mood mood = new Mood();
                intent.putExtra("Mood",mood);
                startActivity(intent);
            }
        });



        this.recyclerView = findViewById(R.id.dashboard_recyclerview);
        this.moodCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                moodObjects.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.get("moodCreator") != null){

                        if (doc.getString("moodCreator").compareTo(userId) == 0) {
                            Log.d(TAG, String.valueOf(doc.getData().get("moodId")));
                            String moodId = doc.getId();
                            String moodDescription = doc.getString("moodDescription");
                            String moodTitle = doc.getString("moodTitle");
                            Timestamp moodDate = doc.getTimestamp("moodDate");
                            String moodColor = doc.getString("moodColor");
                            String moodPhoto = (String) doc.getData().get("moodPhoto");
                            Mood mood = new Mood();
                            mood.setMoodID(moodId);
                            mood.setMoodTitle(moodTitle);
                            mood.setMoodDescription(moodDescription);
                            mood.setMoodDate(moodDate);
                            mood.setMoodColor(moodColor);
                            mood.setMoodPhoto(moodPhoto);
                            moodObjects.add(mood);
                        }

                    }


                }
                adapter.notifyDataSetChanged();
            }
        });




        initRecyclerView();
    }

    @Override
    protected void onResume() {
        index = -1;
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void initRecyclerView() {
        //TODO: Load in Moods from Online.

        Log.d(TAG, "initRecyclerView: init recyclerview");
        recyclerView = findViewById(R.id.dashboard_recyclerview);
        adapter = new RecyclerViewAdapter(moodObjects, this.userId, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}