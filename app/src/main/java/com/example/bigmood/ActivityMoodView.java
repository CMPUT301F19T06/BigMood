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


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

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
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCamera(view);
            }
        });
    }

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
    public boolean onCreateOptionsMenu(Menu menu){
        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mood_view_menu, menu);
        return true;

    }
    public boolean onOptionsItemSelected(MenuItem item){
        return true;

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        context = getApplicationContext();
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CAMERA_ACCESS && resultCode == RESULT_OK)    {
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

