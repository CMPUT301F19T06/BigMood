package com.example.bigmood;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 *  Allows a user to see their followed people and requests to follow them. They can choose to
 *  accept or decline a request. Takes inputs USER_ID.
 *
 *  USER_ID: User id of current user
 */
public class FriendsActivity extends BaseDrawerActivity {
    private static final String TAG = "FRIENDSACTIVITY";
    private FirebaseFirestore db;
    private CollectionReference userCollectionReference;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewReq;
    public FriendsRecyclerViewAdapter adapter;
    public FriendsRequestRecyclerViewAdapter adapterReq;
    public ArrayList<String> friendObjects = new ArrayList<>();
    public ArrayList<String> friendReq = new ArrayList<>();
    private String userId;
    private List userFriends;
    private List userFriendReqs;
    public static int index;

    private TextView emptyFriends;
    private TextView emptyFriendReqs;

    public FriendsActivity() {
        this.db = FirebaseFirestore.getInstance();
        this.userCollectionReference = db.collection("Users");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_friends, frameLayout);
        toolbar.setTitle("Friends");

        this.userId = getIntent().getExtras().getString("USER_ID");
        this.userCollectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            pullFriends();
            pullFriendReqs();
            adapter.notifyDataSetChanged();
        });


        this.recyclerView = findViewById(R.id.friends_recyclerview);
        this.recyclerViewReq = findViewById(R.id.friendReq_recyclerview);

        this.emptyFriendReqs = findViewById(R.id.friends_textView_emptyUser);
        this.emptyFriends = findViewById(R.id.friends_textView_emptyFriends);

        initRecyclerView();
    }

    @Override
    protected void onResume() {
        index = -1;
        super.onResume();
        navigationView.getMenu().getItem(2).setChecked(true);
    }

    // step 1: get all friends and shunt them into a list
    // step 2: realize there's only one step
    private void pullFriends() {
        final Query query = userCollectionReference.whereEqualTo("userId", this.userId);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            friendObjects.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                if (doc.contains("userFriends")) {
                    // This line might explode
                    // slam in a try/catch
                    Toast.makeText(FriendsActivity.this, "You have friends. Congrats.", Toast.LENGTH_SHORT).show();
                    userFriends = (List) doc.get("userFriends");
                    ArrayList<String> temp = new ArrayList<String>(userFriends);
                    friendObjects.addAll(temp);
                    Log.d(TAG, "initRecyclerViewFriends: Vibe Check"+friendObjects.toString());
                    adapter.notifyDataSetChanged();
                    emptyFriends.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    // no friends, you fucking loser
                    emptyFriends.setVisibility(View.VISIBLE);
                    Toast.makeText(FriendsActivity.this, "4ever alone", Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void pullFriendReqs() {
        final Query query = userCollectionReference.whereEqualTo("userId", this.userId);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            friendReq.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                if (doc.contains("incomingReq")) {
                    // This line might explode
                    // slam in a try/catch
                    userFriendReqs = (List) doc.get("incomingReq");
                    ArrayList<String> temp = new ArrayList<String>(userFriendReqs);
                    friendReq.addAll(temp);
                    Log.d(TAG, "initRecyclerViewFriends: Vibe Check"+friendReq.toString());
                    adapterReq.notifyDataSetChanged();
                    emptyFriendReqs.setVisibility(View.GONE);
                    recyclerViewReq.setVisibility(View.VISIBLE);
                } else {
                    // no friends, you fucking loser
                    emptyFriendReqs.setVisibility(View.VISIBLE);
                    Log.d(TAG, "initRecyclerViewFriendReq: No Friends.");
                    recyclerViewReq.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initRecyclerView() {
        recyclerViewReq = findViewById(R.id.friendReq_recyclerview);
        adapterReq = new FriendsRequestRecyclerViewAdapter(friendReq, this.userId, this);
        recyclerViewReq.setAdapter(adapterReq);
        recyclerViewReq.setLayoutManager(new LinearLayoutManager(this));


        Log.d(TAG, "initRecyclerViewFriends2: Vibe Check"+friendObjects.toString());
        recyclerView = findViewById(R.id.friends_recyclerview);
        adapter = new FriendsRecyclerViewAdapter(friendObjects, this.userId, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
