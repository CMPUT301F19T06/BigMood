package com.example.bigmood;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * The GpsActivity
 *
 * This activity is called from the sidebar and from the userview
 *
 * Passed in through intent:
 * "USER_ID" :  the id of the current user
 */

public class GpsActivity extends AppCompatActivity {

    String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        this.userId = getIntent().getExtras().getString("USER_ID");

        FloatingActionButton modeButton = findViewById(R.id.gps_button_mode);
        FloatingActionButton zoominButton = findViewById(R.id.gps_button_zoomin);
        FloatingActionButton zoomoutButton = findViewById(R.id.gps_button_zoomout);

        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: When the mode button is pressed
            }
        });

        zoominButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: When th zoom in button is pressed
            }
        });

        zoomoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: When the zoom out button is pressed
            }
        });


    }
}
