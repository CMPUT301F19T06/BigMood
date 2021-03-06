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
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 *  Class for the adapter for the Friend fragment. Displays a user's profile pic and username,
 *  as well as the option to accept and reject the request
 */
//Here, we're using a RecyclerViewAdapter instead of a Listylist due to the limitations
public class FriendsRequestRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRequestRecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    private static final String FRIEND_ID = "com.example.bigmood.FriendRecycleViewAdapter";
    private CollectionReference userCollectionReference;
    private FirebaseFirestore db;

    //set up local arraylist to store rides
    private ArrayList<String> friendReqObjects = new ArrayList<>();

    //set up interface components to measure input from clicks
    private Context mContext;
    private String userId;
    ImageView deleteMood;
    //constructor
    public FriendsRequestRecyclerViewAdapter(ArrayList friendIDs, String userId, Context mContext) {
        this.friendReqObjects = friendIDs;
        this.mContext = mContext;
        this.userId = userId;
        this.db = FirebaseFirestore.getInstance();
        this.userCollectionReference = db.collection("Users");
    }

    //set up a new viewholder to mount onto main activity
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_friendrequestfragment, parent, false);
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
        //holder.friendName.setText(getName(this.friendReqObjects.get(position)));

        final Query query = userCollectionReference.whereEqualTo("userId", this.friendReqObjects.get(position));
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

        //todo: find a way for firebase to accept and decline requests via buttons
        //also update values i suppose
        //establish listener for each element
        holder.yeaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: implement power of friendship
                updateFriend(friendReqObjects.get(position));
                deleteRequest(friendReqObjects.get(position));
            }
        });

        holder.nahButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRequest(friendReqObjects.get(position));
            }
        });

    }

    public void intentUserView(int targetUser, View v){
        //todo: send user to UserViewActivity
        Intent intent = new Intent(v.getContext(), UserViewActivity.class);
        String targetUserId = friendReqObjects.get(targetUser);
        intent.putExtra("TARGET_ID", targetUserId);
        intent.putExtra("USER_ID", this.userId);
        intent.putExtra("HAS_VIEW_PERMISSION", true);
        mContext.startActivity(intent);
    }

    //item count for parsing through
    @Override
    public int getItemCount() {
        return this.friendReqObjects.size();
    }

    //constructor for ViewHolder object, which holds our xml components together
    public class ViewHolder extends RecyclerView.ViewHolder{
        // TextView text;
        TextView friendName;
        ConstraintLayout linearLayout;
        Button yeaButton;
        Button nahButton;
        public ViewHolder(View itemView) {
            super(itemView);
            // establish views here
            friendName = itemView.findViewById(R.id.friendName);
            yeaButton = itemView.findViewById(R.id.acceptButton);
            nahButton = itemView.findViewById(R.id.rejectButton);
        }
    }

    private void updateFriend(String targetId){
        final Query query = userCollectionReference.whereEqualTo("userId", targetId);
        final DocumentReference docRef = this.userCollectionReference.document(targetId);
        final HashMap<String, Object> data = new HashMap<>();
        data.put("userId", targetId);
            //accept the friend request
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.contains("userFriends")) {
                        // take the user's friends and append userID and update
                        List<String> temp = (List) doc.get("userFriends");
                        ArrayList<String> tempArray = new ArrayList<String>(temp);
                        tempArray.add(userId);
                        data.put("userFriends", tempArray);
                        docRef.set(data, SetOptions.merge());
                    } else {
                        // no friends, you fucking loser
                        List<String> temp = (List<String>) doc.get("incomingReq");
                        temp.add(userId);
                        data.put("userFriends", temp);
                        docRef.set(data, SetOptions.merge());
                    }
                }
            }
        });
    }

    private void deleteRequest(String targetId){
        final Query query = userCollectionReference.whereEqualTo("userId", this.userId);
        final DocumentReference docRef = this.userCollectionReference.document(this.userId);
        final HashMap<String, Object> data = new HashMap<>();
        data.put("userId", this.userId);
        //accept the friend request
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    // This line might explode
                    // slam in a try/catch
                    List<String> temp = (List) doc.get("incomingReq");
                    ArrayList<String> tempArray = new ArrayList<String>(temp);
                    tempArray.remove(targetId);
                    data.put("incomingReq", tempArray);
                    docRef.set(data, SetOptions.merge());
                }
            }
        });
    }

}
