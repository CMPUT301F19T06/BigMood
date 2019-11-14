package com.example.bigmood;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FriendRequestActivity extends BaseDrawerActivity {
    //TODO: clean up imports and variables
    private static final String TAG = "FRIENDREQUESTACTIVITY";
    private FirebaseFirestore db;
    private CollectionReference userCollectionReference;
    private CollectionReference friendsCollectionReference;
    private RecyclerView recyclerView;
    public static RecyclerViewAdapter adapter;
    public static ArrayList<String> friendObjects = new ArrayList<>();
    private String userId;
    private int startingIndex = 0;
    final private int queryLimit = 25;
    ImageView deleteMood;
    FloatingActionButton fab;
    public static int index;

    public FriendRequestActivity() {
        this.db = FirebaseFirestore.getInstance();
        this.userCollectionReference = db.collection("Users");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_friends, frameLayout);
        toolbar.setTitle("Follow Requests");

        this.userId = getIntent().getExtras().getString("USER_ID");

        this.recyclerView = findViewById(R.id.dashboard_recyclerview);
        this.userCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                friendObjects.clear();
                //TODO: implement follow requests from firebase
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
        //TODO: Load in Follow requests from Online.

        Log.d(TAG, "initRecyclerView: init recyclerview");
        recyclerView = findViewById(R.id.dashboard_recyclerview);
        adapter = new RecyclerViewAdapter(friendObjects, this.userId, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
