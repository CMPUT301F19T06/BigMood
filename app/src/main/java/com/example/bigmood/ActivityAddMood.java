package com.example.bigmood;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import static com.example.bigmood.testActivity.index;
import static com.example.bigmood.testActivity.moodArrayAdapter;
import static com.example.bigmood.testActivity.moods;

/**
 * todo: Edit mood is using activity_add_mood as a layout
 */
public class ActivityAddMood extends AppCompatActivity {
    private Context context;
    TextView dateText, moodType, description;
    Button saveButton;
    LinearLayout profileBackground;
    ImageView profilePic;
    EditText moodTitle;
    String image;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:MM");
    Date date = Calendar.getInstance().getTime();
    String dayString = dateFormat.format(date);

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
        dateText = findViewById(R.id.currentDate);
        moodType = findViewById(R.id.currentMood);
        description = findViewById(R.id.moodDescription);
        moodTitle = findViewById(R.id.moodTitle);
        profileBackground = findViewById(R.id.background_pic);
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        dateText.setText(dateFormat.format(date));
        // object added to moods array adapter
        final Mood mood = (Mood)getIntent().getSerializableExtra("Mood");

        final CollectionReference collectionReference = db.collection("Moods");


        /**
         * todo: No error checks are done here, need to be done if a new mood is added
         */
        final String TAG = "Sample";

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //public Mood (String moodType, String moodDescription, String moodColor, Date moodDate){
                mood.setMoodTitle(moodTitle.getText().toString());
                mood.setMoodDescription(description.getText().toString());
                mood.setMoodColor("#FFFF00");
                mood.setMoodPhoto(image);
                HashMap<String, String> data = new HashMap<>();
                // date input given
                try{
                    mood.setMoodDate((dateFormat.parse(dateText.getText().toString())));

                }catch (ParseException e){
                    e.getStackTrace();
                }
                moodArrayAdapter.notifyDataSetChanged();
                moodArrayAdapter.add(mood);

                collectionReference
                        .document(mood.getMoodTitle())
                        .set(mood)
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
            }
        });

        /**
         * save button
         */

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCamera(view);
            }
        });
        //public Mood (String moodType, String moodDescription, String moodColor, Date moodDate){
        //todo: mood object gives a null object reference to be fixed
        // todo: I will probably need to separate add and edit to make things simpler



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
                dateText.setText(String.format("yyyy-mm-dd",year,month,day));
                try{
                    date = dateFormat.parse(dateText.getText().toString());

                }catch (ParseException e){
                    e.getStackTrace();
                }
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
            //moods.get(moods.size()).setMoodPhoto(temp);
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
