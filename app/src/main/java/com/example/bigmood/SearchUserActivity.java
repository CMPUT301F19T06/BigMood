package com.example.bigmood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserActivity extends AppCompatActivity {

    private EditText mSearchField;
    private CircleImageView mSearchBtn;

    private RecyclerView mResultList;

    private FirebaseFirestore mUserDatabase;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        this.userId = getIntent().getExtras().getString("USER_ID");

        mUserDatabase = FirebaseFirestore.getInstance();

        mSearchField = findViewById(R.id.search_field);
        mSearchBtn = findViewById(R.id.search_btn);

        mResultList = findViewById(R.id.search_suggestions);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = mSearchField.getText().toString();
                if(searchText.compareTo("") != 0){
                    Toast.makeText(SearchUserActivity.this, "Searching", Toast.LENGTH_LONG).show();
                    firebaseUserSearch(searchText);
                } else{
                    Toast.makeText(SearchUserActivity.this, "Please enter a Name", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Searches for the searchText in firebase under the collection "Users" and adds it to a RecyclerView
     * @param searchText
     */
    private void firebaseUserSearch(String searchText) {

        Query firebaseSearchQuery = mUserDatabase.collection("Users").orderBy("displayName").startAt(searchText).endAt(searchText + "\uf8ff");

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(firebaseSearchQuery, User.class)
                .build();

        FirestoreRecyclerAdapter firebaseRecyclerAdapter = new FirestoreRecyclerAdapter<User, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull User model) {
                holder.setDetails(model.getDisplayName());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), UserViewActivity.class);
                        intent.putExtra("USER_ID", userId);
                        intent.putExtra("TARGET_ID", model.getUserId());
                        intent.putExtra("HAS_VIEW_PERMISSION", true);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_friendfragment, parent,false);
                return new UsersViewHolder(view);
            }
        };
        mResultList.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }

    /**
     * UserViewHolder Class to set the details in the fragments in the RecyclerView
      */
    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView){
            super(itemView);

            mView = itemView;
        }

        public void setDetails(String userName){
            TextView user_name = mView.findViewById(R.id.friendName);
            ImageView  user_image = mView.findViewById(R.id.pfpImage);

            user_name.setText(userName);
        }
    }
}
