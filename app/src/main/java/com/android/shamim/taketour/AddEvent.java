package com.android.shamim.taketour;

import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.android.shamim.taketour.helper.EventAdapter;
import com.android.shamim.taketour.pojjoclass.Events;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddEvent extends AppCompatActivity {
    private EditText eventNameET, eventBudgetET, eventDateTV;
    ListView eventList;
    DatabaseReference root;
    private EventAdapter eventAdapter;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;
    private Button saveBt;
    private FloatingActionButton fab;
    public String updatedeventid = null;
    public Events  event =null;
    private Calendar calendar;
    private  String userid;
    private boolean validst = false;
    private  String todate;
    int year,month,day,hour,min;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        todate = df.format(Calendar.getInstance().getTime());
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        eventNameET = findViewById ( R.id.eventName );
        eventBudgetET = findViewById ( R.id.eventBudget );
        eventDateTV = findViewById ( R.id.eventDate );
        saveBt = findViewById ( R.id.save );
       Intent intent = getIntent();
       event= (Events) intent.getSerializableExtra("obj");
        saveBt = findViewById ( R.id.save );
        if(event == null) {
            saveBt.setText ( "Save" );
            toolbar.setTitle("Create Event");
        } else {
            saveBt.setText ( "Update" );
            eventNameET.setText(event.getEventName());
            eventBudgetET.setText(String.valueOf(event.getBudget()));
            eventDateTV.setText(event.getEventDate());
            updatedeventid = event.getEventID();
            userid = event.getUserID();
            toolbar.setTitle("Edit Event");
        }
        setSupportActionBar(toolbar);
        root =FirebaseDatabase.getInstance ().getReference ("Events");
        root.keepSynced ( true );
    }

    public void saveEvent(View view) {
        final String eventName = eventNameET.getText().toString();
        final double eventBudget = Double.parseDouble (eventBudgetET.getText().toString());
        final String eventDate = eventDateTV.getText().toString();
        String eventId = root.push().getKey();
        String userid = user.getUid ();
        if (eventName.length() == 0) {
            eventNameET.setError("Enter Event Name");
            validst = false;
        } else {
            validst = true;
        }

        if (eventBudget == 0) {
            eventBudgetET.setError("Enter Event Budget");
            validst = false;
        } else {
            validst = true;
        }
        if (eventDate.length() == 0) {
            eventDateTV.setError("Enter Event Date");
            validst = false;
        } else {
            validst = true;
        }
        if (validst) {

            final String submitbutton  = saveBt.getText().toString();
            switch (submitbutton) {
                case "Save":
                    Events ev = new Events ( eventId,userid,eventName,eventBudget, eventDate,todate);
                    root.child ( eventId ).setValue ( ev );
                    startActivity(new Intent(AddEvent.this, EventList.class));

                    break;
                case "Update":
                    Events evu = new Events ( updatedeventid,userid,eventName,eventBudget, eventDate,event.getCreateDate());
                    root.child ( updatedeventid ).setValue ( evu );
                    startActivity(new Intent(AddEvent.this, EventList.class));
                    break;
            }
        }
    }
    public void selectDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                dateSetListener,year,month,day);
        calendar.add(Calendar.DATE, 1);
        Date newDate = calendar.getTime();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(year,month,dayOfMonth);
              SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            ((EditText)findViewById(R.id.eventDate)).setText(sdf.format(calendar.getTime()));
        }
    };
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
        if(user != null) {
            LogoutItem.setVisible(true);
            Myprofile.setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hm:
                  startActivity(new Intent(this,EventList.class));
                break;
            case R.id.events:
                  startActivity(new Intent(this,EventList.class));
                break;
            case R.id.location_map:
                startActivity(new Intent(this,LocationMap.class));
                break;
            case R.id.nearplace:

                startActivity(new Intent(this,NearestPlace.class));
                break;
            case R.id.direction:
                startActivity(new Intent(this,DirectionMap.class));
                break;
            case R.id.profile:
                startActivity(new Intent(this,UserProfile.class));
                break;
            case R.id.weather_info:
                startActivity(new Intent(this,WeatherInfo.class));
                break;
            case R.id.logout:
                logoutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void logoutUser() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent (AddEvent.this,LoginActivity.class));
                finish();
            }
        });
    }

    public void goToEvent(View view) {
        startActivity(new Intent (AddEvent.this,EventList.class));
        finish();
    }
    /************************************** Menu Item End Here ************************************/

  /*
    "yyyy.MM.dd G 'at' HH:mm:ss z" ---- 2001.07.04 AD at 12:08:56 PDT
"hh 'o''clock' a, zzzz" ----------- 12 o'clock PM, Pacific Daylight Time
            "EEE, d MMM yyyy HH:mm:ss Z"------- Wed, 4 Jul 2001 12:08:56 -0700
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ"------- 2001-07-04T12:08:56.235-0700
            "yyMMddHHmmssZ"-------------------- 010704120856-0700
            "K:mm a, z" ----------------------- 0:08 PM, PDT
"h:mm a" -------------------------- 12:08 PM
"EEE, MMM d, ''yy" ---------------- Wed, Jul 4, '01*/

}
