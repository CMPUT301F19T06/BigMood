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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.bigmood.ActivityMoodView.CAMERA_ACCESS;
import static com.example.bigmood.ActivityMoodView.GALLERY_ACCESS;
import static com.example.bigmood.testActivity.index;
import static com.example.bigmood.testActivity.moodArrayAdapter;
import static com.example.bigmood.testActivity.moods;

/**
 * important: this is not used
 * todo: Edit mood activity is not being used
 */
public class EditmoodActivity extends AppCompatActivity {
    private Context context;
    TextView dateText, moodTitle, description;
    Button saveButton;
    LinearLayout profileBackground;
    ImageView profilePic;
    Mood mood;
    Mood new_mood;

    /**
     * firebase stuff here
     * todo: putting mood objects in firebase and generating them
     */

    private FirebaseFirestore db;
    private CollectionReference moodCollectionReference;
    private static final String AUTH_TAG = "Google auth failure";
    private static final String USER_EXISTS_TAG = "Current mood check";
    private static final String REGISTER_USER_TAG = "Registering new mood";

    public EditmoodActivity() {
        this.db = FirebaseFirestore.getInstance();
        // collection reference for moods
        this.moodCollectionReference = db.collection("Moods");
    }


    public String date;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mood);
        profilePic = findViewById(R.id.Profile_image);
        saveButton = findViewById(R.id.save_button);
        dateText = findViewById(R.id.currentDate);
        moodTitle = findViewById(R.id.currentMood);
        description = findViewById(R.id.moodDescription);
        profileBackground = findViewById(R.id.background_pic);
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");


        /**
         * todo: No error checks are done here, need to be done if a new mood is added
         */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //public Mood (String moodTitle, String moodDescription, String moodColor, Date moodDate){
                try{
                    mood = new Mood(moodTitle.getText().toString(),description.getText().toString(),"#FFFF00",
                            dateFormat.parse(dateText.getText().toString()));
                }
                catch (ParseException e){
                    e.printStackTrace();
                }

            }
        });

        /**
         * save button
         */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCamera(view);
            }
        });

        /**
         * Setting up everything for edit mood when index != -1 after long click
         */
        if (testActivity.index != -1 ){
            dateText.setText(moods.get(testActivity.index).getMoodDate().toString());
            moodTitle.setText(moods.get(testActivity.index).getMoodTitle());
            description.setText(moods.get(testActivity.index).getMoodDescription());
            String stringHEX = moods.get(index).getMoodColor();
            try {
                profileBackground.setBackgroundColor(Color.parseColor(stringHEX));
            }catch (Throwable e){
                e.printStackTrace();
            }

            //todo: String to bitmap
            try{
                byte [] encodeByte=Base64.decode(moods.get(index).getMoodPhoto(),Base64.DEFAULT);
                Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                profilePic.setImageBitmap(bitmap);
            }catch (Exception e){
                e.getMessage();
            }

        }

        //public Mood (String moodTitle, String moodDescription, String moodColor, Date moodDate){

        // todo: if not edited or a new mood object us added
        else{
            HashMap<String,String> data= new HashMap<>();

            new_mood = new Mood();
            //todo: mood object gives a null object reference to be fixed
            // todo: I will probably need to separate add and edit to make things simpler

            try{
                if (!new_mood.getMoodTitle().isEmpty()){
                    data.put("MoodType", moodTitle.getText().toString());
                    new_mood.setMoodTitle(moodTitle.getText().toString());
                    new_mood.setMoodDate(dateFormat.parse(dateText.getText().toString()));
                    new_mood.setMoodColor("#00acee");

                }
                if (!new_mood.getMoodDescription().isEmpty()){
                    new_mood.setMoodDescription(description.getText().toString());
                }


            } catch (ParseException e){
                e.printStackTrace();
            }
            moods.add(new_mood);
            moodArrayAdapter.notifyDataSetChanged();
        }

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
                        EditmoodActivity.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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
                date = dateText.getText().toString();
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
            moods.get(index).setMoodPhoto(temp);
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
