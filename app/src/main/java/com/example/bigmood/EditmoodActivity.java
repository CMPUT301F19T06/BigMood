package com.example.bigmood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class EditmoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editmood);

        //Get the moodObject out of the intent
        Intent intent = getIntent();
        moodObject mood = (moodObject) intent.getSerializableExtra("mood");

        //Create the toolbar object and set it to the action bar setSupportAction
        //Create mood setter
        //Create color setter
        //Create picture setter

        //Set up the Save Button
        findViewById(R.id.button_editmood_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });
    }
}
