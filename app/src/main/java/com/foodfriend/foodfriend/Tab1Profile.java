package com.foodfriend.foodfriend;

/**
 * Created by Dan on 21/02/2018.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfriend.foodfriend.AccountActivity.LoginActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class Tab1Profile extends Fragment {

    private Button search, placePicker;
    private TextView name;
    private ImageView profileImg;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private Spinner spinner;
    ArrayAdapter<CharSequence> adapter;

    private GPSLocation gps;
    private Location location;

    //EditText that autocompletes
    private AutoCompleteTextView autoComplete;
    private ArrayAdapter<String> foodAdapter;

    private final static int PLACE_PICKER_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_tab1profile, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        //Prevent activity from starting with the keyboard open
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Get the Firebase authenticatpr
        auth = FirebaseAuth.getInstance();
        //Get the Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Request permissions for GPS/location
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");

        //Get reference to all components in the activitys layout
        name = (TextView) getView().findViewById(R.id.userName);
        search = (Button) getView().findViewById(R.id.searchButton);
        profileImg = (ImageView) getView().findViewById(R.id.profileImage);
        placePicker = getView().findViewById(R.id.placeButton);


        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        sendLocation();

        //Get array of common food places of interest
        String[] foodPOIarray = getResources().getStringArray(R.array.foodPOI);

        //Create adapter to use a list as autocomplete for the text box
        foodAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice, foodPOIarray);

        autoComplete = getView().findViewById(R.id.foodChoice);
        autoComplete.setAdapter(foodAdapter);
        autoComplete.setThreshold(1);

        //When user clicks outside of the keyboard/auto complete text view
        autoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if(!focused)
                {
                    hideKeyboard(view);
                }
            }
        });


        //create drop down menu
        spinner = (Spinner)getView().findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(getActivity(),R.array.Times,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //Search button click
        search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                //send users location to database
                sendLocation();

                //get users chosen place of interest
                String poi = autoComplete.getText().toString().trim();
                //get users chosen time to eat
                String time = spinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(poi)) {
                    Toast.makeText(getActivity(), "Enter food place of interest", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (time.equals("Choose a Time")) {
                    Toast.makeText(getActivity(), "Enter a time", Toast.LENGTH_SHORT).show();
                    return;
                }

                //set database values: food place of interest and time
                mDatabase.child("users").child(user.getUid()).child("foodPOI").setValue(poi);
                mDatabase.child("users").child(user.getUid()).child("time").setValue(time);

                //get current date and send to database
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String date = sdf.format(today);

                //send todays date to user data on server database
                mDatabase.child("users").child(user.getUid()).child("date").setValue(date);

                Toast.makeText(getActivity(), "Finding Matches", Toast.LENGTH_SHORT).show();

                TabbedActivity.mViewPager.setCurrentItem(1);
            }
        });

        placePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Intent intent;

                try {
                    intent = builder.build(getActivity());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                }
                catch (GooglePlayServicesRepairableException e)
                {
                    e.printStackTrace();
                }
                catch (GooglePlayServicesNotAvailableException e)
                {
                    e.printStackTrace();
                }

            }
        });



        //Load data from database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get user id
                String uid = user.getUid();
                //get users name
                String firstName = (String) dataSnapshot.child(uid).child("name").getValue();
                //get user profile image
                String imageUrl = (String) dataSnapshot.child(uid).child("profileImage").getValue();

                //set user name text
                name.setText(firstName);


                //Check if user has uploaded image
                if(!TextUtils.isEmpty(imageUrl))
                {
                    //Load image into imageView, use placeholder image before user image is loaded
                    Picasso.with(getActivity()).load(imageUrl).placeholder(R.drawable.profileimage).into(profileImg);
                }
                else if (!TextUtils.isEmpty(imageUrl))
                {
                    //Use placeholder image if user has no profile image
                    Picasso.with(getActivity()).load(R.drawable.profileimage).into(profileImg);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(getActivity(), "Database Error", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == PLACE_PICKER_REQUEST)
        {
            if(resultCode == RESULT_OK)
            {
                Place place = PlacePicker.getPlace(getActivity(), data);

                String placeName = String.format("%s", place.getName());

                for(int i : place.getPlaceTypes())
                {
                    if(i == place.TYPE_FOOD || i == place.TYPE_RESTAURANT)
                    {
                        autoComplete.setText(placeName);
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "You can not eat at " + placeName + ". Please choose another place", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
    }


    //Hide the soft keyboard method
    public void hideKeyboard(View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //send users current location to the server
    public void sendLocation()
    {
        //get GPS location
        gps = new GPSLocation(getContext());

        location = gps.getLocation();


        if(location != null) {

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();


            //send users location to database
            mDatabase.child("users").child(user.getUid()).child("longitude").setValue(longitude);
            mDatabase.child("users").child(user.getUid()).child("latitude").setValue(latitude);
        }
    }



}
