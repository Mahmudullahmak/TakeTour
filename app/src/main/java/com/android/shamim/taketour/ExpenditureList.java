package com.android.shamim.taketour;

        import android.app.SearchManager;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.provider.SearchRecentSuggestions;
        import android.support.annotation.NonNull;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.SearchView;
        import android.support.v7.widget.Toolbar;
        import android.text.Html;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.shamim.taketour.helper.EventAdapter;
        import com.android.shamim.taketour.helper.ExpenditureAdapter;
        import com.android.shamim.taketour.helper.WeatherAdapter;
        import com.android.shamim.taketour.pojjoclass.Events;
        import com.android.shamim.taketour.pojjoclass.Expenditure;
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

public class ExpenditureList extends AppCompatActivity {
    private EditText eventNameET, eventBudgetET, eventDateTV;
    private TextView show;
    ListView eventList;
    DatabaseReference root;
    private ExpenditureAdapter expenditureAdapter;
    public RecyclerView mRecyclerView;
    FirebaseUser user;
    private FirebaseAuth auth;
    private Button saveBt;
    private Events event;
    private FloatingActionButton fab;
    public String updatedeventid = null;
    private  AlertDialog.Builder b;
    private boolean validst = false;
    private  String todate;
    private  double restbudget ;
    private double eventbudget;
    public  double eventexpense=0;
    ArrayList<Expenditure> expenditures = new ArrayList <Expenditure>( );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure_list);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        todate = df.format(Calendar.getInstance().getTime());
        Intent intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mRecyclerView = findViewById(R.id.mRecyclerView);
        show = findViewById(R.id.showmessage);
        //  FirebaseDatabase.getInstance ().setPersistenceEnabled ( true );
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
         event= (Events) intent.getSerializableExtra("obj");
        eventbudget = event.getBudget();
        root = FirebaseDatabase.getInstance ().getReference ("Expense");
        root.keepSynced ( true );
        toolbar.setTitle(event.getEventName()+ " Expense List");
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
            getAllExpenditureList();
        }






    }
    public  void  getAllExpenditureList() {
        Query budgequery = root.orderByChild( "eventid" ).equalTo(event.getEventID());
        budgequery.addValueEventListener ( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                expenditures.clear ();
                restbudget = 0;
                eventexpense =0;
                for (DataSnapshot d: dataSnapshot.getChildren ()){
                    Expenditure evs= d.getValue (Expenditure.class);
                    expenditures.add ( evs );
                    eventexpense += evs.getExpense();
                }
                restbudget = (eventbudget - eventexpense);
                if(expenditures.size() > 0){
                    expenditureAdapter = new ExpenditureAdapter(ExpenditureList.this ,expenditures);
                    LinearLayoutManager llm = new LinearLayoutManager(ExpenditureList.this);
                    //GridLayoutManager glm = new GridLayoutManager(context,1);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(llm);
                    mRecyclerView.setAdapter(expenditureAdapter);
                    show.setVisibility(View.INVISIBLE);
                } else {
                    show.setText("You have no Expense yet. Please add expense");
                    show.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddExpenditureDialog();
            }
        });


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
                        restbudget = (restbudget - expendValue) ;
                        ad.setCancelable(true);
                        validst = true;
                    }
                }
                if (validst) {

                    Expenditure ev = new Expenditure (exid, event.getEventID(),exValue,expendValue, todate);
                    root.child ( exid ).setValue ( ev );
                    Toast.makeText(ExpenditureList.this, "Expenditure add success", Toast.LENGTH_SHORT).show();
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

    public void editExpenseDialog( Expenditure expend) {
        final Expenditure exp = expend;
        final  double excost = exp.getExpense();
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_expense_dialog, null);
        builder.setTitle("Edit Expenditure");
        builder.setView(alertLayout);
        builder.setCancelable(true);
        final EditText exname  = (EditText) alertLayout.findViewById(R.id.description);
        final EditText expense  = (EditText) alertLayout.findViewById(R.id.expense);
       exname.setText(exp.getDescription());
        expense.setText(String.valueOf(exp.getExpense()));
        Button upbtn = (Button) alertLayout.findViewById(R.id.addBtn);
        Button cancel = (Button) alertLayout.findViewById(R.id.cancel);
        upbtn.setText("Update");
        final AlertDialog ad = builder.create();

        upbtn.setOnClickListener(new View.OnClickListener() {
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
                    if(excost == expendValue) {
                        ad.setCancelable(true);
                        validst = true;

                    } else if( excost >  expendValue) {
                        if ( expendValue > (restbudget + excost )) {
                            expense.setError("Youe expense exceed the budget!");
                            ad.setCancelable(false);
                            validst = false;
                        } else {
                            restbudget = ((restbudget - expendValue) + excost );

                            ad.setCancelable(true);
                            validst = true;
                        }

                    }
                }
                if (validst) {

                    Expenditure ev = new Expenditure (exp.getExpenseid(), exp.getEventid(),exValue,expendValue, exp.getCreatedate());
                    root.child ( exp.getExpenseid() ).setValue ( ev );
                    Toast.makeText(ExpenditureList.this, "Expenditure update success", Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent (ExpenditureList.this,MainActivity.class));
                finish();
            }
        });
    }

    public void gotoweather(View view) {
        startActivity(new Intent (ExpenditureList.this,WeatherInfo.class));
        finish();
    }
    public  void deleteRecord(Expenditure eve) {
        final String exid = eve.getExpenseid ();
        final  double  exc =  eve.getExpense();
        b = new AlertDialog.Builder(ExpenditureList.this);
        b.setTitle("Delete Expenditure");
        b.setMessage("Are you sure delete this expense?");
        b.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
        restbudget= (restbudget-exc);
                deleteconfirm(exid);
            }
        });
        b.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        b.show();
    }
    public void deleteconfirm(String eid)
    {
        root.child ( eid ).removeValue ();
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
    /************************************** Menu Item End Here ************************************/

}
