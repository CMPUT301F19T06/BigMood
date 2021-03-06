package com.example.bigmood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 *  This activity displays a user who's id is passed to it in an intent.
 *  It also requires a permission flag to be passed to it since it is more
 *  convenient to acquire the flag from outside the activity.
 *
 *  If there is permission it displays the user's mood
 *
 *  Requires 2 fields in the intent: USER_ID, TARGET_ID
 *
 *  USER_ID: The user id of the current app user
 *  TARGET_ID: The user id of the user that will be viewed
 */

public class UserViewActivity extends BaseDrawerActivity
        implements AdapterView.OnItemSelectedListener, MultiSelectionSpinner.OnMultipleItemsSelectedListener {
    protected String targetUser;
    protected String currentUser;
    protected static final String TAG = "USER_VIEW_ACTIVITY";
    protected boolean hasViewPermission = false;
    protected TextView userName;
    // for firestore stuff
    protected FirebaseFirestore db;
    protected CollectionReference userCollectionReference;
    protected CollectionReference moodCollectionReference;
    // for recyclerView stuff
    protected RecyclerView recyclerView;
    protected RecyclerViewAdapter recyclerAdapter;
    protected ArrayList<Mood> moodObjects = new ArrayList<>();
    // for filters and sorts
    protected MultiSelectionSpinner filterSpinner;
    protected Spinner sortSpinner;
    protected int mode = 1;
    final static public int SORT_DATEDESC = 1;
    final static public int SORT_DATEASC = 2;
    ArrayList<String> moodFiltersToApply = new ArrayList<>();
    final static protected int MAX_FILTERS = 8;
    public static int index;

    public UserViewActivity() {
        this.db = FirebaseFirestore.getInstance();
        this.userCollectionReference = db.collection("Users");
        this.moodCollectionReference = db.collection("Moods");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_view_user, frameLayout);
        toolbar.setTitle("Profile");

        try {
            this.currentUser = getIntent().getExtras().getString("USER_ID");
            this.targetUser = getIntent().getExtras().getString("TARGET_ID");
            this.userCollectionReference.document(this.currentUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ArrayList<String> friends = (ArrayList<String>) documentSnapshot.get("userFriends");
                    if (friends.contains(targetUser)) {
                        viewMoods();
                    }
                }
            });
        } catch (NullPointerException e) {
            Log.d(TAG, "Could not acquire initial data");
            super.finish();
        }

        this.userName = findViewById(R.id.user_view_username);
        this.recyclerView = findViewById(R.id.dashboard_recyclerview);
        this.recyclerAdapter = new RecyclerViewAdapter(moodObjects, this.targetUser, this);
        this.getUserName();
        this.initAddFriend();
        this.initSortSpinner();
        this.initFilterSpinner();
        this.initStartGpsView();

        if (this.currentUser.compareTo(this.targetUser) == 0) {
            this.viewMoods();
            findViewById(R.id.user_view_location_pic).setVisibility(View.VISIBLE);
        }
    }

    protected void viewMoods() {
        findViewById(R.id.user_view_add_friend).setVisibility(View.GONE);
        findViewById(R.id.user_view_recycler).setVisibility(View.VISIBLE);
        setMoodListener();
        initRecyclerView();
    }

    protected void getUserName() {
        DocumentReference docRef = this.userCollectionReference.document(this.targetUser);
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
                updateSortAndFilter();
            }
        });
    }

    protected void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");
        recyclerView = findViewById(R.id.user_view_recycler);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void initStartGpsView() {
        ImageView imageView = findViewById(R.id.user_view_location_pic);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GpsActivity.class);
                intent.putExtra("USER_ID", currentUser);
                intent.putExtra("User_Name", userName.getText().toString());
                intent.putExtra("MODE", "USER");
                startActivity(intent);
            }
        });
    }


    protected void initAddFriend() {
        String targetUser = this.targetUser;
        final DocumentReference docRef = this.userCollectionReference.document(targetUser);
        final HashMap<String, Object> data = new HashMap<>();
        ImageView imageView = findViewById(R.id.user_view_add_friend);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create and send request
                docRef
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                List<String> tempReq = new ArrayList<String>();
                                if (documentSnapshot.contains("incomingReq")) {
                                    tempReq = (ArrayList<String>) documentSnapshot.get("incomingReq");
                                }
                                if (documentSnapshot.contains("userFriends")) {
                                    List<String> tempFriends = (ArrayList<String>) documentSnapshot.get("incomingReq");
                                    if (tempFriends.contains(currentUser)) {
                                        Toast.makeText(UserViewActivity.this, "You're already following!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                                if (!tempReq.contains(currentUser)) {
                                    tempReq.add(currentUser);
                                    data.put("incomingReq", tempReq);
                                    docRef.set(data, SetOptions.merge());
                                    Toast.makeText(UserViewActivity.this, "Sent follow request!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UserViewActivity.this, "Follow request already sent!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }



    protected void initSortSpinner() {
        // adapted from developer docs
        this.sortSpinner = findViewById(R.id.user_view_sort_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(UserViewActivity.this,
                R.array.userview_sortspinner, android.R.layout.simple_spinner_item);
        // Apply the adapter to the spinner
        this.sortSpinner.setAdapter(adapter);
        this.sortSpinner.setOnItemSelectedListener(this);
    }

    protected void initFilterSpinner() {
        List<String> items = new ArrayList<String>();
        items.add("Happy");
        items.add("Angry");
        items.add("Scared");
        items.add("Surprised");
        items.add("Sad");
        items.add("Disgusted");
        items.add("Bored");
        items.add("Touched");
        this.filterSpinner = findViewById(R.id.user_view_filter_spinner);
        this.filterSpinner.setItems(items);
        this.filterSpinner.setSelection(new int[]{0, 1, 2, 3, 4, 5, 6, 7});
        this.filterSpinner.setListener(this);
    }

    protected boolean filterDoc(DocumentSnapshot doc, ArrayList<String> filters) {
        if (!doc.contains("moodCreator")) {return false;}
        if (doc.getString("moodCreator").compareTo(targetUser) != 0) {return false;}
        if (filters.isEmpty()) {return true;}
        if (filters.contains(doc.getString("moodTitle"))) {
            return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String spinnerResult = (String) parent.getItemAtPosition(position);
        if (spinnerResult.compareTo("Date (Asc)") == 0) {
            mode = UserViewActivity.SORT_DATEASC;
        } else {
            mode = UserViewActivity.SORT_DATEDESC;
        }

        updateSortAndFilter();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    protected void updateSortAndFilter() {

        Query query = moodCollectionReference;

        /* TODO: sorting through query doesn't work
        if (mode == UserViewActivity.SORT_DATEDESC) {
            query.orderBy("moodDate", Query.Direction.DESCENDING);
        } else if (mode == UserViewActivity.SORT_DATEASC){
            query.orderBy("moodDate", Query.Direction.ASCENDING);
        }
        */
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                moodObjects.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (filterDoc(doc, moodFiltersToApply)) {
                        Log.d(TAG, String.valueOf(doc.getData().get("moodId")));
                        moodObjects.add(Mood.getFromDoc(doc));
                    }
                }
                sortMoods();
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }



    protected void sortMoods() {
        // Sort
        if (mode == UserViewActivity.SORT_DATEDESC) {
            moodObjects.sort(new Comparator<Mood>() {
                @Override
                public int compare(Mood mood1, Mood mood2) {
                    return mood2.getMoodDate().compareTo(mood1.getMoodDate());
                }
            });
        } else if (mode == UserViewActivity.SORT_DATEASC){
            moodObjects.sort(new Comparator<Mood>() {
                @Override
                public int compare(Mood mood1, Mood mood2) {
                    return mood1.getMoodDate().compareTo(mood2.getMoodDate());
                }
            });
        }
    }

    @Override
    public void selectedIndices(List<Integer> indices) {
        // do nothing
    }

    @Override
    public void selectedStrings(List<String> strings) {
        if(strings.size() >= 8) {
            moodFiltersToApply.clear();
        } else {
            for (String string : strings) {
                moodFiltersToApply.add(string);
            }
        }
        updateSortAndFilter();
    }

    @Override
    protected void onResume() {
        index = -1;
        super.onResume();
        navigationView.getMenu().getItem(1).setChecked(true);
    }
}
