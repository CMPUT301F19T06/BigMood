package com.example.bigmood;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * This class takes care of displaying a mood event
 */
public class ActivityMoodView extends AppCompatActivity {
    private static final String TAG = "ActivityMoodView";

    TextView dateText, moodUserName;
    TextView description;
    Button editButton;
    Button addLoc;
    ImageView profileBackground;
    ImageView profilePic, emojiPic;

    TextView moodTitle, moodSituation;
    String image;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:MM");
    Date date = Calendar.getInstance().getTime();
    String dayString = dateFormat.format(date);
    private FusedLocationProviderClient fusedLocationClient;
    private String userId, username;


    private FirebaseFirestore db;
    private CollectionReference moodCollectionReference;
    private CollectionReference userCollectionReference;

    public ActivityMoodView() {
        this.db = FirebaseFirestore.getInstance();
        // collection reference for moods
        this.moodCollectionReference = db.collection("Moods");
        this.userCollectionReference = db.collection("Users");
    }



    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // all the stuff id's
        setContentView(R.layout.activity_mood_view);
        editButton = findViewById(R.id.edit_button);
        addLoc = findViewById(R.id.add_loc);

        dateText = findViewById(R.id.currentDate);
        description = findViewById(R.id.moodDescription_view);
        moodTitle = findViewById(R.id.currentMood);
        moodUserName = findViewById(R.id.moodUserName);
        moodSituation = findViewById(R.id.moodSituationSpinner);
        profileBackground = findViewById(R.id.background_pic);
        emojiPic = findViewById(R.id.currentMoodImage);
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.userId = getIntent().getExtras().getString("USER_ID");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final Mood mood = (Mood) getIntent().getSerializableExtra("Mood");
        final String date = (String) getIntent().getExtras().getString("DATE");
        Date parsedDate = new Date();
        try {
            parsedDate = dateFormat.parse(date);

            Timestamp timestamp = new Timestamp(parsedDate);
            mood.setMoodDate(timestamp);
        } catch(Exception e) { //this generic but you can control another types of exception
            e.printStackTrace();
        }
        dateText.setText(dateFormat.format(parsedDate));
        Toast.makeText(getApplicationContext(),date,Toast.LENGTH_LONG);
        moodUserName.setText(mood.getMoodUsername());
        moodSituation.setText(mood.getMoodSituation());
        moodTitle.setText(mood.getMoodTitle());
        android.text.format.DateFormat df = new android.text.format.DateFormat();

        description.setEnabled(false);
        description.setText(mood.getMoodDescription());
        byte[] decodedByte = Base64.decode(mood.getMoodEmoji(), 0);
        Bitmap image = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        setMoodEmoji(mood.getMoodTitle());
        emojiPic.setColorFilter(Color.parseColor(mood.getMoodColor()), PorterDuff.Mode.MULTIPLY);
        // setting the profile background here
        try {
            byte[] encodeByte = Base64.decode(mood.getMoodPhoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            profileBackground.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.getMessage();
        }

        /**
         * Edit button to edit mood object with it's requirements
         */
        if(userId.equals(mood.getMoodCreator())) {

            editButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent editMood = new Intent(ActivityMoodView.this, ActivityAddMood.class);
                    editMood.putExtra("EDIT","EditingMode");
                    editMood.putExtra("USER_ID", userId);
                    editMood.putExtra("Mood", mood);
                    editMood.putExtra("DATE",date);
                    startActivity(editMood);
                }
            });
        }
        else{
            editButton.setVisibility(View.INVISIBLE);
        }
        /**
         * get the background pic and set it to the mood Photo
         */
        ImageView backgroundFull = findViewById(R.id.background_pic_Full);
        try {
            byte[] encodeByte = Base64.decode(mood.getMoodPhoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            backgroundFull.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.getMessage();
        }

        //Expands the background image by setting the fullscreen image of the background to VISIBLE
        profileBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundFull.setVisibility(View.VISIBLE);
                profileBackground.setVisibility(View.INVISIBLE);
            }
        });
        //Puts the fullscreen image back to the state GONE
        backgroundFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundFull.setVisibility(View.GONE);
                profileBackground.setVisibility(View.VISIBLE);

            }
        });
    }

    /**
     * Set emoji according to mood type
     * @param emotion
     */
    // todo: set emoji according to hashmap of mood type
    public void setMoodEmoji(String emotion){
        switch (emotion){
            case "Happy":
                emojiPic.setImageResource(R.drawable.emoji_happy);
                break;
            case "Sad":
                emojiPic.setImageResource(R.drawable.emoji_sad);
                break;
            case "Scared":
                emojiPic.setImageResource(R.drawable.emoji_fear);
                break;
            case "Surprised":
                emojiPic.setImageResource(R.drawable.emoji_surprised);
                break;
            case "Angry":
                emojiPic.setImageResource(R.drawable.emoji_angry);
                break;
            case "Bored":
                emojiPic.setImageResource(R.drawable.emoji_bored);
                break;
            case "Disgusted":
                emojiPic.setImageResource(R.drawable.emoji_disgust);
                break;
            case "Touched":
                emojiPic.setImageResource(R.drawable.emoji_love);
                break;
        }
    }

}