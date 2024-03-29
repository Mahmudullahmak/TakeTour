package com.android.shamim.taketour;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.shamim.taketour.helper.FriendsAdapter;

import com.android.shamim.taketour.pojjoclass.Events;

import com.android.shamim.taketour.pojjoclass.Friends;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FriendList extends AppCompatActivity {

    private TextView show;
    DatabaseReference root;
    private FriendsAdapter friendsAdapter;
    public RecyclerView mRecyclerView;
    FirebaseUser user;
    private FirebaseAuth auth;
    private Events event;
    private FloatingActionButton fab;
    private AlertDialog.Builder b;
    private boolean validst = false;
    private String todate;
    private double restbudget;
    private double eventbudget;
    public double eventexpense = 0;
    public static final int MY_PERMISSIONS_REQUEST_PHONE = 5;
    public static final int SPLASH_DISPLAY_LENGTH = 10;
    public static final int RESULT_PICK_CONTACT = 12;
    private String phoneNumber;
    ArrayList<Friends> friends = new ArrayList<Friends>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        todate = df.format(Calendar.getInstance().getTime());
        Intent intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mRecyclerView = findViewById(R.id.mRecyclerView);
        show = findViewById(R.id.showmessage);
        //  FirebaseDatabase.getInstance ().setPersistenceEnabled ( true );
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        event = (Events) intent.getSerializableExtra("obj");
        eventbudget = event.getBudget();
        root = FirebaseDatabase.getInstance().getReference("Friends");
        root.keepSynced(true);
        toolbar.setTitle(event.getEventName() + " Friends List");
        setSupportActionBar(toolbar);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String eventTitle = intent.getStringExtra(SearchManager.QUERY);
            //call api using city name
            SearchRecentSuggestions searchRecentSuggestions =
                    new SearchRecentSuggestions(this,
                            CityNameSuggestions.AUTHORITY,
                            CityNameSuggestions.MODE);
            searchRecentSuggestions.saveRecentQuery(eventTitle, null);
            // Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show();
            // getSearchEventList(eventTitle);
            //getCurentWeatherinformationByArea(cityName);
        } else {
            getAllFriendList();
        }


    }

    public void getAllFriendList() {
        Query budgequery = root.orderByChild("eventid").equalTo(event.getEventID());
        budgequery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friends.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Friends ev = d.getValue(Friends.class);
                    friends.add(ev);

                }
                if (friends.size() > 0) {
                    friendsAdapter = new FriendsAdapter(FriendList.this, friends);
                    LinearLayoutManager llm = new LinearLayoutManager(FriendList.this);
                    //GridLayoutManager glm = new GridLayoutManager(context,1);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(llm);
                    mRecyclerView.setAdapter(friendsAdapter);
                    show.setVisibility(View.INVISIBLE);
                } else {
                    show.setText("You have no friend add. Please add friend");
                    show.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFriendDialog();
            }
        });


    }

    private void showAddFriendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Friend");
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_friend_dialog, null);

        builder.setView(alertLayout);
        builder.setCancelable(true);
        final EditText frname = (EditText) alertLayout.findViewById(R.id.fname);
        final EditText frphone = (EditText) alertLayout.findViewById(R.id.fphone);
        final EditText fremail = (EditText) alertLayout.findViewById(R.id.femail);
        Button addfriend = (Button) alertLayout.findViewById(R.id.addBtn);
        Button cancel = (Button) alertLayout.findViewById(R.id.cancel);

        final AlertDialog ad = builder.create();

        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fn = frname.getText().toString();
                String fp = frphone.getText().toString();
                String fe = fremail.getText().toString();

                String frid = root.push().getKey();
                String userid = user.getUid();
                if (fn.length() == 0) {
                    frname.setError("Enter Friend Name");
                    validst = false;
                } else {
                    validst = true;
                }
                if (fp.length() == 0) {
                    frphone.setError("Enter Friend Phone No");
                    validst = false;
                } else {
                    validst = true;
                }
                if (fe.length() == 0) {
                    fremail.setError("Enter Friend Email");
                    validst = false;
                } else {
                    validst = true;
                }

                if (validst) {

                    Friends frn = new Friends(frid, event.getEventID(), fn, fp, fe);
                    root.child(frid).setValue(frn);
                    Toast.makeText(FriendList.this, "Friend add success", Toast.LENGTH_SHORT).show();
                    //  startActivity(new Intent(EventDetail.this, EventDetail.class));

                    ad.dismiss();
                    ad.cancel();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
                ad.cancel();
            }
        });
        ad.show();
    }

    public void editFriendDialog(Friends friend) {
        final Friends frnd = friend;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Friend");
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_friend_dialog, null);

        builder.setView(alertLayout);
        builder.setCancelable(true);
        final EditText frname = (EditText) alertLayout.findViewById(R.id.fname);
        final EditText frphone = (EditText) alertLayout.findViewById(R.id.fphone);
        final EditText fremail = (EditText) alertLayout.findViewById(R.id.femail);
        Button addfriend = (Button) alertLayout.findViewById(R.id.addBtn);
        Button cancel = (Button) alertLayout.findViewById(R.id.cancel);
        frname.setText(frnd.getFriendName());
        frphone.setText(frnd.getFriendPhone());
        fremail.setText(frnd.getFriendEmail());
        addfriend.setText("Update");
        final AlertDialog ad = builder.create();

        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fn = frname.getText().toString();
                String fp = frphone.getText().toString();
                String fe = fremail.getText().toString();


                if (fn.length() == 0) {
                    frname.setError("Enter Friend Name");
                    validst = false;
                } else {
                    validst = true;
                }
                if (fp.length() == 0) {
                    frphone.setError("Enter Friend Phone No");
                    validst = false;
                } else {
                    validst = true;
                }
                if (fe.length() == 0) {
                    fremail.setError("Enter Friend Email");
                    validst = false;
                } else {
                    validst = true;
                }

                if (validst) {

                    Friends frn = new Friends(frnd.getFriendId(), event.getEventID(), fn, fp, fe);
                    root.child(frnd.getFriendId()).setValue(frn);
                    Toast.makeText(FriendList.this, "Friend Update success", Toast.LENGTH_SHORT).show();
                    //  startActivity(new Intent(EventDetail.this, EventDetail.class));

                    ad.dismiss();
                    ad.cancel();
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
                ad.cancel();
            }
        });
        ad.show();
    }


    public void logoutUser() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(FriendList.this, MainActivity.class));
                finish();
            }
        });
    }

    public void gotoweather(View view) {
        startActivity(new Intent(FriendList.this, WeatherInfo.class));
        finish();
    }

    public void deleteRecord(Friends eve) {
        final String exid = eve.getFriendId();
        b = new AlertDialog.Builder(FriendList.this);
        b.setTitle("Remove Friend");
        b.setMessage("Are you sure remove this friend?");
        b.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                deleteconfirm(exid);
            }
        });
        b.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        b.show();
    }

    public void deleteconfirm(String eid) {
        root.child(eid).removeValue();
    }


    /************************************** Menu Item Stsrt Here ************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem Searcc = menu.findItem(R.id.search);
        MenuItem CelsiusItem = menu.findItem(R.id.tempc);
        MenuItem FahrenheitItem = menu.findItem(R.id.tempf);
        MenuItem EventItem = menu.findItem(R.id.events);
        MenuItem MapItem = menu.findItem(R.id.location_map);
        MenuItem NearPlaceItem = menu.findItem(R.id.nearplace);
        MenuItem MapDirectionItem = menu.findItem(R.id.direction);
        MenuItem WeatherItem = menu.findItem(R.id.weather_info);
        MenuItem LogoutItem = menu.findItem(R.id.logout);
        MenuItem Myprofile = menu.findItem(R.id.profile);
        Searcc.setVisible(false);
        CelsiusItem.setVisible(false);
        FahrenheitItem.setVisible(false);
        EventItem.setVisible(true);
        MapItem.setVisible(true);
        NearPlaceItem.setVisible(true);
        MapDirectionItem.setVisible(true);
        WeatherItem.setVisible(true);
        if (user != null) {
            LogoutItem.setVisible(true);
            Myprofile.setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hm:
                startActivity(new Intent(this, EventList.class));
                break;
            case R.id.events:
                startActivity(new Intent(this, EventList.class));
                break;
            case R.id.location_map:
                startActivity(new Intent(this, LocationMap.class));
                break;
            case R.id.nearplace:

                startActivity(new Intent(this, NearestPlace.class));
                break;
            case R.id.direction:
                startActivity(new Intent(this, DirectionMap.class));
                break;
            case R.id.profile:
                startActivity(new Intent(this, UserProfile.class));
                break;
            case R.id.weather_info:
                startActivity(new Intent(this, WeatherInfo.class));
                break;
            case R.id.logout:
                logoutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /************************************** Menu Item End Here ************************************/
    public void callaPhone(String phoneNumber) {


    }

    public void checkPhonepermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE,
                            android.Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_PHONE);

            return;
        }
    }

    public void callPerson(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE,
                            android.Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_PHONE);

            return;
        }

        try {
            Intent my_callIntent = new Intent(Intent.ACTION_CALL);
            my_callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(my_callIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    public void sendMessage(String phoneNumber) {

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
    }

    public void sendEmail(String emailId) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + emailId));
        if (intent.resolveActivity(getPackageManager()) != null) {

            startActivity(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PHONE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent my_callIntent = new Intent(Intent.ACTION_CALL);
                            my_callIntent.setData(Uri.parse("tel:" + phoneNumber));
                            if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                               return;
                            }
                            startActivity(my_callIntent);
                            finish();
                        }
                    }, SPLASH_DISPLAY_LENGTH);

                } else {
                    finish();
                }
                return;
        }
    }

    public void pickContact(View v)
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("Add Friend ", "Failed to pick contact");
        }
    }
    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     * @param data
     */
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            // Set the value to the textviews

           // frphone.setText(phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
