package com.example.bigmood;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.example.bigmood.DashboardActivity.index;
import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

/**
 * This is a class for adding and editing mood events
 */

public class ActivityAddMood extends AppCompatActivity {
    private static final String TAG = "ActivityAddMood";

    public static final int CAMERA_ACCESS = 1001;
    public static final int GALLERY_ACCESS = 9999;
    public static final int MOODVIEW_ACCESS = 5555;
    private Context context;
    TextView dateText , description, moodUserName;
    Button saveButton;
    Button addLoc;
    ImageView profileBackground, getProfileBackground, galleryAccess;
    ImageView profilePic, deleteMood, emojiPic;
    Spinner moodTitle, moodColor, moodSituation; // moodTitle and moodType is the same here for now
    String image;
    private FusedLocationProviderClient fusedLocationClient;
    private String userId, username;
    int imageTracker = 0;

    /**
     * firebase stuff here
     * todo: putting mood objects in firebase and generating them
     */

    private FirebaseFirestore db;
    private CollectionReference moodCollectionReference;
    private CollectionReference userCollectionReference;

    public ActivityAddMood() {
        this.db = FirebaseFirestore.getInstance();
        // collection reference for moods
        this.moodCollectionReference = db.collection("Moods");
        this.userCollectionReference = db.collection("Users");
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener;

    /**
     * OnCreate for edit Mood
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // all the stuff id's
        setContentView(R.layout.activity_add_mood);
        saveButton = findViewById(R.id.save_button);
        addLoc = findViewById(R.id.add_loc);
        dateText = findViewById(R.id.currentDate);
        description = findViewById(R.id.moodDescription_edit);
        moodTitle = findViewById(R.id.currentMood);
        moodUserName = findViewById(R.id.moodUserName);
        moodSituation = findViewById(R.id.moodSituationSpinner);

        // profile pick and background pic
        profilePic = findViewById(R.id.add_background_image);
        profileBackground = findViewById(R.id.background_pic);
        getProfileBackground = findViewById(R.id.add_background_image);

        deleteMood = findViewById(R.id.deleteMood);
        emojiPic = findViewById(R.id.currentMoodImage);
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.userId = getIntent().getExtras().getString("USER_ID");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // object added to moods array adapter
        final ArrayAdapter<CharSequence> titleAdapter = ArrayAdapter.createFromResource(this, R.array.editmood_moodspinner, android.R.layout.simple_list_item_1);
        final ArrayAdapter<CharSequence> situations = ArrayAdapter.createFromResource(this, R.array.editmood_moodsituation_spinner, android.R.layout.simple_list_item_1);

        /**
         * Set up the spinner adapters accordingly
         */
        moodTitle.setAdapter(titleAdapter);
        moodSituation.setAdapter(situations);


        /**
         * HashMap for each mood Colors
         * changes the color according to moodtitle
         */
        final HashMap<String,String> colorHash = new HashMap<String, String>(){{
            put("Set Color", getResources().getString(0+R.color.Base));
            put("Happy", getResources().getString(0+R.color.Happy));
            put("Scared", getResources().getString(0+R.color.Scared));
            put("Surprised", getResources().getString(0+R.color.Surprised));
            put("Disgusted", getResources().getString(0+R.color.Disgusted));
            put("Angry", getResources().getString(0+R.color.Angry));
            put("Bored", getResources().getString(0+R.color.Bored));
            put("Sad", getResources().getString(0+R.color.Sad));
            put("Touched", getResources().getString(0+R.color.Touched));
        }};

        /**
         * getting the information from the intents
         */
        final Mood mood = (Mood)getIntent().getSerializableExtra("Mood");
        username = mood.getMoodUsername();
        moodUserName.setText(mood.getMoodUsername());
        final CollectionReference collectionReference = db.collection("Moods");
        String date = getIntent().getExtras().getString("DATE");
        String mode = getIntent().getExtras().getString("EDIT");


        /**
         * handling if it's either editing or adding a mood
         */
        switch (mode){
            case "AddingMode":
                setMoodEmoji("Happy");
                emojiPic.setColorFilter(getColor(R.color.Happy), PorterDuff.Mode.MULTIPLY);
                dateText.setText(date);
                break;
            case "EditingMode":
                setMoodEmoji(mood.getMoodTitle());
                emojiPic.setColorFilter(Color.parseColor(mood.getMoodColor()), PorterDuff.Mode.MULTIPLY);
                moodTitle.setSelection(titleAdapter.getPosition(mood.getMoodTitle()));
                moodSituation.setSelection(situations.getPosition(mood.getMoodSituation()));
                moodUserName.setText(mood.getMoodUsername());
                byte [] bytes=Base64.decode(mood.getMoodEmoji(),Base64.DEFAULT);
                Bitmap bitmap=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                emojiPic.setImageBitmap(bitmap);
                setMoodEmoji(mood.getMoodTitle());
                emojiPic.setColorFilter(Color.parseColor(mood.getMoodColor()), PorterDuff.Mode.MULTIPLY);
                dateText.setText(date);
                description.setText(mood.getMoodDescription());

                /**
                 * conversion of string to bitmap for profile picture
                 */
                try{
                    byte [] encodeByte=Base64.decode(mood.getMoodPhoto(),Base64.DEFAULT);
                    Bitmap bitmap1 =BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    profileBackground.setImageBitmap(bitmap1);


                }catch (Exception e){
                    e.getMessage();
                }
        }
        /**
         * changes the moodTitle and emojis here
         */
        moodTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setMoodEmoji(titleAdapter.getItem(moodTitle.getSelectedItemPosition()).toString());
                emojiPic.setColorFilter(Color.parseColor(
                        colorHash.get(titleAdapter.getItem(moodTitle.getSelectedItemPosition())))
                        , PorterDuff.Mode.MULTIPLY);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**
         * Set profile background
         */
        getProfileBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCamera(v);
            }
        });

        /**
         * Save button to save mood object with it's requirements
         */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mood.setMoodTitle(titleAdapter.getItem(moodTitle.getSelectedItemPosition()).toString());
                setMoodEmoji(mood.getMoodTitle());
                try{
                    mood.setMoodDate(new Timestamp((dateFormat.parse(dateText.getText().toString()))));
                }catch (ParseException e){
                    mood.setMoodDate(Timestamp.now());
                    e.getStackTrace();
                }
                mood.setMoodColor(colorHash.get(titleAdapter.getItem(moodTitle.getSelectedItemPosition())));
                mood.setMoodSituation(situations.getItem(moodSituation.getSelectedItemPosition()).toString());
                mood.setMoodPhoto(image);

                mood.setMoodEmoji(getMoodEmoji());
                mood.setMoodUsername(mood.getMoodUsername());
                String reason = description.getText().toString();
                if (reason.length() > 20){
                    Toast.makeText(ActivityAddMood.this, "DESCRIPTION TOO LONG\nMAX 20 CHARACTERS",Toast.LENGTH_SHORT).show();
                }
                else if (words(reason) == false){
                    Toast.makeText(ActivityAddMood.this, "MAX 3 WORDS",Toast.LENGTH_SHORT).show();

                }
                else {
                    mood.setMoodDescription(reason);
                    // input mood after mood object is created
                    InputMood(mood, view, collectionReference);
                }

            }
        });

        /**
         * delete mood removes a mood event
         */
        deleteMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mood.getMoodID() != NULL){
                    collectionReference.document(mood.getMoodID())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Mood successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting mood", e);
                                }
                            });

                }else{
                    Toast.makeText(ActivityAddMood.this, "No mood posted",Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(ActivityAddMood.this, DashboardActivity.class);
                intent.putExtra("USER_ID",userId);
                intent.putExtra("User_Name",username);
                startActivity(intent);
            }
        });

        /**
         * adding a location to a mood event
         */
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //mloc = location;
                Log.d("Location Changes", location.toString());
                //lng = location.getLongitude();
                //lat = location.getLatitude();
                mood.setLatitude(location.getLatitude());
                mood.setLongitude(location.getLongitude());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Status Changed", String.valueOf(status));
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Provider Enabled", provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Provider Disabled", provider);
            }
        };

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Looper looper = null;


        addLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    ActivityCompat.requestPermissions(ActivityAddMood.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                }
                locationManager.requestSingleUpdate(criteria, locationListener, looper);
            }
        });
        /**
         * changing the profile picture here
         */
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCamera(view);
            }
        });

    }
    /**
     * Creates a mood object and puts it in the database
     * @param mood
     * @param view
     * @param collectionReference
     */
    public void InputMood(Mood mood, View view, CollectionReference collectionReference){
        HashMap<String, Object> data = new HashMap<>();
        data.put("moodTitle", mood.getMoodTitle());
        data.put("moodDescription", mood.getMoodDescription());
        data.put("moodColor", mood.getMoodColor());
        data.put("moodPhoto", mood.getMoodPhoto());
        data.put("moodDate", mood.getMoodDate());
        data.put("dateCreated", mood.getMoodDate());
        data.put("dateUpdated", Timestamp.now());
        data.put("userName", mood.getMoodUsername());
        data.put("moodCreator", userId);
        data.put("moodSituation", mood.getMoodSituation());
        data.put("longitude", mood.getLongitude());
        data.put("latitude", mood.getLatitude());
        data.put("moodEmoji", mood.getMoodEmoji());

        /**
         * checks if the object is already present in the adapter
         */
        if (index != -1){
            data.put("moodId", mood.getMoodID());
            collectionReference
                    .document(mood.getMoodID())
                    .update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Hello","Data addition successful");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Hello", "Data addition failed" + e.toString());
                        }
                    });
            OpenMoodView(view, mood);
            index = -1;
        }
        else{
            /**
             * else add a new mood if that mood event does not exist
             */
            mood.setMoodID(String.valueOf(Timestamp.now().hashCode()));
            data.put("moodId", mood.getMoodID());
            collectionReference
                    .document(mood.getMoodID())
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Hello","Data addition successful");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Hello", "Data addition failed" + e.toString());
                        }
                    });
            index = -1;
            OpenMoodView(view, mood);
        }

    }
    /**
     * Set emoji according to mood type
     * @param emotion
     */
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

    /**
     * number of words testing
     * @param i
     * @return
     */
    public boolean words(String i){
        String[] arrOfi = i.split(" ");
        if (arrOfi.length <= 3){
            return true;
        } else {
            return false;
        }
    }

    /**
     * get the mood emoji from drawable
     * @return : a string format for the emoji
     */
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

    /**
     * Open camera
     */

    public void OpenCamera(View view){
        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA_ACCESS);
    }
    /**
     * Open gallery
     * Not used in this implementation
     */
    public void OpenAlbum(View view){
        Intent intent =  new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_ACCESS);

    }

    /**
     * Open mood View
     * @param view
     * @param mood
     */
    public void OpenMoodView(View view, Mood mood){
        Intent intent = new Intent(ActivityAddMood.this,ActivityMoodView.class);
        // todo fix the stack trace
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("Mood",mood);
        String date = mood.getMoodDate().toDate().toString();
        intent.putExtra("DATE",date);
        startActivityForResult(intent,MOODVIEW_ACCESS);
    }



    /**
     * On activity result takes care of the stack trace for each activity opening from edit mood
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
            image = temp;
            profileBackground.setImageBitmap(bitmap);
        }

        else if(requestCode==GALLERY_ACCESS) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageStream.close();

                profileBackground.setImageBitmap(selectedImage);
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG,100, baos);
                byte [] b =baos.toByteArray();
                String temp=Base64.encodeToString(b, Base64.DEFAULT);
                image = temp;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        else  if (requestCode == MOODVIEW_ACCESS){
            finish();
        }
        else {
            Toast.makeText(context, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}