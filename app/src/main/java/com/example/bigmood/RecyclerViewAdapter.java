package com.example.bigmood;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

//Here, we're using a RecyclerViewAdapter instead of a Listylist due to the limitations
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    private static final String MOOD_ID = "com.example.bigmood.RecycleViewAdapter";

    //set up local arraylist to store rides
    private ArrayList<Mood> moodIDs = new ArrayList<>();

    //set up interface components to measure input from clicks
    private Context mContext;

    //constructor
    public RecyclerViewAdapter(ArrayList moodIDs, Context mContext) {
        this.moodIDs = moodIDs;
        this.mContext = mContext;
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

        //establish listener for each element
        holder.activityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //use interface to register input back in MainActivity
                Log.d(TAG, "onClick: clicked on:" + moodIDs.get(position));
                intentMoodView(moodIDs.get(position).getMoodID(), v);
            }
        });
    }

    public void intentMoodView(String moodID, View v){
        Intent intent = new Intent(v.getContext(), EditmoodActivity.class);
        intent.putExtra(MOOD_ID, moodID);
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
        CoordinatorLayout parentLayout;
        Button activityButton;

        public ViewHolder(View itemView) {
            super(itemView);
            //establish views here
            activityButton = itemView.findViewById(R.id.placeholder1);
            // date = itemView.findViewById(R.id.dateView);
            // time  = itemView.findViewById(R.id.timeView);
            // distance = itemView.findViewById(R.id.distanceView);
            // parentLayout = itemView.findViewById(R.id.C);
        }
    }
}
