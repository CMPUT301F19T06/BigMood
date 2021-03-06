package com.example.bigmood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *  Login screen for google authentication. Authentication was used from the developers guide
 *  at https://developers.google.com/identity/sign-in/android/sign-in
 *
 *  Connects to the dashboard automatically if the google account is still valid
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseFirestore db;
    private CollectionReference userCollectionReference;
    private static final String AUTH_TAG = "Google auth failure";
    private static final String USER_EXISTS_TAG = "Current user check";
    private static final String REGISTER_USER_TAG = "Registering new user";
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;
    private boolean signInAttempted = false;

    public LoginActivity() {
        this.db = FirebaseFirestore.getInstance();
        this.userCollectionReference = db.collection("Users");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.button_login_button).setOnClickListener(this);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_login_button) {
            signIn();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    protected void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            signInAttempted = true;
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            signInAttempted = true;
            Log.w(AUTH_TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    protected void updateUI(GoogleSignInAccount account) {
        if (account == null & signInAttempted) {
            findViewById(R.id.textView_login_error_msg).setVisibility(View.VISIBLE);
        } else if (account != null){
            findViewById(R.id.textView_login_error_msg).setVisibility(View.GONE);
            findViewById(R.id.button_login_button).setVisibility(View.GONE);
            checkUserExists(account);
        }
    }

    protected void checkUserExists(final GoogleSignInAccount account) {
        // start spinner until completed
        findViewById(R.id.progressBar_login_check_user).setVisibility(View.VISIBLE);
        DocumentReference docRef = userCollectionReference.document(account.getId());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(USER_EXISTS_TAG, "DocumentSnapshot data: " + document.getData());

                } else {
                    Log.d(USER_EXISTS_TAG, "No such document");
                    createUser(account);
                }
                findViewById(R.id.progressBar_login_check_user).setVisibility(View.GONE);
                TextView welcomeMsg = findViewById(R.id.textView_login_welcome_msg);
                welcomeMsg.setText(String.format("Welcome %s", account.getDisplayName()));
                welcomeMsg.setVisibility(View.VISIBLE);
                startDashboard(account.getId(), account.getDisplayName());
            } else {
                Log.d(USER_EXISTS_TAG, "get failed with ", task.getException());
                findViewById(R.id.progressBar_login_check_user).setVisibility(View.GONE);
            }
        });
    }

    protected void createUser(GoogleSignInAccount account) {
        Timestamp timestamp = new Timestamp(new Date());
        HashMap<String, Object> data = new HashMap<>();
        data.put("dateCreated", timestamp);
        data.put("userId", account.getId());
        data.put("displayName", account.getDisplayName());
        data.put("moods", new ArrayList<String>());
        data.put("userFriends", new ArrayList<String>());
        data.put("incomingReq", new ArrayList<String>());
        userCollectionReference
                .document(account.getId())
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d(REGISTER_USER_TAG, "Data addition successful"))
                .addOnFailureListener(e -> Log.d(REGISTER_USER_TAG, "Data addition failed" + e.toString()));
    }

    protected void startDashboard(String id, String username) {
        Intent intent = new Intent(this, BaseDrawerActivity.class);
        intent.putExtra("USER_ID", id);
        intent.putExtra("User_Name",username);
        Log.d("User","Username from login: " + username);
        startActivity(intent);
    }
}
