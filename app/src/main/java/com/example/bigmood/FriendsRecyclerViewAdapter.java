package com.example.bigmood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    private static final String FRIEND_ID = "com.example.bigmood.FriendRecycleViewAdapter";

    //set up local arraylist to store rides
    private ArrayList<String> friendObjects = new ArrayList<>();

    //set up interface components to measure input from clicks
    private Context mContext;
    private String userId;
    ImageView deleteMood;
    //constructor
    public FriendsRecyclerViewAdapter(ArrayList friendIDs, String userId, Context mContext) {
        this.friendObjects = friendIDs;
        this.mContext = mContext;
        this.userId = userId;
    }

    //set up a new viewholder to mount onto main activity
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_friendfragment, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //bind viewholder to holder, process input here
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //prepare display variables for list
        Log.d(TAG, "onBindViewHolder: called.");
        FriendsActivity.index = position;

        //set up the connection to view here, TBA
        holder.friendName.setText(this.friendObjects.get(position));
        //todo: Find way to implement friend display names and profile pictures proper
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendsActivity.index = position;
                Log.d(TAG, "onClick: clicked on:" + String.valueOf(position));
                //intentMoodView(FriendsActivity.moodObjects.get(position), v);
            }
        });
        //establish listener for each element

    }

    public void intentMoodView(Mood moodID, View v){
        //todo: send user to UserViewActivity
        //Intent intent = new Intent(v.getContext(), ActivityAddMood.class);
        //intent.putExtra("Mood", moodID);
        //intent.putExtra("USER_ID", this.userId);
        //mContext.startActivity(intent);
    }

    //item count for parsing through
    @Override
    public int getItemCount() {
        return this.friendObjects.size();
    }

    //constructor for ViewHolder object, which holds our xml components together
    public class ViewHolder extends RecyclerView.ViewHolder{
        // TextView text;
        TextView friendName;
        ConstraintLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            // establish views here
            friendName = itemView.findViewById(R.id.friendName);
        }
    }
}
