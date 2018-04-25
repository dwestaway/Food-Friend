package com.foodfriend.foodfriend.AccountActivity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.foodfriend.foodfriend.R;
import com.foodfriend.foodfriend.TabbedActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button buttonSignup, buttonLogin, buttonReset;
    private SignInButton buttonGoogle;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private GoogleSignInClient googleSignInClient = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, TabbedActivity.class));
            finish();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("258491184913-ma54t4jcemjuavsh0ukrf0qsp73s6jou.apps.googleusercontent.com").requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Get views
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar =  findViewById(R.id.progressBar);
        buttonSignup = findViewById(R.id.buttonSignUp);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonReset = findViewById(R.id.buttonResetPass);
        buttonGoogle = findViewById(R.id.buttonGoogle);


        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, TabbedActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });

        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });

    }

    //Result of launching Google Sign In
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                final GoogleSignInAccount account = task.getResult(ApiException.class);

                //Get auth crediential from Google auth
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                //Sign in with google credentials
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    //Log.d(TAG, "signInWithCredential:success");

                                    FirebaseUser user = auth.getCurrentUser();

                                    //get google account display name and uid
                                    String name = account.getDisplayName();
                                    String uid = user.getUid();

                                    ///////////
                                    //Get profile image and make default image show
                                    //Change it so all values are not set to "" on sign in

                                    String profileImage = account.getPhotoUrl().toString();

                                    //Check if user first time signing in
                                    boolean newUser = task.getResult().getAdditionalUserInfo().isNewUser();

                                    //If first time signing in, set default database values
                                    if(newUser == true)
                                    {
                                        //set users values in database to blank
                                        mDatabase.child("users").child(uid).child("longitude").setValue("");
                                        mDatabase.child("users").child(uid).child("latitude").setValue("");
                                        mDatabase.child("users").child(uid).child("time").setValue("");
                                        mDatabase.child("users").child(uid).child("foodPOI").setValue("");
                                        mDatabase.child("users").child(uid).child("date").setValue("");
                                    }

                                    //send name and image to database
                                    mDatabase.child("users").child(uid).child("name").setValue(name);
                                    mDatabase.child("users").child(uid).child("profileImage").setValue(profileImage);

                                    //Launch tabbed activity
                                    Intent intent = new Intent(LoginActivity.this, TabbedActivity.class);
                                    startActivity(intent);
                                    finish();

                                }

                            }
                        });


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                auth.signOut();

                Toast.makeText(getApplicationContext(), "Google Sign in failed.", Toast.LENGTH_SHORT).show();

                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    @Override
    protected void onStart()
    {
        super.onStart();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, TabbedActivity.class));
            finish();
        }
    }
}
