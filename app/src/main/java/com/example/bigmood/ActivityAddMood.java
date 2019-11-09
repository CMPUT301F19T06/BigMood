package com.example.bigmood;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.spec.ECField;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.example.bigmood.ActivityMoodView.CAMERA_ACCESS;
import static com.example.bigmood.ActivityMoodView.GALLERY_ACCESS;

/**
 * todo: Activity add mood does both edit and add
 */
public class ActivityAddMood extends AppCompatActivity {
    private Context context;
    TextView dateText , description;
    Button saveButton;
    Button addLoc;
    LinearLayout profileBackground;
    ImageView profilePic;
    EditText moodTitle; // moodTitle and moodType is the same here for now
    String image;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:MM");
    Date date = Calendar.getInstance().getTime();
    String dayString = dateFormat.format(date);
    private FusedLocationProviderClient fusedLocationClient;
    private String userId;


    /**
     * firebase stuff here
     * todo: putting mood objects in firebase and generating them
     */

    private FirebaseFirestore db;
    private CollectionReference moodCollectionReference;
    private static final String AUTH_TAG = "Google auth failure";
    private static final String USER_EXISTS_TAG = "Current mood check";
    private static final String REGISTER_USER_TAG = "Registering new mood";

    public ActivityAddMood() {
        this.db = FirebaseFirestore.getInstance();
        // collection reference for moods
        this.moodCollectionReference = db.collection("Moods");
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // all the stuff id's
        setContentView(R.layout.activity_add_mood);
        profilePic = findViewById(R.id.Profile_image);
        saveButton = findViewById(R.id.save_button);
        addLoc = findViewById(R.id.add_loc);
        dateText = findViewById(R.id.currentDate);
        description = findViewById(R.id.moodDescription);
        moodTitle = findViewById(R.id.currentMood);
        profileBackground = findViewById(R.id.background_pic);
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        this.userId = getIntent().getExtras().getString("USER_ID");
        dateText.setText(dateFormat.format(date));
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // object added to moods array adapter

        final Mood mood = (Mood)getIntent().getSerializableExtra("Mood");
        // TODO: hardcoded color
        mood.setMoodColor("#FFFF00");

        final CollectionReference collectionReference = db.collection("Moods");

        if (mood.getMoodDate() == null) {
            mood.setMoodDate(Timestamp.now());
        }

        /**
         * todo: photo is not saved yet. Everything else shows
         * using index for now if a mood object is clicked on list
         */
        final String TAG = "Sample";
        // checking if it's an edit moof
        if (DashboardActivity.index != -1 ){
            dateText.setText(mood.getMoodDate().toString());
            moodTitle.setText(mood.getMoodTitle());
            description.setText(mood.getMoodDescription());
            String stringHEX = mood.getMoodColor();
            try {
                profileBackground.setBackgroundColor(Color.parseColor(stringHEX));
            }catch (Throwable e){
                e.printStackTrace();
            }

            //todo: String to bitmap
            try{
                byte [] encodeByte=Base64.decode(mood.getMoodPhoto(),Base64.DEFAULT);
                Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                profilePic.setImageBitmap(bitmap);
            }catch (Exception e){
                e.getMessage();
            }

        }
        /**
         * Save button to save mood object with it's requirements
         */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //public Mood (String moodType, String moodDescription, String moodColor, Date moodDate){
                mood.setMoodTitle(moodTitle.getText().toString());
                mood.setMoodDescription(description.getText().toString());
                mood.setMoodColor(mood.getMoodColor());
                mood.setMoodPhoto(image);
                // date input given
                try{
                    mood.setMoodDate(new Timestamp((dateFormat.parse(dateText.getText().toString()))));

                }catch (ParseException e){
                    e.getStackTrace();
                }
                mood.setMoodID(String.valueOf(Timestamp.now().hashCode()));
                HashMap<String, Object> data = new HashMap<>();
                data.put("moodTitle", mood.getMoodTitle());
                data.put("moodDescription", mood.getMoodDescription());
                data.put("moodColor", mood.getMoodColor());
                data.put("moodPhoto", mood.getMoodPhoto());
                data.put("moodDate", mood.getMoodDate());
                data.put("dateCreated", Timestamp.now());
                data.put("dateUpdated", Timestamp.now());
                data.put("moodId", mood.getMoodID());
                data.put("moodCreator", userId);
                //TODO: replace with enum for social situation
                data.put("moodSituation", 0);
                data.put("longitude", mood.getLongitude());
                data.put("latitude", mood.getLatitude());
                try{
                    collectionReference
                            .document(mood.getMoodID())
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"Data addition successful");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Data addition failed" + e.toString());
                                }
                            });
                    finish();
                } catch (Exception e){
                    Toast.makeText(context, "You haven't put a title for your mood",Toast.LENGTH_LONG).show();
                }

            }
        });


        addLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null){
                            mood.setLongitude(location.getLongitude());
                            mood.setLatitude(location.getLatitude());
                        } else {
                            Toast.makeText(context, "Location error",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCamera(view);
            }
        });
        /**
         * date picker
         */
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        ActivityAddMood.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        // date picker format
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                dateText.setText(String.format("%02d-%02d-%02d",year,month,day));

            }
        };
    }
    /**
     * working on the open camera and open album functionality
     */
    public void OpenCamera(View view){
        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA_ACCESS);
    }
    public void OpenAlbum(View view){
        Intent intent =  new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_ACCESS);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        context = getApplicationContext();
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CAMERA_ACCESS && resultCode == RESULT_OK)    {
            Bitmap bitmap= (Bitmap) data.getExtras().get("data");
            // todo: working on image compression
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b =baos.toByteArray();
            String temp=Base64.encodeToString(b, Base64.DEFAULT);
            //moods.get(index).setMoodPhoto(temp);
            image = temp;
            profilePic.setImageBitmap(bitmap);
        }

        else if(requestCode==GALLERY_ACCESS){
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profilePic.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(context, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}
