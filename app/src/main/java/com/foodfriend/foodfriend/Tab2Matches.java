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


                                //if the user is less than 32000 meters (20 miles) from the current user, issues may occur when using emulator because it sets GPS location to California
                                if (distance < 1600000000) {

                                    //add userids (keys) to an arraylist
                                    userids.add(uid);

                                    //get current date
                                    //String currentDate = getDate();

                                    //get the date of from the users match data
                                    String date = (String) ds.child("date").getValue();

                                    boolean dateFuture = false;

                                    dateFuture = checkDateFuture(date);

                                    //if the date chosen by user is in the future or same day
                                    if(dateFuture == true)
                                    {
                                        //get time of day
                                        String time = (String) ds.child("time").getValue();

                                        String dateConverted = convertDate(date);

                                        //put date and time into 1 string
                                        String dateAndTime = (dateConverted + ", " + time);

                                        //Get users name from database and send to method
                                        String firstName = removeSecondName((String) ds.child("name").getValue());

                                        //Create a new Match with all the required users data
                                        arrayList.add(new Match(
                                                (String) ds.child("profileImage").getValue(),
                                                firstName,
                                                (String) ds.child("foodPOI").getValue(),
                                                dateAndTime
                                        ));
                                    }

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

    //check if the date chosen by user is in the future or the same day as the current date, if not it should not be displayed in matches/friends
    public Boolean checkDateFuture(String date)
    {

        String currentDate = getDate();

        String[] splitCurrentDate = currentDate.split("-");

        String[] splitDate = date.split("/");

        int currentDateYear = Integer.parseInt(splitCurrentDate[2]);
        int currentDateMonth = Integer.parseInt(splitCurrentDate[1]);
        int currentDateDay = Integer.parseInt(splitCurrentDate[0]);

        int dateYear = Integer.parseInt(splitDate[2]);
        int dateMonth = Integer.parseInt(splitDate[1]);
        int dateDay = Integer.parseInt(splitDate[0]);

        if(dateYear > currentDateYear)
        {
            return true;
        }
        else if(dateYear == currentDateYear)
        {
            if(dateMonth > currentDateMonth)
            {
                return true;
            }
            else if (dateMonth == currentDateMonth)
            {
                if(dateDay >= currentDateDay)
                {
                    return true;
                }
            }
        }

        return false;
    }
    //Convert date to a more readable format (02/07/18 to 2nd July 18)
    public String convertDate(String date)
    {
        String[] splitDate = date.split("/");

        int dateMonth = Integer.parseInt(splitDate[1]);
        int dateDay = Integer.parseInt(splitDate[0]);
        int dateYear = Integer.parseInt(splitDate[2]);

        dateYear = dateYear - 2000;

        String yearString = Integer.toString(dateYear);

        String monthString;

        switch (dateMonth) {
            case 1:  monthString = "January";
                break;
            case 2:  monthString = "February";
                break;
            case 3:  monthString = "March";
                break;
            case 4:  monthString = "April";
                break;
            case 5:  monthString = "May";
                break;
            case 6:  monthString = "June";
                break;
            case 7:  monthString = "July";
                break;
            case 8:  monthString = "August";
                break;
            case 9:  monthString = "September";
                break;
            case 10: monthString = "October";
                break;
            case 11: monthString = "November";
                break;
            case 12: monthString = "December";
                break;
            default: monthString = "Invalid month";
                break;
        }
        String dayString;

        switch (dateDay) {
            case 1:  dayString = "1st";
                break;
            case 2:  dayString = "2nd";
                break;
            case 3:  dayString = "3rd";
                break;
            case 4:  dayString = "4th";
                break;
            case 5:  dayString = "5th";
                break;
            case 6:  dayString = "6th";
                break;
            case 7:  dayString = "7th";
                break;
            case 8:  dayString = "8th";
                break;
            case 9:  dayString = "9th";
                break;
            case 10: dayString = "10th";
                break;
            case 11: dayString = "11th";
                break;
            case 12: dayString = "12th";
                break;
            case 13:  dayString = "13th";
                break;
            case 14:  dayString = "14th";
                break;
            case 15:  dayString = "15th";
                break;
            case 16:  dayString = "16th";
                break;
            case 17:  dayString = "17th";
                break;
            case 18:  dayString = "18th";
                break;
            case 19:  dayString = "19th";
                break;
            case 20:  dayString = "20th";
                break;
            case 21:  dayString = "21st";
                break;
            case 22: dayString = "22nd";
                break;
            case 23: dayString = "23rd";
                break;
            case 24: dayString = "24th";
                break;
            case 25: dayString = "25th";
                break;
            case 26: dayString = "26th";
                break;
            case 27: dayString = "27th";
                break;
            case 28: dayString = "28th";
                break;
            case 29: dayString = "29th";
                break;
            case 30: dayString = "30th";
                break;
            case 31: dayString = "31th";
                break;
            default: dayString = "Invalid Day";
                break;
        }


        String convertedDate = dayString + " " + monthString + " " + yearString;

        return convertedDate;
    }

    //Remove users second name, if no second name is present just return the original string
    public String removeSecondName(String name)
    {
        String[] splitName = name.split(" ");

        if(splitName.length > 1)
        {
            String firstName = splitName[0];

            return firstName;
        }
        else
        {
            return name;
        }
    }

}
