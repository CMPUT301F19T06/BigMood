package com.example.bigmood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class EditmoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editmood);

        Intent intent = getIntent();
        moodObject mood = (moodObject) intent.getSerializableExtra("mood");
    }
}
