package com.example.bigmood;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.bigmood.testActivity.moodObjects;

public class ActivityMoodView extends AppCompatActivity {
    TextView moodText, dateText;
    String dayString;
    MenuInflater menuInflater;
    public static final int CAMERA_ACCESS = 1001;
    public static final int GALLERY_ACCESS = 9999;
    private ImageView imageView;
    private Button cancelButton;
    private Context context;
    private CircleImageView ProfileImage;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseDatabase Fd;
    private FirebaseStorage firebaseStorage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:MM");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_view);
        moodText = (TextView)findViewById(R.id.currentMood);
        dateText = (TextView)findViewById(R.id.currentDate);
        Date date = Calendar.getInstance().getTime();
        dayString = dateFormat.format(date);
        dateText.setText(dayString);
        ProfileImage = findViewById(R.id.Profile_image);
        TextView moodDescription = findViewById(R.id.moodDescription);


        // Firebase

        // setting the stuff inside moodView from moodObject
        moodDescription.setText(String.valueOf(moodObjects.get(0).toString()));
        dateText.setText(String.valueOf(moodObjects.get(0).toString()));
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCamera(view);
            }
        });
    }

    /**
     * Open camera functionality
     * @param view
     */
    public void OpenCamera(View view){
        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA_ACCESS);
    }

    /**
     * Open Gallery/ Album functionality
     * @param view
     */
    public void OpenAlbum(View view){
        Intent intent =  new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_ACCESS);

    }

    /**
     * Menu stuff: inflates menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mood_view_menu, menu);
        return true;

    }
    public boolean onOptionsItemSelected(MenuItem item){
        return true;

    }

    /**
     * On activity result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        context = getApplicationContext();
        super.onActivityResult(requestCode, resultCode, data);
        String path = "firememes/" + UUID.randomUUID() + ".png";
        StorageReference firememesRef = firebaseStorage.getInstance().getReference(path);

        if(requestCode==CAMERA_ACCESS && resultCode == RESULT_OK)    {
            // todo: adding this image in firabase
            Bitmap bitmap= (Bitmap) data.getExtras().get("data");
            ProfileImage.setImageBitmap(bitmap);
        }

        else if(requestCode==GALLERY_ACCESS){
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ProfileImage.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(context, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}

