package com.foodfriend.foodfriend;

//import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

        import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by Dan on 21/02/2018.
 */

public class Tab2Matches extends Fragment {


    ArrayList<Match> arrayList;
    ArrayList<String> userids;

    ListView lv;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab2matches, container, false);

        arrayList = new ArrayList<>();
        userids = new ArrayList<>();

        lv = view.findViewById(R.id.listMatches);

        //FirebaseApp.initializeApp(getContext());

        final FirebaseAuth auth = FirebaseAuth.getInstance();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("users");






        //Load data from database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get current user logged in user id
                String currentUserID = auth.getCurrentUser().getUid();


                //initially clear the lists to avoid data being displayed multiple times and it updates live
                arrayList.clear();
                userids.clear();

                double currentUserLong = 0;
                double currentUserLat = 0;

                //get the current users location
                currentUserLong = Double.parseDouble(String.valueOf(dataSnapshot.child(currentUserID).child("longitude").getValue()));
                currentUserLat = Double.parseDouble(String.valueOf(dataSnapshot.child(currentUserID).child("latitude").getValue()));


                //for every child/userid in users
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    //get userid from each userdata
                    String uid = ds.getKey();

                    //if user id is not equal to current user id, do not add that user data (so user does not match with themselves)
                    if (!uid.equals(currentUserID)) {


                        String foodPOI = (String) ds.child("foodPOI").getValue();

                        //Log.v(TAG, "Foodpoi " + foodPOI);

                        //Check if user has entered foodPOI, this is so users without foodPOI are not displayed on matches
                        if(!foodPOI.isEmpty())
                        {
                            //If long and lat are not null
                            if (currentUserLong != 0 && currentUserLat != 0) {

                                //get users long and lat
                                double longitude = Double.parseDouble(String.valueOf(ds.child("longitude").getValue()));
                                double latitude = Double.parseDouble(String.valueOf(ds.child("latitude").getValue()));


                                //Location of current user
                                Location startPoint = new Location("startPoint");
                                startPoint.setLatitude(currentUserLat);
                                startPoint.setLongitude(currentUserLong);

                                //Location of potential match
                                Location endPoint = new Location("endPoint");
                                endPoint.setLatitude(latitude);
                                endPoint.setLongitude(longitude);

                                //distance between current user and matches
                                float distance = startPoint.distanceTo(endPoint);


                                //if the user is less than 16000 meters (10 miles) from the current user, issues may occur when using emulator because it sets GPS location to California
                                //set to 1600000000 because of
                                if (distance < 1600000000) {

                                    //add userids (keys) to an arraylist
                                    userids.add(uid);

                                    //get current date
                                    String currentDate = getDate();

                                    //get the date of from the users match data
                                    String date = (String) ds.child("date").getValue();

                                    //get time of day
                                    String time = (String) ds.child("time").getValue();

                                    //put date and time into 1 string
                                    String dateAndTime = (date + " \n" + time);

                                    //Check if users date matches current date, only display matches on same day (commented out for testing)
                                    //if(date == currentDate)
                                    //{
                                    //Create a new Match with all the required users data
                                    arrayList.add(new Match(
                                            (String) ds.child("profileImage").getValue(),
                                            (String) ds.child("name").getValue(),
                                            (String) ds.child("foodPOI").getValue(),
                                            dateAndTime
                                    ));
                                    //}
                                }
                            }
                        }


                    }
                }
                //Create adapter that will be used to apply all the data to the list, this uses Match objects which hold the user data
                CustomListAdapter adapter = new CustomListAdapter(getActivity().getApplicationContext(), R.layout.list_layout, arrayList);
                //set the adapter to the list
                lv.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(getActivity(), "Signed Out", Toast.LENGTH_SHORT).show();
            }

        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //list item click listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ChatActivity.class);

                //send the user who is being clicked to the chat activity, this is to start a chat with the match you click on
                intent.putExtra("sentToName", arrayList.get(i).getName());
                intent.putExtra("sentTo", userids.get(i));

                startActivity(intent);

            }
        });

    }

    public String getDate()
    {
        //get current date and send to database
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(today);

        return currentDate;
    }


}
