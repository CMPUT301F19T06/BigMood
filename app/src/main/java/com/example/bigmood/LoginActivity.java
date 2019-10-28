package com.example.bigmood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

// Login screen for google authentication. Authentication was used from the developers guide
// at https://developers.google.com/identity/sign-in/android/sign-in
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Google auth failure";
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;
    private boolean signInAttempted = false;

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
        switch (v.getId()) {
            case R.id.button_login_button:
                signIn();
                break;
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
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    protected void updateUI(GoogleSignInAccount account) {
        //TODO: check for valid login and if exists move to dashboard with userid
        // if account is null then no authentication was completed
        if (account == null & signInAttempted) {
            findViewById(R.id.textView_login_error_msg).setVisibility(View.VISIBLE);
        } else if (account != null){
            findViewById(R.id.textView_login_error_msg).setVisibility(View.GONE);
            findViewById(R.id.button_login_button).setVisibility(View.GONE);
            TextView welcomeMsg = findViewById(R.id.textView_login_welcome_msg);
            welcomeMsg.setText(String.format("Welcome %s", account.getDisplayName()));
            welcomeMsg.setVisibility(View.VISIBLE);
            startDashboard(account.getId());
        }
    }

    protected void startDashboard(String id) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("USER_ID", id);
        startActivity(intent);
    }
}
