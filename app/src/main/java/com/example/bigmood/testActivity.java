package com.example.bigmood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class testActivity extends AppCompatActivity {
    Button moodViewButton;
    public static ArrayList<moodObject> moodObjects;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        moodViewButton = (Button)findViewById(R.id.button);
        moodObjects = new ArrayList<>();
        Date date = Calendar.getInstance().getTime();


        moodObject moodObject = new moodObject("Happy","Feeling good today",0xff0000,date);
        moodObjects.add(moodObject);
        moodViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(testActivity.this, "Cool!", Toast.LENGTH_SHORT).show();
                Intent moodView = new Intent(testActivity.this, ActivityMoodView.class);
                startActivity(moodView);

            }
        });
    }
}
