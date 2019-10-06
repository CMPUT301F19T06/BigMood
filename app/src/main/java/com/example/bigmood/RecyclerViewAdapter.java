package com.example.bigmood;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

//Here, we're using a RecyclerViewAdapter instead of a Listylist due to the limitations
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    //set up local arraylist to store rides
    private ArrayList<moodObject> moodIDs = new ArrayList<>();

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
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_main, parent, false);
       ViewHolder holder = new ViewHolder(view);
       holder.text.setText(moodIDs.get(viewType).mood());
       return holder;

    }

    //bind viewholder to holder, process input here
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //prepare display variables for list
        Log.d(TAG, "onBindViewHolder: called.");

        //set up the connection to view here, TBA

        //establish listener for each element
        //TBD
    }

    //item count for parsing through
    @Override
    public int getItemCount() {
        return moodIDs.size();
    }

    //constructor for ViewHolder object, which holds our xml components together
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        CoordinatorLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            //establish views here
            text = itemView.findViewById(R.id.textView);
            // date = itemView.findViewById(R.id.dateView);
            // time  = itemView.findViewById(R.id.timeView);
            // distance = itemView.findViewById(R.id.distanceView);
            parentLayout = itemView.findViewById(R.id.C);
        }
    }
}
