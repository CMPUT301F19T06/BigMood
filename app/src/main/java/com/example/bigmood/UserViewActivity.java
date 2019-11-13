package com.example.bigmood;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/*  This activity displays a user who's id is passed to it in an intent.
 *  It also requires a permission flag to be passed to it since it is more
 *  convenient to acquire the flag from outside the activity.
 *
 *  If there is permission it displays the user's mood and hi
 */

public class UserViewActivity extends BaseDrawerActivity{
    protected String userId;
    protected static final String TAG = "USER_VIEW_ACTIVITY";
    protected boolean hasViewPermission = false;
    protected TextView userName;
    // for firestore stuff
    protected FirebaseFirestore db;
    protected CollectionReference userCollectionReference;
    protected CollectionReference moodCollectionReference;
    // for recyclerView stuff
    protected RecyclerView recyclerView;
    protected RecyclerViewAdapter adapter;
    protected ArrayList<Mood> moodObjects = new ArrayList<>();
    // for filters and sorts
    protected Spinner filterSpinner;
    protected Spinner sortSpinner;

    public UserViewActivity() {
        this.db = FirebaseFirestore.getInstance();
        this.userCollectionReference = db.collection("Users");
        this.moodCollectionReference = db.collection("Moods");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_view_user, frameLayout);
        toolbar.setTitle("UserView");

        this.userId = getIntent().getExtras().getString("TARGET_ID");
        this.hasViewPermission = getIntent().getExtras().getBoolean("HAS_VIEW_PERMISSON");

        this.userName = findViewById(R.id.user_view_username);
        this.recyclerView = findViewById(R.id.dashboard_recyclerview);

        this.getUserName();
        this.initAddFriend();
        this.initStartGpsView();
        if(hasViewPermission) {
            findViewById(R.id.user_view_add_friend).setVisibility(View.GONE);
            setMoodListener();
            initRecyclerView();
        }
    }

    protected void getUserName() {
        DocumentReference docRef = this.userCollectionReference.document(this.userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                        userName.setText(task.getResult().getString("displayName"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    protected void setMoodListener() {
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
    }

    protected void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");
        recyclerView = findViewById(R.id.dashboard_recyclerview);
        adapter = new RecyclerViewAdapter(moodObjects, this.userId, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void initStartGpsView() {
        findViewById(R.id.user_view_location_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: start gps activity
            }
        });
    }

    protected void initAddFriend() {
        findViewById(R.id.user_view_add_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: add a friend
            }
        });
    }
}
