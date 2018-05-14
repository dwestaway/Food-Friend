package com.foodfriend.foodfriend.AccountActivity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.foodfriend.foodfriend.R;
import com.foodfriend.foodfriend.TabbedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName;
    private Button buttonSignUp, buttonBack;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonSignUp = (Button) findViewById(R.id.signupButton);
        inputName = (EditText) findViewById(R.id.firstName);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = inputName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter first name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.length() < 5) {
                    Toast.makeText(getApplicationContext(), "Please enter a real email", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE); //Make progress bar visible
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                                //If sign in is not successful, display message to user, else change screen and set users default values in database
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Sign Up Failed" + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignupActivity.this, "Account Created" + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignupActivity.this, TabbedActivity.class));

                                    //set name to users id in database
                                    mDatabase.child("users").child(task.getResult().getUser().getUid()).child("name").setValue(name);

                                    //set users values in database
                                    mDatabase.child("users").child(task.getResult().getUser().getUid()).child("longitude").setValue("");
                                    mDatabase.child("users").child(task.getResult().getUser().getUid()).child("latitude").setValue("");
                                    mDatabase.child("users").child(task.getResult().getUser().getUid()).child("time").setValue("");
                                    mDatabase.child("users").child(task.getResult().getUser().getUid()).child("foodPOI").setValue("");
                                    mDatabase.child("users").child(task.getResult().getUser().getUid()).child("date").setValue("");
                                    mDatabase.child("users").child(task.getResult().getUser().getUid()).child("profileImage").setValue("");

                                    finish();
                                }
                            }
                        });

            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}

