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

import androidx.annotation.Nullable;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 *  Class designed to be the "home" screen of application. Allows a user to see their last
 *  posted mood and the last posted mood of each of their friends.
 *
 *  Takes in 2 inputs, USER_ID and User_Name
 *
 *  USER_ID: User id of current user
 *  User_Name: Display name of current user
 */
public class DashboardActivity extends BaseDrawerActivity {
    private static final String TAG = "DASHBOARDACTIVITY";
    private FirebaseFirestore db;
    private CollectionReference userCollectionReference;
    private CollectionReference moodCollectionReference;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerViewUser;
    private RecyclerViewAdapter adapterUser;
    private ArrayList<Mood> moodObjects = new ArrayList<>();
    private ArrayList<Mood> moodObjectsUser = new ArrayList<>();
    private String userId, username;
    private int startingIndex = 0;
    final private int queryLimit = 25;
    ImageView deleteMood;
    FloatingActionButton fab;
    public static int index;
    private List userFriends;
    private TextView emptyUser;
    private TextView emptyFriends;

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
        this.username = getIntent().getExtras().getString("User_Name");

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Cool!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DashboardActivity.this, ActivityAddMood.class);
                intent.putExtra("USER_ID", userId);
                String date = Timestamp.now().toDate().toString();
                intent.putExtra("DATE", date);
                Mood mood = new Mood(userId);
                mood.setMoodUsername(getUsername());
                intent.putExtra("Mood",mood);
                intent.putExtra("EDIT","AddingMode");
                startActivity(intent);
            }
        });


        this.recyclerView = findViewById(R.id.dashboard_recyclerview);
        this.moodCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                pullCurrentUserTopMood();
                pullGetFriendMoods();
            }
        });

        this.recyclerViewUser = findViewById(R.id.dashboard_recyclerviewUser);
        this.emptyUser = findViewById(R.id.dashboard_textView_emptyUser);
        this.emptyFriends = findViewById(R.id.dashboard_textView_emptyFriends);

        initRecyclerView();
    }


    public String getUsername(){
        return this.username;
    }

    @Override
    protected void onResume() {
        index = -1;
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");
        recyclerView = findViewById(R.id.dashboard_recyclerview);
        adapter = new RecyclerViewAdapter(moodObjects, this.userId, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewUser = findViewById(R.id.dashboard_recyclerviewUser);
        adapterUser = new RecyclerViewAdapter(moodObjectsUser, this.userId, this);
        recyclerViewUser.setAdapter(adapterUser);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(this));
    }

    // step 1: get all friends of user
    // step 2: get most recent mood from user
    private void pullGetFriendMoods() {
        final Query query = userCollectionReference.whereEqualTo("userId", this.userId);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                moodObjects.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.contains("userFriends")) {
                        // This line might explode
                        userFriends = (List) doc.get("userFriends");
                        emptyFriends.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        for (Object obj : userFriends) {
                            // This line might explode
                            String user = (String) obj;
                            pullTopMood(user);
                        }
                    } else {
                        emptyFriends.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }

            }
        });
    }

    private void pullTopMood(String friendId) {
        Query query = moodCollectionReference
                .whereEqualTo("moodCreator", friendId);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()){
                    return;
                }
                ArrayList<Mood> temp = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Mood mood = Mood.getFromDoc(doc);
                    temp.add(mood);
                }
                moodObjects.add(getTopOne(temp));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void pullCurrentUserTopMood() {
        Query query = moodCollectionReference.whereEqualTo("moodCreator", this.userId);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                moodObjectsUser.clear();
                ArrayList<Mood> temp = new ArrayList<>();
                if (queryDocumentSnapshots.isEmpty()) {
                    emptyUser.setVisibility(View.VISIBLE);
                    recyclerViewUser.setVisibility(View.GONE);
                    return;
                } else {
                    emptyUser.setVisibility(View.GONE);
                    recyclerViewUser.setVisibility(View.VISIBLE);
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    temp.add(Mood.getFromDoc(doc));
                }
                moodObjectsUser.add(getTopOne(temp));
                adapterUser.notifyDataSetChanged();
            }
        });
    }

    private Mood getTopOne(ArrayList<Mood> list) {
        list.sort(new Comparator<Mood>() {
            @Override
            public int compare(Mood mood1, Mood mood2) {
                return mood2.getMoodDate().compareTo(mood1.getMoodDate());
            }
        });
        return list.get(0);
    }
}
