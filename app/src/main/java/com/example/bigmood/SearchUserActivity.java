package com.example.bigmood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SearchUserActivity extends AppCompatActivity {

    private EditText mSearchField;
    private ImageButton mSearchBtn;

    private RecyclerView mResultList;

    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");

        mSearchField = findViewById(R.id.search_field);
        mSearchBtn = findViewById(R.id.search_btn);

        mResultList = findViewById(R.id.search_suggestions);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();
                firebaseUserSearch(searchText);
            }
        });
    }

    private void firebaseUserSearch(String searchText) {
        Toast.makeText(SearchUserActivity.this, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.child("Users").orderByChild("displayName").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(firebaseSearchQuery, User.class)
                .build();
        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull User model) {
                holder.setDetails(model.getDisplayName(), model.getProfilePictureUrl());
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_friendfragment,parent,false);
                return new UsersViewHolder(view);
            }
        };
        mResultList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    //View Holder Class
    public class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView){
            super(itemView);

            mView = itemView;
        }

        public void setDetails(String userName, String userImage){
            TextView user_name = mView.findViewById(R.id.friendName);
            ImageView  user_image = mView.findViewById(R.id.pfpImage);

            user_name.setText(userName);
            Bitmap bit = StringtoBitmap(userImage);
            user_image.setImageBitmap(bit);
        }

    }

    public Bitmap StringtoBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;

        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
