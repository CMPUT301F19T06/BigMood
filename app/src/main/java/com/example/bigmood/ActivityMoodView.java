package com.example.bigmood;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;


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
    /**
     * todo: working on converting URL's to images
     */
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    public static final int CAMERA_ACCESS = 1001;
    public static final int GALLERY_ACCESS = 9999;
    private Context context;
    TextView dateText, description, moodUserName;
    Button editButton;
    Button addLoc;
    ImageView profileBackground;
    ImageView profilePic, deleteMood, emojiPic;
    TextView moodTitle, moodSituation; // moodTitle and moodType is the same here for now
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
        profilePic = findViewById(R.id.Profile_image);
        editButton = findViewById(R.id.edit_button);
        addLoc = findViewById(R.id.add_loc);
        dateText = findViewById(R.id.currentDate);
        description = findViewById(R.id.moodDescription);
        moodTitle = findViewById(R.id.currentMood);
        moodUserName = findViewById(R.id.moodUserName);
        // todo: set image from URL
        //profilePic.setImageBitmap(getBitmapFromURL("https://drive.google.com/open?id=1FXlozKQrb4QoNWPYfSfsKb0AeaQ5Ocle"));

        moodSituation = findViewById(R.id.moodSituationSpinner);
        profileBackground = findViewById(R.id.background_pic);
        emojiPic = findViewById(R.id.currentMoodImage);
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.userId = getIntent().getExtras().getString("USER_ID");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final Mood mood = (Mood) getIntent().getSerializableExtra("Mood");

        moodUserName.setText(mood.getMoodUsername());
        moodSituation.setText("COMMUNITY: \n" + mood.getMoodSituation());
        moodTitle.setText(mood.getMoodTitle());
        // todo: moodDate does not work
        if (mood.getMoodDate() == null) {
            mood.setMoodDate(Timestamp.now());
        }
        dateText.setText(mood.getMoodDate().toDate().toString());
        description.setText(mood.getMoodDescription());
        setMoodEmoji(mood.getMoodTitle());

        try {
            byte[] encodeByte = Base64.decode(mood.getMoodPhoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

            //todo: use profile pic from google
            //profilePic.setImageBitmap(bitmap);
            profileBackground.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.getMessage();
        }

        /**
         * Edit button to edit mood object with it's requirements
         */

        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent editMood = new Intent(ActivityMoodView.this, ActivityAddMood.class);
                editMood.putExtra("USER_ID", userId);
                editMood.putExtra("Mood",mood);
                startActivity(editMood);
            }
        });

    }

    /**
     * Some new stuff
     * todo: finish this task
     */
    private void getImages(){
        Log.d("Bomb", "initImageBitmaps: preparing bitmaps.");

        mImageUrls.add("https://drive.google.com/open?id=1YOIVRtEo1jOg9Q5TOYJJvLiXv_j_y8wQ");
        mNames.add("Bored");

        mImageUrls.add("https://drive.google.com/open?id=1v9CFZFzLqlFXkV2Oum6mKuzoAK3C9Fj9");
        mNames.add("Angry");

        mImageUrls.add("https://drive.google.com/open?id=1lHlkIzHNgvZ5rNKiGGBwtE2jhEl_-MCR");
        mNames.add("Disgust");

        mImageUrls.add("https://drive.google.com/open?id=1y8dg1_srfSdExr6d75WpL2dvPO9PByY4");
        mNames.add("Fear");


        mImageUrls.add("https://drive.google.com/open?id=1mSv_ywdMi0m1gS9X1SyTO2T4yMqR8bND");
        mNames.add("Happy");

        mImageUrls.add("https://drive.google.com/open?id=1GV9j63lW0P4qA2E6944cGwHSVM7CCPJ6");
        mNames.add("Love");


        mImageUrls.add("https://drive.google.com/open?id=17tPsqGny-S03sOk5Z0zaj7P3GRlEf6ll");
        mNames.add("Sad");

        mImageUrls.add("https://drive.google.com/open?id=1FXlozKQrb4QoNWPYfSfsKb0AeaQ5Ocle");
        mNames.add("Surprised");

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
        mNames.add("Washington");

        //initRecyclerView();

    }

    // todo: get emoji from URL
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


    /**
     * Set emoji according to mood type
     * @param emotion
     */
    // todo: set emoji according to hashmap of mood type
    public void setMoodEmoji(String emotion){
        switch (emotion){
            case "Happy":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_happy));
                break;
            case "Sad":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_sad));
                break;
            case "Fear":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_fear));
                break;
            case "Surprise":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_surprised));
                break;
            case "Anger":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_angry));
                break;
            case "Bored":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_bored));
                break;
            case "Disgust":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_disgust));
                break;
            case "Love":
                emojiPic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.emoji_love));
                break;
        }
    }

    public String getMoodEmoji(){
        Drawable drawable= emojiPic.getDrawable();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b =baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


}