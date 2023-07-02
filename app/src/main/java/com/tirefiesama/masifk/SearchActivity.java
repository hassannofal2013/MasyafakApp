package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.savvi.rangedatepicker.CalendarPickerView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import static android.widget.Toast.LENGTH_SHORT;

public class SearchActivity extends AppCompatActivity {

    private Spinner spinner;
    private ArrayList<String> arrayList = new ArrayList<>();
    Calendar calendar;
    TextView startDay,endDay,search;
    String date;
    private ImageButton minusBtn,plusBtn;
    private EditText NoRooms;
    private Toolbar mtoolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Menu menu;

    boolean progressing= false;
    DatabaseReference searchRef;
    private FirebaseAuth mAuth;
    String UserID,Ststartdate,Stenddate,Ststartmonth,Stendmonth,Ststartyear,Stendyear;
    FirebaseUser mFirebaseUser;
    Date dateSpecified;
    ProgressBar progressBar;
    TextView Noresult;
    CardView spinnerCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        search= findViewById(R.id.searchtextView);
        progressBar= findViewById(R.id.progressBar);
        Noresult= findViewById(R.id.noresultTxtView);
        spinnerCard= findViewById(R.id.spinerCard);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        if (mFirebaseUser != null) { UserID = mAuth.getUid(); }


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Noresult.setVisibility(View.INVISIBLE);
                if( ! progressing) {

                    progressBar.setVisibility(View.VISIBLE);
                    appeareNoresult();

                    progressing = true;
                    mAuth = FirebaseAuth.getInstance();
                    mFirebaseUser = mAuth.getCurrentUser();

                    if (mFirebaseUser != null)
                    {
                        UserID = mAuth.getUid();

                        searchRef = FirebaseDatabase.getInstance().getReference().child("users").child(UserID).child("search");
                        searchRef.removeValue();
                        String cityname = spinner.getSelectedItem().toString();
                        String beds = NoRooms.getText().toString();

                        SearchCityName(cityname,beds);
                       // Toast.makeText(SearchActivity.this, cityname+beds, LENGTH_SHORT).show();
                        //Searchword(cityname,beds);


                    }  else
                        {
                            final AlertDialog alertadd = new AlertDialog.Builder(SearchActivity.this).create();
                            final LayoutInflater factory = LayoutInflater.from(SearchActivity.this);
                            View dialogView = factory.inflate(R.layout.sample, null);
                            TextView alerttextView = dialogView.findViewById(R.id.dialog_textView);
                            alerttextView.setText(getString(R.string.noaccount));
                            Button cancel =dialogView.findViewById(R.id.cancelBtn);
                            Button register =dialogView.findViewById(R.id.regestrationBtn);
                            progressing=false;
                            register.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(SearchActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    progressing=false;
                                    finish();
                                }
                            });

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertadd.dismiss();
                                }
                            });




                            alertadd.setView(dialogView);
                            alertadd.show();
                        }
                }
                else {

                    progressing=false;}

            }
        });
        drawerLayout = findViewById(R.id.drawer_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(SearchActivity.this, drawerLayout, R.string.Drawer_open, R.string.Drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        mtoolbar = findViewById(R.id.toolbar_tool);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.advancedsearch));
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navigationView=findViewById(R.id.navigation_view);
        menu = navigationView.getMenu();
        View navview = navigationView.inflateHeaderView(R.layout.navigation_header);

        minusBtn= findViewById(R.id.minusimageButton);
        plusBtn= findViewById(R.id.plusimageButton);
        NoRooms = findViewById(R.id.numberofunits);
        NoRooms.setText("1");

        NoRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Noresult.setVisibility(View.INVISIBLE);
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Noresult.setVisibility(View.INVISIBLE);
                if(  progressing)
                {
                    progressing=false;
                    progressBar.setVisibility(View.INVISIBLE);
                    Noresult.setVisibility(View.INVISIBLE);
                }
                int unitsint = Integer.parseInt(NoRooms.getText().toString());
                unitsint = unitsint - 1;
                NoRooms.setText(String.valueOf(unitsint));

                if (unitsint <= 0) {
                    NoRooms.setText("1");
                    Toast.makeText(SearchActivity.this, getResources().getString(R.string.minimumone), Toast.LENGTH_SHORT).show();
                }
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Noresult.setVisibility(View.INVISIBLE);
                if(  progressing)
                {
                    progressing=false;
                    progressBar.setVisibility(View.INVISIBLE);
                    Noresult.setVisibility(View.INVISIBLE);
                }
                int unitsint = Integer.parseInt(NoRooms.getText().toString());
                unitsint = unitsint + 1;
                NoRooms.setText(String.valueOf(unitsint));

                if (unitsint >= 20) {
                    NoRooms.setText("9");
                    Toast.makeText(SearchActivity.this, getResources().getString(R.string.maxis), Toast.LENGTH_SHORT).show();
                }
            }
        });


        startDay = findViewById(R.id.startdaytextView3);
        endDay = findViewById(R.id.enddaytextView4);

        spinner = findViewById(R.id.spinner);
        showDatainSpinner();

        spinnerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(  progressing)
                {
                    progressing=false;
                    progressBar.setVisibility(View.INVISIBLE);
                    Noresult.setVisibility(View.INVISIBLE);
                }
            }
        });


        calendar = Calendar.getInstance();
        date = DateFormat.getDateInstance().format(calendar.getTime());
        startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {


                Noresult.setVisibility(View.INVISIBLE);
                if(  progressing)
                {
                    progressing=false;
                    progressBar.setVisibility(View.INVISIBLE);
                    Noresult.setVisibility(View.INVISIBLE);
                }

                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText(R.string.selectstartdate);
                MaterialDatePicker<Long> picker = builder.build();
                picker.show(getSupportFragmentManager(), picker.toString());


                picker.addOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                       if(picker.getSelection() != null)
                       {utc.setTimeInMillis(picker.getSelection());
                        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                        String formatted = format.format(utc.getTime());
                       startDay.setText( formatted);}
                       else
                           {
                               Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                               SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                               String formatted = format.format(calendar.getTime());
                               startDay.setText( formatted);
                       }



                         }
                });

            }
        });

        endDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {


                Noresult.setVisibility(View.INVISIBLE);
                if(  progressing)
                {
                    progressing=false;
                    progressBar.setVisibility(View.INVISIBLE);
                    Noresult.setVisibility(View.INVISIBLE);
                }

                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText(R.string.selectenddate);
                MaterialDatePicker<Long> picker = builder.build();
                picker.show(getSupportFragmentManager(), picker.toString());


                picker.addOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                      if(picker.getSelection() != null)
                      {
                          utc.setTimeInMillis(picker.getSelection());
                          SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                          String formatted = format.format(utc.getTime());
                          endDay.setText(formatted);
                      }
                      else
                          {
                          Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                          SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                          String formatted = format.format(calendar.getTime());
                          endDay.setText( formatted);
                          }
                    }
                });



            }
        });

    }

    Runnable ranable = new Runnable() {
        @Override
        public void run() {
            Noresult.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            progressing=false;
        }
    };
    private void appeareNoresult()
    {
        Handler handler= new Handler();
        handler.postDelayed(ranable,1800);
    }

    private void SearchCityName(String cityname, String beds)
    {
        DatabaseReference SearchingTargetRef = FirebaseDatabase.getInstance().getReference().
                child("cities");

        SearchingTargetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot cities : dataSnapshot.getChildren())
                {
                    String   citykey = cities.getKey().toString();


                    SearchingTargetRef.child(citykey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            String bedsVal = dataSnapshot.child("name").getValue().toString();

                            if(bedsVal.equals(cityname))
                            {


                                   Searchword(citykey,beds);


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showDatainSpinner() {
        DatabaseReference marketRef = FirebaseDatabase.getInstance().getReference().child("cities");
        marketRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();

                for (DataSnapshot saller : dataSnapshot.getChildren()){
                    arrayList.add(saller.child("name").getValue().toString());
                }
                ArrayAdapter<String> arrayAdapter = new
                        ArrayAdapter<>(SearchActivity.this,R.layout.support_simple_spinner_dropdown_item,arrayList);

                spinner.setAdapter(arrayAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void initDatePicker(final TextView textView) {


        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Date date = new Date();

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);



             dateSpecified = c.getTime();


            if (dateSpecified.before(date)) {
                (textView).setTextColor(getResources().getColor(R.color.red));

                Toast.makeText(SearchActivity.this, "Selected Date is in Past", Toast.LENGTH_SHORT).show();
            } else {

                // DateSelected = dateSpecified;

                String predateNew = new SimpleDateFormat( getString(R.string.dateformat)).format(c.getTime());
                textView.setText(predateNew);

                if(textView.equals(startDay)){ Ststartdate=String.valueOf(dateSpecified); }
                if(textView.equals(endDay)){ Stenddate=String.valueOf(dateSpecified); }
            }


        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        Calendar min = Calendar.getInstance();
        min.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(min.getTimeInMillis());

        Calendar max = Calendar.getInstance();
        max.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH+3),
                calendar.get(Calendar.DAY_OF_MONTH) );
        dialog.getDatePicker().setMaxDate(max.getTimeInMillis());

        dialog.getDatePicker().setEnabled(true);
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent = new Intent(SearchActivity.this,MainActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private void Searchword( String city,String beds) {



        DatabaseReference SearchingTargetRef = FirebaseDatabase.getInstance().getReference().
                child("cities").child(city).child("ads");

        SearchingTargetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                for (DataSnapshot ads : dataSnapshot.getChildren()) {
                    String adkey = ads.getKey().toString();

                    if(!TextUtils.isEmpty(adkey))
                    {
                    SearchingTargetRef.child(adkey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            if (dataSnapshot.exists()) {
                                String bedsVal = dataSnapshot.child("beds").getValue().toString();

                                if (!TextUtils.isEmpty(bedsVal)) {
                                    if (bedsVal.equals(beds)) {
                                        //   Toast.makeText(getActivity(), postkey, LENGTH_SHORT).show();
                                        searchRef = FirebaseDatabase.getInstance().getReference().child("users").child(UserID).child("search");
                                        copytosearchKey(city, adkey);
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void copytosearchKey(String city, String adkey) {


        final DatabaseReference adRef = FirebaseDatabase.getInstance().getReference().
                child("cities").child(city).child("ads").child(adkey);

        adRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                searchRef.child(adkey).child("city").setValue(city);
                searchRef.child(adkey).child("adkey").setValue(adkey);
                if(dataSnapshot.child("adnumber").exists())
                {
                    String stavilabile=dataSnapshot.child("adnumber").getValue().toString();
                    searchRef.child(adkey).child("adnumber").setValue(stavilabile);
                }

                if(dataSnapshot.child("area").exists())
                {
                    String stavilabile=dataSnapshot.child("area").getValue().toString();
                    searchRef.child(adkey).child("area").setValue(stavilabile);
                }

                if(dataSnapshot.child("beds").exists())
                {
                    String stcomments=dataSnapshot.child("beds").getValue().toString();
                    searchRef.child(adkey ).child("beds").setValue(stcomments);
                }

                if(dataSnapshot.child("description").exists())
                {
                    String stdescription=dataSnapshot.child("description").getValue().toString();
                    searchRef.child(adkey ).child("description").setValue(stdescription);
                }

                if(dataSnapshot.child("image1").exists())
                {
                    String stimage1=dataSnapshot.child("image1").getValue().toString();
                    searchRef.child(adkey ).child("image1").setValue(stimage1);
                }

                if(dataSnapshot.child("image2").exists())
                {
                    String stimage2=dataSnapshot.child("image2").getValue().toString();
                    searchRef.child(adkey ).child("image2").setValue(stimage2);
                }

                if(dataSnapshot.child("image3").exists())
                {
                    String stimage3=dataSnapshot.child("image3").getValue().toString();
                    searchRef.child(adkey ).child("image3").setValue(stimage3);
                }

                if(dataSnapshot.child("image4").exists())
                {
                    String stimage4=dataSnapshot.child("image4").getValue().toString();
                    searchRef.child(adkey ).child("image4").setValue(stimage4);
                }
                if(dataSnapshot.child("image5").exists())
                {
                    String stimage4=dataSnapshot.child("image5").getValue().toString();
                    searchRef.child(adkey ).child("image5").setValue(stimage4);
                }
                if(dataSnapshot.child("image6").exists())
                {
                    String stimage4=dataSnapshot.child("image6").getValue().toString();
                    searchRef.child(adkey ).child("image6").setValue(stimage4);
                }

                if(dataSnapshot.child("location").exists())
                {
                    String stinbasket=dataSnapshot.child("location").getValue().toString();
                    searchRef.child(adkey ).child("location").setValue(stinbasket);
                }

                if(dataSnapshot.child("parking").exists())
                {
                    String stinbasket=dataSnapshot.child("parking").getValue().toString();
                    searchRef.child(adkey ).child("parking").setValue(stinbasket);
                }

                if(dataSnapshot.child("price").exists())
                {
                    String stprice=dataSnapshot.child("price").getValue().toString();
                    searchRef.child(adkey ).child("price").setValue(stprice);
                }

                if(dataSnapshot.child("rooms").exists())
                {
                    String stlikes=dataSnapshot.child("rooms").getValue().toString();
                    searchRef.child(adkey ).child("rooms").setValue(stlikes);
                }

                if(dataSnapshot.child("toilet").exists())
                {
                    String stprice=dataSnapshot.child("toilet").getValue().toString();
                    searchRef.child(adkey ).child("toilet").setValue(stprice);
                }
                if(dataSnapshot.child("wifi").exists())
                {
                    String stprice=dataSnapshot.child("wifi").getValue().toString();
                    searchRef.child(adkey ).child("wifi").setValue(stprice);
                }


                if(dataSnapshot.child("likes").exists())
                {
                    for(DataSnapshot userId : dataSnapshot.child("likes").getChildren() )
                    {
                        String uid = userId.getKey();
                       String stliketime= dataSnapshot.child("likes").child(uid).child("time").getValue().toString();
                        searchRef.child(adkey ).child("likes").child(uid).child("time").setValue(stliketime);
                    }
                }

                if(dataSnapshot.child("calender").exists())
                {
                    for(DataSnapshot monthId : dataSnapshot.child("calender").getChildren() )
                    {
                        String month = monthId.getKey();
                        for(DataSnapshot dayId : dataSnapshot.child("calender").child(month).getChildren())
                        {
                            String day = dayId.getKey();
                            String stliketime= dataSnapshot.child("calender").child(month).child(day).getValue().toString();
                            searchRef.child(adkey ).child("calender").child(month).child(day).setValue(stliketime);

                        }
                        }
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                 if (   !TextUtils.isEmpty(startDay.getText().toString())
                         &&
                         TextUtils.isEmpty(endDay.getText().toString())
                    )
                 {
                     SearchInPeriod();
                 }
                 else
                     {
                         Intent intent = new Intent(SearchActivity.this,SearchResultActivity.class);
                         startActivity(intent);
                         progressing=false;
                         finish();
                     }
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SearchInPeriod()
    {

        String Ststartday,Stendday;

        Ststartday= startDay.getText().toString().substring(8,10);
        Ststartmonth =startDay.getText().toString().substring(5,7);
        Ststartyear = startDay.getText().toString().substring(0,4);


        Stendday = endDay.getText().toString().substring(8,10);
        Stendmonth = endDay.getText().toString().substring(5,7);
        Stendyear = endDay.getText().toString().substring(0,4);




        if(Ststartday.startsWith("0")){Ststartday=Ststartdate.substring(9,10);}
        if(Stendday.startsWith("0")){Stendday=Stenddate.substring(9,10);}


        if(Ststartmonth.equals(Stendmonth))
        {

            int dif = Integer.parseInt(Stendday)-Integer.parseInt(Ststartday);
            for(int i = 0; i <= dif; i++)
            {
                int day = Integer.parseInt(Ststartday)+i;

                DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(UserID).child("search");

                deleteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.hasChildren())
                        {
                            for(DataSnapshot ad: snapshot.getChildren())
                            {
                                String Stad = ad.getKey().toString();

                                String Stday =String.valueOf(day);

                                if  (ad.child("calender").child(Stendmonth+"_"+Stendyear)
                                        .child(Stday).exists())

                                      {

                                    String dayValue = ad.child("calender").child(Stendmonth + "_" + Stendyear)
                                            .child(String.valueOf(day)).getValue().toString();
                                      //    Toast.makeText(SearchActivity.this, String.valueOf(day)+" is "+dayValue, LENGTH_SHORT).show();


                                    if(dayValue.equals("booked"))
                                    {
                                        deleteRef.child(Stad).removeValue();
                                    }



                                }
                            }
                        }



                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


                progressBar.setVisibility(View.INVISIBLE);
                Noresult.setVisibility(View.INVISIBLE);
            }


            Intent intent = new Intent(SearchActivity.this,SearchResultActivity.class);
            startActivity(intent);
            progressing=false;
            finish();
            // Fragment selectedfragment = new SearchResultFragment();
            //getSupportFragmentManager().beginTransaction().replace(R.id.main_container, selectedfragment).commit();



        }

        /*
        else
        {

            if(Ststartyear.equals(Stendyear))
            {

                String FirstMonNum = "null";
                if (Ststartmonth.equals("Jan")) {
                    FirstMonNum = "1";
                }
                if (Ststartmonth.equals("Fab")) {
                    FirstMonNum = "2";
                }
                if (Ststartmonth.equals("Mar")) {
                    FirstMonNum = "3";
                }
                if (Ststartmonth.equals("Apr")) {
                    FirstMonNum = "4";
                }
                if (Ststartmonth.equals("May")) {
                    FirstMonNum = "5";
                }
                if (Ststartmonth.equals("Jun")) {
                    FirstMonNum = "6";
                }
                if (Ststartmonth.equals("Jul")) {
                    FirstMonNum = "7";
                }
                if (Ststartmonth.equals("Aug")) {
                    FirstMonNum = "8";
                }
                if (Ststartmonth.equals("Sep")) {
                    FirstMonNum = "9";
                }
                if (Ststartmonth.equals("Oct")) {
                    FirstMonNum = "10";
                }
                if (Ststartmonth.equals("Nov")) {
                    FirstMonNum = "11";
                }
                if (Ststartmonth.equals("Dec")) {
                    FirstMonNum = "12";
                }

                String SecondMonNum = "null";
                if (Stendmonth.equals("Jan")) {
                    SecondMonNum = "1";
                }
                if (Stendmonth.equals("Fab")) {
                    SecondMonNum = "2";
                }
                if (Stendmonth.equals("Mar")) {
                    SecondMonNum = "3";
                }
                if (Stendmonth.equals("Apr")) {
                    SecondMonNum = "4";
                }
                if (Stendmonth.equals("May")) {
                    SecondMonNum = "5";
                }
                if (Stendmonth.equals("Jun")) {
                    SecondMonNum = "6";
                }
                if (Stendmonth.equals("Jul")) {
                    SecondMonNum = "7";
                }
                if (Stendmonth.equals("Aug")) {
                    SecondMonNum = "8";
                }
                if (Stendmonth.equals("Sep")) {
                    SecondMonNum = "9";
                }
                if (Stendmonth.equals("Oct")) {
                    SecondMonNum = "10";
                }
                if (Stendmonth.equals("Nov")) {
                    SecondMonNum = "11";
                }
                if (Stendmonth.equals("Dec")) {
                    SecondMonNum = "12";
                }


                String date = FirstMonNum + "/" + Ststartday + "/" + Ststartyear;    //"1/13/2021";
                LocalDate convertedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("M/d/yyyy"));
                convertedDate = convertedDate.withDayOfMonth(
                        convertedDate.getMonth().length(convertedDate.isLeapYear()));
                String Stlastday = String.valueOf(convertedDate).substring(8, 10);


                String StstartdaysecMonth = "1";
                if (Integer.parseInt(SecondMonNum) == Integer.parseInt(FirstMonNum) + 1) {

                    firstPeriodSamweMonth(Ststartday, Stlastday, Ststartmonth, Ststartyear);
                    firstPeriodSamweMonth(StstartdaysecMonth, Stendday, Stendmonth, Stendyear);
                }


                if (Integer.parseInt(SecondMonNum) == Integer.parseInt(FirstMonNum) + 2) {

                    int Secmonth = Integer.parseInt(FirstMonNum) + 1;
                    String StSecmonth = new String();
                    if (Secmonth == 1) {
                        StSecmonth = "Jan";
                    }
                    if (Secmonth == 2) {
                        StSecmonth = "Fab";
                    }
                    if (Secmonth == 3) {
                        StSecmonth = "Mar";
                    }
                    if (Secmonth == 4) {
                        StSecmonth = "Apr";
                    }
                    if (Secmonth == 5) {
                        StSecmonth = "May";
                    }
                    if (Secmonth == 6) {
                        StSecmonth = "Jun";
                    }
                    if (Secmonth == 7) {
                        StSecmonth = "Jul";
                    }
                    if (Secmonth == 8) {
                        StSecmonth = "Aug";
                    }
                    if (Secmonth == 9) {
                        StSecmonth = "Sep";
                    }
                    if (Secmonth == 10) {
                        StSecmonth = "Oct";
                    }
                    if (Secmonth == 11) {
                        StSecmonth = "Nov";
                    }
                    if (Secmonth == 12) {
                        StSecmonth = "Dec";
                    }
                    String date1 = Secmonth + "/" + "1" + "/" + Ststartyear;    //"1/13/2021";
                    LocalDate convertedDate1 = LocalDate.parse(date1, DateTimeFormatter.ofPattern("M/d/yyyy"));
                    convertedDate1 = convertedDate1.withDayOfMonth(
                            convertedDate1.getMonth().length(convertedDate1.isLeapYear()));
                    String Stlastday1 = String.valueOf(convertedDate1).substring(8, 10);


                    firstPeriodSamweMonth(Ststartday, Stlastday, Ststartmonth, Ststartyear);
                    firstPeriodSamweMonth("1", Stlastday1, StSecmonth, Ststartyear);
                    firstPeriodSamweMonth("1", Stendday, Stendmonth, Ststartyear);
                    //  Toast.makeText(SearchActivity.this, Stendmonth, LENGTH_SHORT).show();
                }

            }
            else
                {

                    String FirstMonNum = "null";
                    if (Ststartmonth.equals("Jan")) {
                        FirstMonNum = "1";
                    }
                    if (Ststartmonth.equals("Fab")) {
                        FirstMonNum = "2";
                    }
                    if (Ststartmonth.equals("Mar")) {
                        FirstMonNum = "3";
                    }
                    if (Ststartmonth.equals("Apr")) {
                        FirstMonNum = "4";
                    }
                    if (Ststartmonth.equals("May")) {
                        FirstMonNum = "5";
                    }
                    if (Ststartmonth.equals("Jun")) {
                        FirstMonNum = "6";
                    }
                    if (Ststartmonth.equals("Jul")) {
                        FirstMonNum = "7";
                    }
                    if (Ststartmonth.equals("Aug")) {
                        FirstMonNum = "8";
                    }
                    if (Ststartmonth.equals("Sep")) {
                        FirstMonNum = "9";
                    }
                    if (Ststartmonth.equals("Oct")) {
                        FirstMonNum = "10";
                    }
                    if (Ststartmonth.equals("Nov")) {
                        FirstMonNum = "11";
                    }
                    if (Ststartmonth.equals("Dec")) {
                        FirstMonNum = "12";
                    }

                    String lastMonNum = "null";
                    if (Stendmonth.equals("Jan")) {
                        lastMonNum = "1";
                    }
                    if (Stendmonth.equals("Fab")) {
                        lastMonNum = "2";
                    }
                    if (Stendmonth.equals("Mar")) {
                        lastMonNum = "3";
                    }
                    if (Stendmonth.equals("Apr")) {
                        lastMonNum = "4";
                    }
                    if (Stendmonth.equals("May")) {
                        lastMonNum = "5";
                    }
                    if (Stendmonth.equals("Jun")) {
                        lastMonNum = "6";
                    }
                    if (Stendmonth.equals("Jul")) {
                        lastMonNum = "7";
                    }
                    if (Stendmonth.equals("Aug")) {
                        lastMonNum = "8";
                    }
                    if (Stendmonth.equals("Sep")) {
                        lastMonNum = "9";
                    }
                    if (Stendmonth.equals("Oct")) {
                        lastMonNum = "10";
                    }
                    if (Stendmonth.equals("Nov")) {
                        lastMonNum = "11";
                    }
                    if (Stendmonth.equals("Dec")) {
                        lastMonNum = "12";
                    }


                    String date = FirstMonNum + "/" + Ststartday + "/" + Ststartyear;    //"1/13/2021";
                    LocalDate convertedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("M/d/yyyy"));
                    convertedDate = convertedDate.withDayOfMonth(
                            convertedDate.getMonth().length(convertedDate.isLeapYear()));
                    String Stlastday = String.valueOf(convertedDate).substring(8, 10);


                    String StstartdaysecMonth = "1";
                    if (Integer.parseInt(FirstMonNum) == 10)
                    {

                        firstPeriodSamweMonth(Ststartday, Stlastday, Ststartmonth, Ststartyear);
                        firstPeriodSamweMonth(Ststartday, Stlastday,"11" , Ststartyear);
                        firstPeriodSamweMonth(Ststartday, Stlastday,"12" , Ststartyear);
                        firstPeriodSamweMonth(StstartdaysecMonth, Stendday, lastMonNum, Stendyear);
                    }

                    if (Integer.parseInt(FirstMonNum) == 11)
                    {

                        firstPeriodSamweMonth(Ststartday, Stlastday, Ststartmonth, Ststartyear);
                        firstPeriodSamweMonth(Ststartday, Stlastday,"12" , Ststartyear);

                        if(Integer.parseInt(lastMonNum) == 1)
                        {
                        firstPeriodSamweMonth(StstartdaysecMonth, Stendday, lastMonNum, Stendyear);
                        }
                        if(Integer.parseInt(lastMonNum) == 2)
                        {
                            firstPeriodSamweMonth("1", "31", "1", Stendyear);
                            firstPeriodSamweMonth(StstartdaysecMonth, Stendday, lastMonNum, Stendyear);
                        }
                    }


                    if (Integer.parseInt(FirstMonNum) == 12)
                    {

                        firstPeriodSamweMonth(Ststartday, Stlastday,"12" , Ststartyear);

                        if(Integer.parseInt(lastMonNum) == 1)
                        {
                            firstPeriodSamweMonth(StstartdaysecMonth, Stendday, lastMonNum, Stendyear);
                        }
                        if(Integer.parseInt(lastMonNum) == 2)
                        {
                            firstPeriodSamweMonth("1", "31", "1", Stendyear);
                            firstPeriodSamweMonth(StstartdaysecMonth, Stendday, lastMonNum, Stendyear);
                        }

                        if(Integer.parseInt(lastMonNum) == 3)
                        {
                            firstPeriodSamweMonth("1", "31", "1", Stendyear);
                            firstPeriodSamweMonth("1", "28", "2", Stendyear);

                            firstPeriodSamweMonth(StstartdaysecMonth, Stendday, lastMonNum, Stendyear);
                        }


                    }





                }



        }


         */




    }

    private void firstPeriodSamweMonth(String Ststartday, String Stendday,String Stmonth,String Styear)
    {
        int dif = Integer.parseInt(Stendday)-Integer.parseInt(Ststartday);
        for(int i = 0; i <= dif; i++)
        {
            int day = Integer.parseInt(Ststartday)+i;
            DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(UserID).child("search");

            deleteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChildren())
                    {
                        for(DataSnapshot ad: snapshot.getChildren())
                        {
                            String Stad = ad.getKey().toString();
                            if(ad.child("calender").child(Stmonth+"_"+Styear)
                                    .child(String.valueOf(day)).exists())
                                  {
                                String dayValue = ad.child("calender").child(Stmonth + "_" + Styear)
                                        .child(String.valueOf(day)).getValue().toString();


                                      if(dayValue.equals("booked"))
                                {
                                    deleteRef.child(Stad).removeValue();
                                }

                                  }
                        }
                    }





                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });


        }



        // Fragment selectedfragment = new SearchResultFragment();
        // getSupportFragmentManager().beginTransaction().replace(R.id.main_container, selectedfragment).commit();
        Intent intent = new Intent(SearchActivity.this,SearchResultActivity.class);
        startActivity(intent);
        progressing=false;
        finish();

    }




}