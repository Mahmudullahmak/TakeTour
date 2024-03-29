package com.android.shamim.taketour;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.shamim.taketour.helper.EventAdapter;
import com.android.shamim.taketour.helper.ExpandableListAdapter;
import com.android.shamim.taketour.pojjoclass.Events;
import com.android.shamim.taketour.pojjoclass.Expenditure;
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

public class EventDetail extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private Events event;
    private TextView enameTv, textView,cexTv,texTv;
    private  TextView ebudgetTv;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private  String userid ;
    private  String eventname;
    private  double restbudget ;
    private double eventbudget;
    public  double eventexpense=0;
    private  String eventid;
    private boolean validst = false;
    private  String todate;
    DatabaseReference root;
    private EventAdapter eventAdapter;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Event Detail");
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        todate = df.format(Calendar.getInstance().getTime());
        Intent intent = getIntent();
        event= (Events) intent.getSerializableExtra("obj");
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        enameTv= (TextView) findViewById(R.id.ename);
        ebudgetTv= (TextView) findViewById(R.id.ebudged);
        textView= (TextView) findViewById(R.id.pt);
        cexTv= (TextView) findViewById(R.id.cex);
        texTv= (TextView) findViewById(R.id.tex);
        progressBar = (ProgressBar) findViewById(R.id.budgetProgress);
        progressStatus = (int) ((float)Math.round(eventexpense * 100) / eventbudget);
        progressBar.setProgress(progressStatus);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        if(event != null) {
           userid = event.getUserID();
           eventname = event.getEventName();
           eventbudget  = event.getBudget();
           eventid = event.getEventID();
           enameTv.setText(event.getEventName());
           ebudgetTv.setText("Budget Status ( 0 / "+ eventbudget + "0 )");
        }
        cexTv.setText(String.valueOf(0)+"%");
        texTv.setText(String.valueOf(100)+"%");
        root =FirebaseDatabase.getInstance ().getReference ("Expense");
        root.keepSynced ( true );

        getAllExpense();
        // preparing list data
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);

// Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
              /*  Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
               /* Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();*/

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {


                switch(listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition)) {

                    case "Take a Photo":
                        startActivity(new Intent(EventDetail.this,TakeCameraPhoto.class).putExtra("obj",event));
                        break;
                    case "View Gallery":
                        startActivity(new Intent(EventDetail.this,EventGallery.class).putExtra("obj",event));
                        break;
                    case "View All Moments":
                        startActivity(new Intent(EventDetail.this,MomentDetail.class).putExtra("obj",event));
                        break;
                    case "Add New Expense":
                       showAddExpenditureDialog();
                        break;
                    case "Add More Budget":
                        showAddBudgetDialog();
                        break;
                    case "View All Expense":
                        startActivity(new Intent(EventDetail.this,ExpenditureList.class).putExtra("obj",event));
                        break;
                    case "Add Friend":
                        showAddFriendDialog();
                        break;
                    case "View Friend List":
                        startActivity(new Intent(EventDetail.this,FriendList.class).putExtra("obj",event));
                        break;
                    case "Add Geofencing Area":
                        startActivity(new Intent(EventDetail.this,AddGeofencing.class).putExtra("obj",event));
                        break;
                    case "View All Geofencing List":
                        startActivity(new Intent(EventDetail.this,GeofenceList.class).putExtra("obj",event));
                        break;

                }









                // TODO Auto-generated method stub
             /*   Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();*/
                return false;
            }
        });
    }
    public  void  getAllExpense() {
        Query budgequery = root.orderByChild( "eventid" ).equalTo(eventid);
        budgequery.addValueEventListener ( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventexpense= 0;
                for (DataSnapshot d: dataSnapshot.getChildren ()){
                    Expenditure ev= d.getValue (Expenditure.class);
                    eventexpense += ev.getExpense();
                }
                restbudget = eventbudget - eventexpense;
                ebudgetTv.setText("Budget Status ( "+ eventexpense +"0 / "+eventbudget + "0 )");
                if(eventexpense > 0){
                    progressStatus = (int) ((float)Math.round(eventexpense * 100) / eventbudget);
                    cexTv.setText(String.valueOf(progressStatus)+"%");
                    if(progressStatus > 80) {

                        progressBar.setProgress(progressStatus);
                        progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));

                    } else if(progressStatus >50) {

                        progressBar.setProgress(progressStatus);
                        progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
                    } else {
                        progressBar.setProgress(progressStatus);
                        progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                    }





                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

    }
    private void showAddFriendDialog() {
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Friend");
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_friend_dialog, null);

        builder.setView(alertLayout);
        builder.setCancelable(true);
        final EditText frname  = (EditText) alertLayout.findViewById(R.id.fname);
        final EditText frphone  = (EditText) alertLayout.findViewById(R.id.fphone);
        final EditText fremail  = (EditText) alertLayout.findViewById(R.id.femail);
        Button addfriend = (Button) alertLayout.findViewById(R.id.addBtn);
        Button cancel = (Button) alertLayout.findViewById(R.id.cancel);

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
                    DatabaseReference  dbfriend =FirebaseDatabase.getInstance ().getReference ("Friends");
                    String frid = dbfriend.push().getKey();
                    Friends frn = new Friends (frid, event.getEventID(),fn,fp, fe);
                    dbfriend.child ( frid ).setValue ( frn );
                    Toast.makeText(EventDetail.this, "Friend add success", Toast.LENGTH_SHORT).show();
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
    private void showAddExpenditureDialog() {
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Expenditure");
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_expense_dialog, null);

        builder.setView(alertLayout);
        builder.setCancelable(true);
        final EditText exname  = (EditText) alertLayout.findViewById(R.id.description);
        final EditText expense  = (EditText) alertLayout.findViewById(R.id.expense);
        Button login = (Button) alertLayout.findViewById(R.id.addBtn);
        Button cancel = (Button) alertLayout.findViewById(R.id.cancel);

        final AlertDialog ad = builder.create();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exValue = exname.getText().toString();
                double expendValue = Double.parseDouble (expense.getText().toString());

                String exid = root.push().getKey();
                String userid = user.getUid ();
                if (exValue.length() == 0) {
                    exname.setError("Enter Expense Detail");
                    validst = false;
                } else {
                    validst = true;
                }

                if (expendValue == 0) {
                    expense.setError("Enter Cost");
                    validst = false;

                } else {
                    if(restbudget < expendValue) {
                        expense.setError("Youe expense exceed the budget!");
                        ad.setCancelable(false);
                        validst = false;
                    } else {
                        ad.setCancelable(true);
                        validst = true;
                    }
                }
                 if (validst) {

                     Expenditure ev = new Expenditure (exid, event.getEventID(),exValue,expendValue, todate);
                            root.child ( exid ).setValue ( ev );
                     Toast.makeText(EventDetail.this, "Expenditure add success", Toast.LENGTH_SHORT).show();
                          //  startActivity(new Intent(EventDetail.this, EventDetail.class));
                     getAllExpense();
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
    private void showAddBudgetDialog() {
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Extra Budget");
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_budget, null);

        builder.setView(alertLayout);
        builder.setCancelable(true);
        final EditText exname  = (EditText) alertLayout.findViewById(R.id.description);
        final EditText expense  = (EditText) alertLayout.findViewById(R.id.expense);
        Button login = (Button) alertLayout.findViewById(R.id.addBtn);
        Button cancel = (Button) alertLayout.findViewById(R.id.cancel);

        final AlertDialog ad = builder.create();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exValue = exname.getText().toString();
                double expendValue = Double.parseDouble (expense.getText().toString());

                String exid = root.push().getKey();
                String userid = user.getUid ();

                if (expendValue == 0) {
                    expense.setError("Enter amount");
                    validst = false;

                } else {

                        validst = true;

                }
                if (validst) {
                    eventbudget = (eventbudget +  expendValue);
                    DatabaseReference  dbref =FirebaseDatabase.getInstance ().getReference ("Events");
                    Events ev = new Events (eventid,event.getUserID(),event.getEventName(), eventbudget,event.getEventDate(),event.getCreateDate());
                    dbref.child ( eventid ).setValue ( ev );
                    Toast.makeText(EventDetail.this, "Budget update success", Toast.LENGTH_SHORT).show();
                    //  startActivity(new Intent(EventDetail.this, EventDetail.class));
                    getAllExpense();
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
    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding Heading data
        listDataHeader.add("Expenditure");
        listDataHeader.add("Moments");
        listDataHeader.add("Friend List");
        listDataHeader.add("Geofencing");

        // Adding child data
        List<String> expenditure = new ArrayList<String>();
        expenditure.add("Add New Expense");
        expenditure.add("View All Expense");
        expenditure.add("Add More Budget");

        List<String> moments = new ArrayList<String>();
        moments.add("Take a Photo");
        moments.add("View Gallery");
        moments.add("View All Moments");

        List<String> friends = new ArrayList<String>();
        friends.add("Add Friend");
        friends.add("View Friend List");

        List<String> geofencing = new ArrayList<String>();
        geofencing.add("Add Geofencing Area");
        geofencing.add("View All Geofencing List");

        listDataChild.put(listDataHeader.get(0), expenditure); // Header, Child data
        listDataChild.put(listDataHeader.get(1), moments);
        listDataChild.put(listDataHeader.get(2), friends);
        listDataChild.put(listDataHeader.get(3), geofencing);
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
            case R.id.weather_info:
                startActivity(new Intent(this,WeatherInfo.class));
                break;
            case R.id.profile:
                startActivity(new Intent(this,UserProfile.class));
                break;
            case R.id.logout:
                logoutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /************************************** Menu Item End Here ************************************/

    public void logoutUser() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent (EventDetail.this,LoginActivity.class));
                finish();
            }
        });
    }

}