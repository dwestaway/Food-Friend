package com.foodfriend.foodfriend;

/**
 * Created by Dan on 21/02/2018.
 */
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfriend.foodfriend.AccountActivity.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Tab1Profile extends Fragment {

    private Button btnChangePassword, confirmPassword, search;
    private TextView email, name;

    private EditText newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    private Spinner spinner;
    ArrayAdapter<CharSequence> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {





        return inflater.inflate(R.layout.fragment_tab1profile, container, false);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        //mDatabase = FirebaseDatabase.getInstance().getReference();

        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");

        name = (TextView) getView().findViewById(R.id.userName);
        email = (TextView) getView().findViewById(R.id.userEmail);

        btnChangePassword = (Button) getView().findViewById(R.id.change_password_button);
        confirmPassword = (Button) getView().findViewById(R.id.confirmPass);
        search = (Button) getView().findViewById(R.id.searchButton);

        newPassword = (EditText) getView().findViewById(R.id.newPassword);



        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setDataToView(user);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    //finish();
                }
            }
        };




        newPassword.setVisibility(View.GONE);
        confirmPassword.setVisibility(View.GONE);

        //create drop down menu
        spinner = (Spinner)getView().findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(getActivity(),R.array.Times,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getActivity(),adapterView.getItemAtPosition(i)+" has been selected",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }


        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newPassword.setVisibility(View.VISIBLE);
                confirmPassword.setVisibility(View.VISIBLE);

            }
        });

        //get GPS long and lat
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GPSLocation gps = new GPSLocation(getContext()); //send context to GPSLocation

                Location location = gps.getLocation();

                if(location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    Toast.makeText(getContext(),"Latitude: " + latitude + "\n Longitude: " + longitude, Toast.LENGTH_LONG).show();
                }
            }
        });

        //replace password
        confirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) { //Check password length is more than 6 characters
                        newPassword.setError("Password too short, enter minimum 6 characters");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Password updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            signOut();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(getActivity(), "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Enter password");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        //Load data from database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String uid = user.getUid(); //get user id

                String firstName = (String) dataSnapshot.child(uid).getValue();

                name.setText(firstName);

                email.setText(user.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        super.onActivityCreated(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    private void setDataToView(FirebaseUser user) {

        //email.setText(user.getEmail());

    }


    // this listener will be called when there is change in firebase user session
    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(getActivity(), LoginActivity.class));
                //finish();
            } else {
                setDataToView(user);

            }
        }


    };

    public void signOut() {
        auth.signOut();


// this listener will be called when there is change in firebase user session
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(getActivity(), "Signed Out", Toast.LENGTH_SHORT).show();
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    //finish();
                }
            }
        };
    }
    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
