package com.example.bigmood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        Date date = Calendar.getInstance().getTime();
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
        moods.add(happyMood);
        moods.add(sadMood);
        moods.add(fearMood);
        moods.add(loveMood);
        moods.add(angryMood);
        index = -1;
        moodViews.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(testActivity.this, EditmoodActivity.class);
                startActivity(intent);
                index = i;
                return false;
            }
        });
        addMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(testActivity.this, EditmoodActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // to check current activity in the navigation drawer
        navigationView.getMenu().getItem(2).setChecked(true);
    }
}
