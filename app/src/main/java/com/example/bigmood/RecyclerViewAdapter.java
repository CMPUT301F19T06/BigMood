package com.example.bigmood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 *  Adapter for moods for use in Recycler views. Displays the mood title, the mood creator,
 *  the date for the mood, as well as a colored emoji
 */
//Here, we're using a RecyclerViewAdapter instead of a Listylist due to the limitations
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    private static final String MOOD_ID = "com.example.bigmood.RecycleViewAdapter";

    //set up local arraylist to store rides
    private ArrayList<Mood> moodIDs = new ArrayList<Mood>();

    //set up interface components to measure input from clicks
    private Context mContext;
    private String userId;
    //constructor
    public RecyclerViewAdapter(ArrayList moodIDs, String userId, Context mContext) {
        this.moodIDs = moodIDs;
        this.mContext = mContext;
        this.userId = userId;
    }

    //set up a new viewholder to mount onto main activity
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_moodfragment, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //bind viewholder to holder, process input here
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //prepare display variables for list
        Log.d(TAG, "onBindViewHolder: called.");


        //set up the connection to view here, TBA
        //temporary placeholder for date
        holder.moodDate.setText(moodIDs.get(position).getMoodDate().toDate().toString());
        holder.moodDescription.setText(moodIDs.get(position).getMoodTitle());

        holder.moodUsername.setText(moodIDs.get(position).getMoodUsername());
        Log.d("SDA","Mood from rec: " + moodIDs.get(position).getMoodSituation());
        //emoji
        byte[] decodedByte = Base64.decode(moodIDs.get(position).getMoodEmoji(), 0);
        Bitmap image = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        holder.emoji.setImageBitmap(image);
        holder.emoji.setColorFilter(Color.parseColor(moodIDs.get(position).getMoodColor()), PorterDuff.Mode.MULTIPLY);


        String stringHEX = moodIDs.get(position).getMoodColor();
        try {
            //holder.linearLayout.setBackgroundColor(Color.parseColor(stringHEX));
        }catch (Throwable e){
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DashboardActivity.index = position;
                Log.d(TAG, "onClick: clicked on:" + String.valueOf(position));
                intentMoodView(moodIDs.get(position), v);
            }
        });
        //establish listener for each element

    }
    /**
     * function for turning string to bitmap
     * usage: for emoji
     * @param encodedString
     * @return
     */
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public void intentMoodView(Mood moodID, View v){
        Intent intent = new Intent(v.getContext(), ActivityMoodView.class);
        intent.putExtra("Mood", moodID);
        Log.d(TAG,"Mood Id from recyclerView" + moodID.getMoodDate());
        String date = moodID.getMoodDate().toDate().toString();
        intent.putExtra("USER_ID", this.userId);
        intent.putExtra("DATE",date);

        mContext.startActivity(intent);
    }

    //item count for parsing through
    @Override
    public int getItemCount() {
        return moodIDs.size();
    }

    //constructor for ViewHolder object, which holds our xml components together
    public class ViewHolder extends RecyclerView.ViewHolder{
        // TextView text;
        TextView moodUsername, moodDescription, moodDate;
        ImageView emoji;
        ConstraintLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            // establish views here
            moodUsername = itemView.findViewById(R.id.moodUserName);
            moodDate = itemView.findViewById(R.id.moodDate);

            moodDescription = itemView.findViewById(R.id.moodDescription);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            emoji = itemView.findViewById(R.id.moodEmoji);
        }
    }
}