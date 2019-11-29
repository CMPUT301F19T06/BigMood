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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 *  Class for the adapter for the Friend fragment. Displays a user's profile pic and username
 */
//Here, we're using a RecyclerViewAdapter instead of a Listylist due to the limitations
public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private static final String FRIEND_ID = "com.example.bigmood.FriendRecycleViewAdapter";
    private FirebaseFirestore db;
    private CollectionReference userCollectionReference;

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
        this.db = FirebaseFirestore.getInstance();
        this.userCollectionReference = db.collection("Users");
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
        //holder.friendName.setText(getName(this.friendObjects.get(position)));

        final Query query = userCollectionReference.whereEqualTo("userId", this.friendObjects.get(position));
        final DocumentReference docRef = this.userCollectionReference.document(userId);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.contains("displayName")) {
                        String temp = (String) doc.get("displayName");
                        holder.friendName.setText(temp);
                    }
                }
            }
        });

        //todo: Find way to implement friend display names and profile pictures proper
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendsActivity.index = position;
                Log.d(TAG, "onClick: clicked on:" + String.valueOf(position));
                intentUserView(position, v);
            }
        });
        //establish listener for each element

    }

    public void intentUserView(int targetUser, View v){
        //todo: send user to UserViewActivity
        Intent intent = new Intent(v.getContext(), UserViewActivity.class);
        String targetUserId = friendObjects.get(targetUser);
        intent.putExtra("TARGET_ID", targetUserId);
        intent.putExtra("USER_ID", this.userId);
        mContext.startActivity(intent);
    }

    //item count for parsing through
    @Override
    public int getItemCount() {
        return this.friendObjects.size();
    }

    //constructor for ViewHolder object, which holds our xml components together
    public class ViewHolder extends RecyclerView.ViewHolder {
        // TextView text;
        TextView friendName;
        ConstraintLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            // establish views here
            friendName = itemView.findViewById(R.id.friendName);
        }
    }

    //accept the friend request
    //displayName
}
