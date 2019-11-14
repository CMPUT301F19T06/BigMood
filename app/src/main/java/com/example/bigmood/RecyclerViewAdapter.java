package com.example.bigmood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

//Here, we're using a RecyclerViewAdapter instead of a Listylist due to the limitations
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    private static final String MOOD_ID = "com.example.bigmood.RecycleViewAdapter";
    ImageView emoji;

    //set up local arraylist to store rides
    //private ArrayList<Mood> moodIDs = new ArrayList<>();

    //set up interface components to measure input from clicks
    private Context mContext;
    private String userId;
    //constructor
    public RecyclerViewAdapter(ArrayList moodIDs, String userId, Context mContext) {
        DashboardActivity.moodObjects = moodIDs;
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
        DashboardActivity.index = position;

        //set up the connection to view here, TBA
        //temporary placeholder for date
        holder.moodDate.setText(DashboardActivity.moodObjects.get(position).getMoodDate().toDate().toString());
        holder.moodDescription.setText(DashboardActivity.moodObjects.get(position).getMoodDescription());
        holder.moodTitle.setText(DashboardActivity.moodObjects.get(position).getMoodTitle());
        holder.emoji.setImageBitmap(StringToBitMap(DashboardActivity.moodObjects.get(position).getMoodEmoji()));
        String stringHEX = DashboardActivity.moodObjects.get(position).getMoodColor();
        try {
            holder.linearLayout.setBackgroundColor(Color.parseColor(stringHEX));
        }catch (Throwable e){
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on:" + DashboardActivity.moodObjects.get(position));
                intentMoodView(DashboardActivity.moodObjects.get(position), v);
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
        Intent intent = new Intent(v.getContext(), ActivityAddMood.class);
        intent.putExtra("Mood", moodID);
        intent.putExtra("USER_ID", this.userId);
        mContext.startActivity(intent);
    }

    //item count for parsing through
    @Override
    public int getItemCount() {
        return DashboardActivity.moodObjects.size();
    }

    //constructor for ViewHolder object, which holds our xml components together
    public class ViewHolder extends RecyclerView.ViewHolder{
        // TextView text;
        TextView moodTitle, moodDescription, moodDate;
        ConstraintLayout linearLayout;
        ImageView emoji;

        public ViewHolder(View itemView) {
            super(itemView);
            // establish views here
            moodTitle = itemView.findViewById(R.id.moodName);
            moodDate = itemView.findViewById(R.id.moodDate);
            moodDescription = itemView.findViewById(R.id.moodDescription);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            emoji = itemView.findViewById(R.id.moodEmoji);
        }
    }
}
