package com.example.bigmood;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class testActivity extends BaseDrawerActivity{


    Button moodViewButton;
    public static ArrayList<Mood> moods;
    public static ArrayAdapter<Mood> moodArrayAdapter;
    ListView moodViews;
    LinearLayout moodContent;
    public static int index;
    FloatingActionButton addMood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_test, frameLayout);
        moods = new ArrayList<>();
        final Date date = Calendar.getInstance().getTime();
        moodViews = findViewById(R.id.mood_Content);
        moodArrayAdapter = new CustomArrayAdapter(moods,this);
        moodViews.setAdapter(moodArrayAdapter);
        moodContent = findViewById(R.id.linearLayout);
        addMood = findViewById(R.id.addMood);

        Mood happyMood = new Mood("Happy","Feeling good today","#ff0000",date);
        Mood sadMood = new Mood("Sad","Feeling sad today","#0054ff",date);
        Mood fearMood = new Mood("Afraid","Feeling afraid today","#1AFF00",date);
        Mood  loveMood= new Mood("Love","Feeling loved today","#FFFF00",date);
        Mood angryMood = new Mood("Angry","Feeling angry today","#CC00FF",date);
        /**
         * Adding all the mood objects in the array adapter
         */
        // commenting hard codes out for now
//        moods.add(happyMood);
//        moods.add(sadMood);
//        moods.add(fearMood);
//        moods.add(loveMood);
//        moods.add(angryMood);
        index = -1;
        /**
         * moodView is the list view and l
         */
        final String TAG ="Sample";
        moodViews.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(testActivity.this, ActivityAddMood.class);
                intent.putExtra("Mood",moods.get(i));
                startActivity(intent);
                index = i;
                return false;
            }

        });
        /**
         * Adding will put a new mood in the new activity
         */
        addMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(testActivity.this, ActivityAddMood.class);
                Mood mood = new Mood();
                intent.putExtra("Mood",mood);
                startActivity(intent);
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Moods");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                moods.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    //String moodTitle, String moodDescription, String moodColor, Date moodDate
                    Log.d(TAG, String.valueOf(doc.getData().get("moodTitle")));
                    String moodTitle = doc.getId();
                    String moodDescription = (String) doc.getData().get("moodDescription");
                    moods.add(new Mood(moodTitle, moodDescription, "#FFFF00",date));
                }
                moodArrayAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // to check current activity in the navigation drawer
        navigationView.getMenu().getItem(2).setChecked(true);
        index = -1;
    }
}
