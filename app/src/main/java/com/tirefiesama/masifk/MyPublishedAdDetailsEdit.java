package com.tirefiesama.masifk;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.savvi.rangedatepicker.CalendarPickerView;
import com.savvi.rangedatepicker.SubTitle;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class MyPublishedAdDetailsEdit extends AppCompatActivity {
   static String adkey ,userId,citykey;
    String imagename,StOldCity;
    String stscreenshot="",stimage1="",stimage2="",stimage3="",stimage4="",stimage5="",stimage6="";
    private DatabaseReference mDatabase,publishedRef,requestedRef;
    ImageView image1,image2,image3,image4,image5,image6,screenshot;
    LinearLayout linearLayout1,linearLayout2,linearLayout3;
    EditText price,rooms,description,city,locationETxt,area,beds,toilet;
    TextView adNumber,requestTime,status;
    FloatingActionButton call;
    private Toolbar mtoolbar;
    final ArrayList<SubTitle> subTitles = new ArrayList<>();

    private CardView delete,saveChanages,locationRequest;
    FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    final static int Gallary_pic = 1;
    boolean selectImage1, selectImage2, selectImage3, selectImage4, selectImage5, selectImage6,
            loadedimage1, loadedimage2, loadedimage3, loadedimage4, loadedimage5, loadedimage6,
            saveboolean=false,mapMade=false;
    private Uri ImageData, Image1Data, Image2Data, Image3Data, Image4Data, Image5Data, Image6Data;

    StorageReference UserProfileImageRef;
    DatabaseReference userRef;
    DatabaseReference adsRef;
    FusedLocationProviderClient fusedLocationProviderClient;

    private CheckBox parrkingCheckBox,wifiCheckBox;
    private PlacesClient mPlacesClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    TextView  choosen_days, choosendaysis;
    CalendarPickerView calendarPicker;
    ArrayList<Date> DeactevatedList = new ArrayList<>();


    CardView ok,cancel;
    GoogleMap mMap;

    Boolean screenshotteken=false,editdesBool=false,editdayBool=false;
    CheckBox editdesBox,editdaysBox;
    LinearLayout maplayout;
    String stprice, strooms, stdescription, stcity, stlocation, stbeds, sttoilet, starea;
    HashMap admap = new HashMap();



    private static final String TAG = "MapsActivity";

    private Location mLastKnownLocation;


    private final LatLng mDefaultLocation = new LatLng(31.600116, 31.089007);
    private static final int DEFAULT_ZOOM = 45;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted, loadedscreenshot;

    // Used for selecting the Current Place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    ScrollView scrollView;
    ImageView currentlocationImage;
    String Stlocation,chosenmonth, chosenyear,cityname;
    ScrollView dayslayouy;
    String strdate,month1,year1,day1;
    Calendar calendar,calendar1,calendar2,calendar3, tmrw;
    TextView addBooked,addUnbooked;
    int Val1=0,restoffirstMon=0;
    int days1 = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_published_ad_details_edit);



        SharedPreferences preferences = getSharedPreferences("sharedFile",MODE_PRIVATE);
        adkey = preferences.getString("adkey","adkey");
        citykey = preferences.getString("citykey","citykey");




        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        userId=mAuth.getUid().toString();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MyPublishedAdDetailsEdit.this);
        tmrw = Calendar.getInstance();
        call = findViewById(R.id.fab);
        price = findViewById(R.id.pricetextView3);
        rooms = findViewById(R.id.roomstextView);
        description = findViewById(R.id.descriptiontextView);
        adNumber = findViewById(R.id.adnutextView);
        city = findViewById(R.id.cityTextView);
        requestTime  = findViewById(R.id.publishingtime);
        locationETxt = findViewById(R.id.locationtextView46);
        currentlocationImage  = findViewById(R.id.currentlocationimageView);
        editdesBox = findViewById(R.id.editDescheckBox);
        editdaysBox = findViewById(R.id.editAvilabledayscheckBox3);
        dayslayouy = findViewById(R.id.dayslayout);
        choosen_days =findViewById(R.id.choosendays);
        choosendaysis = findViewById(R.id.textView33);
        calendarPicker = findViewById(R.id.calendar_view);
        addBooked= findViewById(R.id.addbookedtextView);
        addUnbooked= findViewById(R.id.addunbookedtextView);

        maplayout = findViewById(R.id.maplayout);
        cancel = findViewById(R.id.cancelcardview);
        ok = findViewById(R.id.okcardview);

        area= findViewById(R.id.areatextView3);
        beds= findViewById(R.id.bedtextView);
        toilet= findViewById(R.id.toilettextView3);

        status = findViewById(R.id.adstatus);
        saveChanages = findViewById(R.id.savecard);
        delete = findViewById(R.id.deletecard);
        locationRequest = findViewById(R.id.locationcard2);

        wifiCheckBox = findViewById(R.id.wificheckBox);
        parrkingCheckBox=findViewById(R.id.parkingcheckBox);
        scrollView = findViewById(R.id.scrollviewmyad);

        image1 = findViewById(R.id.imageView1);
        image2 = findViewById(R.id.imageView2);
        image3 = findViewById(R.id.imageView3);
        image4 = findViewById(R.id.imageView4);
        image5 = findViewById(R.id.imageView5);
        image6 = findViewById(R.id.imageView6);
        screenshot = findViewById(R.id.screenshotimageView);

        linearLayout1 = findViewById(R.id.linearLayout1);
        linearLayout2 = findViewById(R.id.linearLayout2);
        linearLayout3 = findViewById(R.id.linearLayout3);
        mtoolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(getString(R.string.myad));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addBooked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeMap();
                if(mapMade)
                {
                    String StChosenDays = choosen_days.getText().toString();
                    MakeDaysBusyOwnerPublishedAds(userId,citykey,adkey,StChosenDays);
                    editdaysBox.setChecked(false);
                    String addedto= getString(R.string.addedtobooked);
                    sendEmail(addedto);

                    AddToTransactions();
                    recreate();
                    Toast.makeText(MyPublishedAdDetailsEdit.this, getString(R.string.addedsuccessfully), Toast.LENGTH_SHORT).show();
                }
            }
        });

        addUnbooked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeMap();
                if(mapMade)
                {
                    String StChosenDays = choosen_days.getText().toString();
                    MakeDaysUnbookedOwnerPublishedAds(userId,citykey,adkey,StChosenDays);
                    editdaysBox.setChecked(false);
                    String addedto= getString(R.string.addedtounbooked);
                    sendEmail(addedto);

                    AddToTransactions();
                    recreate();
                    Toast.makeText(MyPublishedAdDetailsEdit.this, getString(R.string.addedsuccessfully), Toast.LENGTH_SHORT).show();
                }
            }
        });

        calendarPicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {

                choosen_days.setVisibility(View.VISIBLE);
                choosendaysis.setVisibility(View.VISIBLE);

                chosenyear=String.valueOf(calendarPicker.getSelectedDate()).substring(30,34);

                chosenmonth=String.valueOf(calendarPicker.getSelectedDate()).substring(0,3);
                List <Date> selectedDays = calendarPicker.getSelectedDates();
                StringBuilder builder = new StringBuilder();
                for(Date date1: selectedDays )
                {
                    String strdate1=String.valueOf(date1).substring(4,10);
                    String styear=String.valueOf(date1).substring(30,34);


                    builder.append(strdate1+" "+styear + "\n");

                }
                choosen_days.setText(builder);

            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
        calendar = Calendar.getInstance();

        calendar.add(Calendar.MONTH, +0);
        month1 = calendar.getTime().toString().substring(4,7);
        year1 = calendar.getTime().toString().substring(30,34);
        day1 = calendar.getTime().toString().substring(8,10);
        getNomDays(month1,year1);
        calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        readSchedualDataInCurrentMonth(day1,month1,year1);



        calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.MONTH, +1);
        month1 = calendar1.getTime().toString().substring(4,7);
        year1 = calendar1.getTime().toString().substring(30,34);
        readSchedualData(month1,year1,days1);


        createCalenderPicker();
        editdaysBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

             editdesBox.setChecked(false);
            editdayBool=true;
            editdesBool=false;
            dayslayouy.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);

            }
        });

        editdesBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                editdaysBox.setChecked(false);
                editdayBool=false;
                editdesBool=true;
                dayslayouy.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

            }
        });

        currentlocationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCurrentPlace();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maplayout.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                mtoolbar.setVisibility(View.VISIBLE);
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureScreen();
                maplayout.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                mtoolbar.setVisibility(View.VISIBLE);
                screenshotteken=true;

            }
        });


        screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openmap();
                hideSoftKeyboard(MyPublishedAdDetailsEdit.this);
            }
        });
        locationRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openmap();
                hideSoftKeyboard(MyPublishedAdDetailsEdit.this);

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new androidx.appcompat.app.AlertDialog.Builder(MyPublishedAdDetailsEdit.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.removingad))
                        .setMessage(getString(R.string.sureRemove))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.MONTH, +0);
                                String month = calendar.getTime().toString().substring(4, 7);
                                String year = calendar.getTime().toString().substring(30, 34);
                                String day = calendar.getTime().toString().substring(8, 10);
                                String monthkey = month + "_" + year;
                                progressDialog = ProgressDialog.show(MyPublishedAdDetailsEdit.this,
                                        getString(R.string.wait),getString(R.string.deleting),false,false);
                                DatabaseReference MonitorRef = FirebaseDatabase.getInstance().getReference().
                                        child("transactions").child("addeleted").child(monthkey).child(day).child(adkey);


                                UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("ads").child(userId)
                                        .child(adkey);
                                publishedRef= FirebaseDatabase.getInstance().getReference().child("cities").child(citykey).
                                        child("ads").child(adkey);

                                publishedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        if(snapshot.exists())
                                        {

                                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                            {
                                                String info = dataSnapshot.getKey();
                                                String valu = dataSnapshot.getValue().toString();
                                                MonitorRef.child(info).setValue(valu);
                                            }

                                            if(snapshot.child("calender").exists())
                                            {
                                                for(DataSnapshot monthSnapshot : snapshot.child("calender").getChildren())
                                                {
                                                    String month = monthSnapshot.getKey();
                                                    for(DataSnapshot daySnapshot : snapshot.child("calender").child(month).getChildren())
                                                    {
                                                        String day = daySnapshot.getKey();
                                                        String valu = daySnapshot.child("calender").child(month).child(day).getValue().toString();
                                                        MonitorRef.child("calender").child(month).child(day).setValue(valu);
                                                    }
                                                }
                                            }
                                            MonitorRef.child("adminkey").setValue(userId);
                                            MonitorRef.child("citykey").setValue(citykey);




                                            UserProfileImageRef.child("screenshot").delete();
                                            UserProfileImageRef.child("image1").delete();
                                            UserProfileImageRef.child("image2").delete();
                                            UserProfileImageRef.child("image3").delete();
                                            UserProfileImageRef.child("image4").delete();
                                            UserProfileImageRef.child("image5").delete();
                                            UserProfileImageRef.child("image6").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").
                                                            child(userId).child("published").child(adkey);

                                                    requestedRef = FirebaseDatabase.getInstance().getReference().
                                                            child("cities").child(citykey).child("ads").child(adkey);



                                                    publishedRef.removeValue();
                                                    mDatabase.removeValue();
                                                    requestedRef.removeValue();

                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(MyPublishedAdDetailsEdit.this,MainActivity.class);
                                                    startActivity(intent);
                                                    Toast.makeText(MyPublishedAdDetailsEdit.this, R.string.deletead, Toast.LENGTH_SHORT).show();

                                                }
                                            });




                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();



            }
        });


        saveChanages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new androidx.appcompat.app.AlertDialog.Builder(MyPublishedAdDetailsEdit.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.removingad))
                        .setMessage(getString(R.string.sureRemoveAndRequest))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                saveboolean=true;
                                if(saveboolean)
                                {
                                    saveboolean = false;
                                    makeMap();


                                }


                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();



            }
        });




        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog alertadd = new AlertDialog.Builder(MyPublishedAdDetailsEdit.this).create();
                final LayoutInflater factory = LayoutInflater.from(MyPublishedAdDetailsEdit.this);
                View dialogView = factory.inflate(R.layout.sample_dialog_call, null);

                CardView Call = dialogView.findViewById(R.id.callcard);

                CardView Whats =dialogView.findViewById(R.id.whatsCard);

                Call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:+201200207208"));
                        startActivity(intent);
                        alertadd.dismiss();

                    }
                });

                Whats.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        String url = "https://api.whatsapp.com/send?phone="+"+201200207208";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        alertadd.dismiss();
                    }
                });




                alertadd.setView(dialogView);
                alertadd.show();



            }
        });
        if(adkey != null)
        {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("published").child(adkey);
            mDatabase.keepSynced(true);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.child("wifi").exists())
                    {
                        String imageSt = snapshot.child("wifi").getValue().toString();
                        if(imageSt.equals("true"))
                        {wifiCheckBox.setChecked(true);}
                    }

                    if(snapshot.child("parking").exists())
                    {
                        String imageSt = snapshot.child("parking").getValue().toString();
                        if(imageSt.equals("true"))
                        {parrkingCheckBox.setChecked(true);}
                    }

                    if(snapshot.child("image1").exists())
                    {
                        String imageSt = snapshot.child("image1").getValue().toString();
                        Picasso.get().load(imageSt).into(image1);
                        linearLayout1.setVisibility(View.VISIBLE);
                    }

                    if(snapshot.child("image2").exists())
                    {
                        String imageSt = snapshot.child("image2").getValue().toString();
                        Picasso.get().load(imageSt).into(image2);
                    }

                    if(snapshot.child("image3").exists())
                    {
                        String imageSt = snapshot.child("image3").getValue().toString();
                        Picasso.get().load(imageSt).into(image3);
                        linearLayout2.setVisibility(View.VISIBLE);
                    }

                    if(snapshot.child("image4").exists())
                    {
                        String imageSt = snapshot.child("image4").getValue().toString();
                        Picasso.get().load(imageSt).into(image4);
                    }

                    if(snapshot.child("image5").exists())
                    {
                        String imageSt = snapshot.child("image5").getValue().toString();
                        Picasso.get().load(imageSt).into(image5);
                        linearLayout3.setVisibility(View.VISIBLE);
                    }

                    if(snapshot.child("image6").exists())
                    {
                        String imageSt = snapshot.child("image6").getValue().toString();
                        Picasso.get().load(imageSt).into(image6);
                    }

                    if(snapshot.child("screenshot").exists())
                    {
                        String imageSt = snapshot.child("screenshot").getValue().toString();
                        Picasso.get().load(imageSt).into(screenshot);
                    }

                    if(snapshot.child("price").exists())
                    {
                        String St = snapshot.child("price").getValue().toString();
                        price.setText(St);
                    }

                    if(snapshot.child("rooms").exists())
                    {
                        String St = snapshot.child("rooms").getValue().toString();
                        rooms.setText(St);
                    }
                    if(snapshot.child("description").exists())
                    {
                        String St = snapshot.child("description").getValue().toString();
                        description.setText(St);
                    }
                    if(snapshot.child("adnumber").exists())
                    {


                        mtoolbar.setTitle(getString(R.string.adnuis)+adkey);
                        adNumber.setText(getString(R.string.adnuis)+adkey);

                    }

                    if(snapshot.child("city").exists())
                    {
                        StOldCity = snapshot.child("city").getValue().toString();
                        city.setText(StOldCity);
                    }

                    if(snapshot.child("location").exists())
                    {
                        Stlocation = snapshot.child("location").getValue().toString();
                        locationETxt.setText(Stlocation);
                    }


                    if(snapshot.child("toilet").exists())
                    {
                        String St = snapshot.child("toilet").getValue().toString();
                        toilet.setText(St);
                    }

                    if(snapshot.child("beds").exists())
                    {
                        String St = snapshot.child("beds").getValue().toString();
                        beds.setText(St);
                    }

                    if(snapshot.child("area").exists())
                    {
                        String St = snapshot.child("area").getValue().toString();
                        area.setText(St);
                    }


                    if(snapshot.child("status").exists())
                    {
                        String St = snapshot.child("status").getValue().toString();
                        status.setText(St);
                    }

                    if(snapshot.child("time").exists())
                    {
                        String St = snapshot.child("time").getValue().toString();
                        requestTime.setText(St);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                image1.setBackground(null);
                selectImage1=true;
                openGallary(image1);
                image2.setVisibility(View.VISIBLE);
                image2.setBackground(getDrawable(R.drawable.logo_head));
            }
        });


        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                image2.setBackground(null);
                selectImage2=true;
                openGallary(image2);
                image3.setVisibility(View.VISIBLE);
                image3.setBackground(getDrawable(R.drawable.logo_head));
                linearLayout2.setVisibility(View.VISIBLE);
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                image3.setBackground(null);
                selectImage3=true;
                openGallary(image3);
                image4.setVisibility(View.VISIBLE);
                image4.setBackground(getDrawable(R.drawable.logo_head));
            }
        });


        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                image4.setBackground(null);
                selectImage4 = true ;
                openGallary(image4) ;
                image5.setVisibility(View.VISIBLE);
                image5.setBackground(getDrawable(R.drawable.logo_head));
                linearLayout3.setVisibility(View.VISIBLE);
            }
        });


        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                image5.setBackground(null);
                selectImage5=true;
                openGallary(image5);
                image6.setVisibility(View.VISIBLE);
                image6.setBackground(getDrawable(R.drawable.logo_head));
            }
        });


        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                image6.setBackground(null);
                selectImage6=true;
                openGallary(image6);
            }
        });



    }

    private void updateDataBase()
    {
         DatabaseReference publishedAdsRef = FirebaseDatabase.getInstance().getReference().
                child("cities").child(citykey).child("ads").child(adkey);
        DatabaseReference adRequestRef = FirebaseDatabase.getInstance().getReference().
                child("adsrequests").child(adkey);
        DatabaseReference userPublished = FirebaseDatabase.getInstance().getReference().
                child("users").child(userId).child("published").child(adkey);
        DatabaseReference userAdrequest = FirebaseDatabase.getInstance().getReference().
                child("users").child(userId).child("adsrequests").child(adkey);

        adRequestRef.updateChildren(admap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                adRequestRef.child("status").setValue("جاري العرض على المشرف");



                userAdrequest.updateChildren(admap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        userAdrequest.child("status").setValue("جاري العرض على المشرف");

                        publishedAdsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {

                                if(snapshot.child("calender").exists())
                                {
                                    for(DataSnapshot monthkey : snapshot.child("calender").getChildren())
                                    {
                                        String StMonth = monthkey.getKey();
                                        for(DataSnapshot daykey : snapshot.child("calender").child(StMonth).getChildren())
                                        {
                                            String Stday = daykey.getKey();
                                            String dayValue = snapshot.child("calender").child(StMonth).child(Stday).getValue().toString();
                                            userAdrequest.child("calender").child(StMonth).child(Stday).setValue(dayValue);
                                            adRequestRef.child("calender").child(StMonth).child(Stday).setValue(dayValue);
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });


            }
        });



        userPublished.removeValue();
        publishedAdsRef.removeValue();
        progressDialog.dismiss();

        Intent intent = new Intent(MyPublishedAdDetailsEdit.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void makeMap()
    {

        progressDialog = ProgressDialog.show(MyPublishedAdDetailsEdit.this,
                getString(R.string.wait),getString(R.string.uploading),false,false);
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        String saveCurrentDate = Date.format(calFordDate.getTime()) + time.format(calFordDate.getTime());



        stprice = price.getText().toString();
        strooms = rooms.getText().toString();
        stcity = city.getText().toString();
        stdescription = description.getText().toString();
        stlocation = locationETxt.getText().toString();
        stbeds = beds.getText().toString();
        sttoilet = toilet.getText().toString();
        starea = area.getText().toString();


        DatabaseReference publishedAdsRef = FirebaseDatabase.getInstance().getReference().
                child("cities").child(citykey).child("ads").child(adkey);

        publishedAdsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                  if(snapshot.child("screenshot").exists())
                {
                   stscreenshot = snapshot.child("screenshot").getValue().toString();

                }


                if(snapshot.child("image1").exists())
                {
                    stimage1 = snapshot.child("image1").getValue().toString();

                }

                if(snapshot.child("image2").exists())
                {
                    stimage2 = snapshot.child("image2").getValue().toString();
                 }

                if(snapshot.child("image3").exists())
                {
                    stimage3= snapshot.child("image3").getValue().toString();
               }

                if(snapshot.child("image4").exists())
                {
                    stimage4 = snapshot.child("image4").getValue().toString();
               }

                if(snapshot.child("image5").exists())
                {
                    stimage5 = snapshot.child("image5").getValue().toString();
                }

                if(snapshot.child("image6").exists())
                {
                    stimage6 = snapshot.child("image6").getValue().toString();
                }

                if (!TextUtils.isEmpty(stprice)) {
                    if (!TextUtils.isEmpty(strooms)) {
                        if (!TextUtils.isEmpty(stcity)) {
                            if (!TextUtils.isEmpty(stbeds)) {
                                if (!TextUtils.isEmpty(sttoilet)) {
                                    if (!TextUtils.isEmpty(starea)) {
                                        if (!TextUtils.isEmpty(stdescription)) {
                                            if (!TextUtils.isEmpty(stlocation)) {



                                                admap.put("description", stdescription);
                                                admap.put("location", stlocation);
                                                admap.put("price", stprice);
                                                admap.put("rooms", strooms);
                                                admap.put("time", saveCurrentDate);
                                                admap.put("adnumber", adkey);
                                                admap.put("city", stcity);

                                                admap.put("ownerkey", userId);
                                                admap.put("beds", stbeds);
                                                admap.put("toilet", sttoilet);
                                                admap.put("area", starea);

                                                if (wifiCheckBox.isChecked()) {
                                                    admap.put("wifi", "true");
                                                } else {
                                                    admap.put("wifi", "false");
                                                }
                                                if (parrkingCheckBox.isChecked()) {
                                                    admap.put("parking", "true");
                                                } else {
                                                    admap.put("parking", "false");
                                                }

                                               if( ! stscreenshot.equals(""))
                                                {admap.put("screenshot", stscreenshot);}

                                                if( ! stimage1.equals(""))
                                                {admap.put("image1", stimage1);}
                                                if( ! stimage2.equals(""))
                                                {admap.put("image2", stimage2);}
                                                if( ! stimage3.equals(""))
                                                {admap.put("image3", stimage3);}
                                                if( ! stimage4.equals(""))
                                                {admap.put("image4", stimage4);}
                                                if( ! stimage5.equals(""))
                                                {admap.put("image5", stimage5);}
                                                if( ! stimage6.equals(""))
                                                {admap.put("image6", stimage6);}

                                                uploadimages();
                                                mapMade=true;
                                                updateDataBase();


                                            } else {
                                                Toast.makeText(MyPublishedAdDetailsEdit.this, R.string.locationfirst, Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(MyPublishedAdDetailsEdit.this, R.string.descriptionfirst, Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(MyPublishedAdDetailsEdit.this, R.string.areafirst, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MyPublishedAdDetailsEdit.this, R.string.toiletfirst, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MyPublishedAdDetailsEdit.this, R.string.bedssfirst, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MyPublishedAdDetailsEdit.this, R.string.cityfirst, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MyPublishedAdDetailsEdit.this, R.string.roomsfirst, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MyPublishedAdDetailsEdit.this, R.string.pricefirst, Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void AddToTransactions()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, +0);
        String month = calendar.getTime().toString().substring(4, 7);
        String year = calendar.getTime().toString().substring(30, 34);
        String day = calendar.getTime().toString().substring(8, 10);
        String monthkey = month + "_" + year;
        DatabaseReference MonitorRef = FirebaseDatabase.getInstance().getReference().
                child("transactions").child("updated").child(monthkey).child(day).child(adkey);

        MonitorRef.updateChildren(admap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                MonitorRef.child("chanageddays").setValue(choosen_days.getText().toString());

            }
        });


    }

    private void sendEmail(String addedto)
    {
        DatabaseReference userEmail= FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("email");


        userEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    String ownerEmail =snapshot.getValue().toString();

                    String message = getResources().getString(R.string.days)
                            +"\n "+choosen_days.getText().toString()+"/n"
                            + addedto;
                    String subject = getResources().getString(R.string.daysstatuschanged);
                    JavaMailAPI javaMailAPI = new JavaMailAPI(MyPublishedAdDetailsEdit.this, ownerEmail,subject,message);
                    JavaMailAPI javaMailAPI1 = new JavaMailAPI(MyPublishedAdDetailsEdit.this, "masifkapp@gmail.com",subject,message);
                    javaMailAPI1.execute();
                    javaMailAPI.execute();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    private int getNomDays(String month,String year)
    {
        String MonNum = "null",day="1";
        if(month.equals("Jan")){MonNum="0";}
        if(month.equals("Fab")){MonNum="1";}
        if(month.equals("Mar")){MonNum="2";}
        if(month.equals("Apr")){MonNum="3";}
        if(month.equals("May")){MonNum="4";}
        if(month.equals("Jun")){MonNum="5";}
        if(month.equals("Jul")){MonNum="6";}
        if(month.equals("Aug")){MonNum="7";}
        if(month.equals("Sep")){MonNum="8";}
        if(month.equals("Oct")){MonNum="9";}
        if(month.equals("Nov")){MonNum="10";}
        if(month.equals("Dec")){MonNum="11";}


        Calendar monthStart = new GregorianCalendar(Integer.valueOf(year),Integer.valueOf(MonNum) , 1);
        days1= monthStart.getActualMaximum(Calendar.DAY_OF_MONTH);


        return  days1;

    }



    private void openmap()
    {
        String apiKey ="AIzaSyDbgYN-PQFjltxMiJmYwf1ywyusL8MpzMY";
        Places.initialize(getApplicationContext(), apiKey);

        maplayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        mtoolbar.setVisibility(View.GONE);
        mPlacesClient = Places.createClient(MyPublishedAdDetailsEdit.this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MyPublishedAdDetailsEdit.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MyPublishedAdDetailsEdit.this::onMapReady);
    }


    public void openGallary(View view) {
        Intent gallaryIntint = new Intent();
        gallaryIntint.setAction(Intent.ACTION_GET_CONTENT);
        gallaryIntint.setType("image/*");
        startActivityForResult(gallaryIntint, Gallary_pic);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallary_pic) {

            if (resultCode == RESULT_OK) {

                if (selectImage1) {
                    Image1Data = data.getData();
                    Picasso.get().load(Image1Data).into(image1);
                    selectImage1 = false;
                    loadedimage1 = true;


                }
                if (selectImage2) {
                    Image2Data = data.getData();
                    Picasso.get().load(Image2Data).into(image2);
                    selectImage2 = false;
                    loadedimage2 = true;

                }
                if (selectImage3) {
                    Image3Data = data.getData();
                    Picasso.get().load(Image3Data).into(image3);
                    selectImage3 = false;
                    loadedimage3 = true;

                }
                if (selectImage4) {
                    Image4Data = data.getData();
                    Picasso.get().load(Image4Data).into(image4);
                    selectImage4 = false;
                    loadedimage4 = true;
                }
                if (selectImage5) {
                    Image5Data = data.getData();
                    Picasso.get().load(Image5Data).into(image5);
                    selectImage5 = false;
                    loadedimage5 = true;

                }
                if (selectImage6) {
                    Image6Data = data.getData();
                    Picasso.get().load(Image6Data).into(image6);
                    selectImage6 = false;
                    loadedimage6 = true;
                }

            }

        } else {
            Toast.makeText(MyPublishedAdDetailsEdit.this, R.string.Selectimage, Toast.LENGTH_SHORT).show();
        }
    }

    private void    uploadimages() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat Date = new SimpleDateFormat("dd-MMMM-yyyy");
        SimpleDateFormat time = new SimpleDateFormat("hh-mm-ss");
        String saveCurrentDate = Date.format(calFordDate.getTime()) + time.format(calFordDate.getTime());


        userId = mAuth.getUid().toString();

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("ads").child(userId)
                .child(adkey).getParent();
        if (loadedimage1) {
            imagename = "image1";
            uploadimage(imagename, Image1Data);
        }
        if (loadedimage2) {
            imagename = "image2";
            uploadimage(imagename, Image2Data);
        }
        if (loadedimage3) {
            imagename = "image3";
            uploadimage(imagename, Image3Data);
        }
        if (loadedimage4) {
            imagename = "image4";
            uploadimage(imagename, Image4Data);
        }
        if (loadedimage5) {
            imagename = "image5";
            uploadimage(imagename, Image5Data);
        }
        if (loadedimage6) {
            imagename = "image6";
            uploadimage(imagename, Image6Data);
        }



    }

    private void uploadimage(String image, Uri data) {
        UserProfileImageRef.child(image).putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                UserProfileImageRef.child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).
                                child("published").child(adkey).child(image);
                        adsRef = FirebaseDatabase.getInstance().getReference().
                        child("cities").child(citykey).child("ads").child(adkey).child(image);

                        userRef.setValue(String.valueOf(uri));
                        adsRef.setValue(String.valueOf(uri));


                    }
                });
            }
        });
    }



    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }




    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Stlocation = locationETxt.getText().toString().replace("https://www.google.com/maps/search/?api=1&query=","");
        String [] stint=Stlocation.split(",");


        Float lng1 = Float.valueOf(stint[0]);
        Float lng2 = Float.valueOf(stint[1]);

        LatLng savedlocation = new LatLng(lng1, lng2);
        mMap.addMarker(new MarkerOptions().position(savedlocation).title("Saved location"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(savedlocation));


        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(lng1, lng2));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);


        // Enable the zoom controls for the map
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Prompt the user for permission.
        getLocationPermission();




        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude +":"+latLng.longitude);
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,mMap.getCameraPosition().zoom));
                mMap.addMarker(markerOptions);
                locationETxt.setText("https://www.google.com/maps/search/?api=1&query=" +
                        latLng.latitude + "," + latLng.longitude);



            }
        });

    }



    /**
     * Handles the result of the request for location permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }




    private void getCurrentPlaceLikelihoods() {
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG);

        // Get the likely places - that is, the businesses and other points of interest that
        // are the best match for the device's current location.
        @SuppressWarnings("MissingPermission") final FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();
        @SuppressLint("MissingPermission") Task<FindCurrentPlaceResponse> placeResponse = mPlacesClient.findCurrentPlace(request);
        placeResponse.addOnCompleteListener(this,
                new OnCompleteListener<FindCurrentPlaceResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                        if (task.isSuccessful()) {
                            FindCurrentPlaceResponse response = task.getResult();
                            // Set the count, handling cases where less than 5 entries are returned.
                            int count;
                            if (response.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                                count = response.getPlaceLikelihoods().size();
                            } else {
                                count = M_MAX_ENTRIES;
                            }

                            int i = 0;
                            mLikelyPlaceNames = new String[count];
                            mLikelyPlaceAddresses = new String[count];
                            mLikelyPlaceAttributions = new String[count];
                            mLikelyPlaceLatLngs = new LatLng[count];

                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                Place currPlace = placeLikelihood.getPlace();
                                mLikelyPlaceNames[i] = currPlace.getName();
                                mLikelyPlaceAddresses[i] = currPlace.getAddress();
                                mLikelyPlaceAttributions[i] = (currPlace.getAttributions() == null) ?
                                        null : TextUtils.join(" ", currPlace.getAttributions());
                                mLikelyPlaceLatLngs[i] = currPlace.getLatLng();

                                String currLatLng = (mLikelyPlaceLatLngs[i] == null) ?
                                        "" : mLikelyPlaceLatLngs[i].toString();

                                Log.i(TAG, String.format("Place " + currPlace.getName()
                                        + " has likelihood: " + placeLikelihood.getLikelihood()
                                        + " at " + currLatLng));

                                i++;
                                if (i > (count - 1)) {
                                    break;
                                }
                            }


                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                            }
                        }
                    }
                });
    }




    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            Log.d(TAG, "Latitude: " + mLastKnownLocation.getLatitude());
                            Log.d(TAG, "Longitude: " + mLastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        }

                        getCurrentPlaceLikelihoods();
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }








    public void captureScreen()
    {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback()
        {

            @Override
            public void onSnapshotReady(Bitmap snapshot)
            {

                screenshot.setImageBitmap(snapshot);


                screenshot.setVisibility(View.VISIBLE);
                loadedscreenshot=true;
                // Get the data from an ImageView as bytes
                screenshot.setDrawingCacheEnabled(true);
                screenshot.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) screenshot.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();






                UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("ads").child(userId)
                        .child(adkey);
                UploadTask uploadTask = UserProfileImageRef.child("screenshot").putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(MyPublishedAdDetailsEdit.this, "Failure", Toast.LENGTH_LONG).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        UserProfileImageRef.child("screenshot").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).
                                        child("published").child(adkey).child("screenshot");
                                adsRef = FirebaseDatabase.getInstance().getReference().
                                        child("cities").child(citykey).child("ads").child(adkey).child("screenshot");

                                userRef.setValue(String.valueOf(uri));
                                adsRef.setValue(String.valueOf(uri));


                            }
                        });
                    }
                });



            }
        };

        mMap.snapshot(callback);
    }


    private void pickCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            getDeviceLocation();
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }


    private void createCalenderPicker()
    {
        final Calendar CurrentMonth = Calendar.getInstance();
        CurrentMonth.add(Calendar.DAY_OF_MONTH, 1);
        final Calendar NextMonth = Calendar.getInstance();
        NextMonth.add(Calendar.MONTH, +1);

        calendarPicker.init(CurrentMonth.getTime(), NextMonth.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                //  .withDeactivateDates(list);  //day off every weak
                  .withSubTitles(getSubTitles())
                .withHighlightedDates(DeactevatedList);


    }

    private ArrayList<SubTitle> getSubTitles(){

        return subTitles;
    }

    private void readSchedualData(String month,String year,int days) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("cities").child(citykey)
                .child("ads").child(adkey);



        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                int Val=0,first=0;
                restoffirstMon=days-Val1;

                if(snapshot.child("calender").child(month+"_"+year).child("01").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("01").getValue().toString();
                    if(value.equals("confirmed"))
                    {
                          MakeRedHighLight("01",month,year);
                    }
                    if(value.equals("booked"))
                    {
                         tmrw.add(Calendar.DAY_OF_MONTH, Val1);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        restoffirstMon=0;
                        Val=1;
                        first=1;
                    }
                }

                if(snapshot.child("calender").child(month+"_"+year).child("02").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("02").getValue().toString();
                    if(value.equals("confirmed")){
                         MakeRedHighLight("02",month,year);}
                    if(value.equals("booked"))
                        {
                              tmrw.add(Calendar.DAY_OF_MONTH, 1+Val1-Val-first);
                              subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                             Val=2;
                             restoffirstMon=0;
                            first=1;
                        }
                }

                if(snapshot.child("calender").child(month+"_"+year).child("03").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("03").getValue().toString();
                    if(value.equals("confirmed")){
                         MakeRedHighLight("03",month,year);}
                    if(value.equals("booked")){
                         tmrw.add(Calendar.DAY_OF_MONTH, 2+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=3;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("04").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("04").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("04",month,year);}
                    if(value.equals("booked")){ tmrw.add(Calendar.DAY_OF_MONTH, 3+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=4;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("05").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("05").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("05",month,year);}
                    if(value.equals("booked")){ tmrw.add(Calendar.DAY_OF_MONTH, 4+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=5;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("06").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("06").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("06",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 5+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=6;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("07").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("07").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("07",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 6+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=7;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("08").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("08").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("08",month,year);}
                    if(value.equals("booked")){ tmrw.add(Calendar.DAY_OF_MONTH, 7+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=8;
                        restoffirstMon=0;
                        first=1;
                    }

                }
                if(snapshot.child("calender").child(month+"_"+year).child("09").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("09").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("09",month,year);}
                    if(value.equals("booked")){ tmrw.add(Calendar.DAY_OF_MONTH, 8+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=9;
                        restoffirstMon=0;
                        first=1;
                    }

                }

                if(snapshot.child("calender").child(month+"_"+year).child("10").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("10").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("10",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 9+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=10;
                        restoffirstMon=0;
                        first=1;
                    }
                }

                if(snapshot.child("calender").child(month+"_"+year).child("11").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("11").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("11",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 10+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+11;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("12").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("12").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("12",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH,11+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+12;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("13").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("13").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("13",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH,12+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+13;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("14").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("14").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("14",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 13 +Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+14;
                        restoffirstMon=0;
                        first=1;
                    }
                }

                if(snapshot.child("calender").child(month+"_"+year).child("15").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("15").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("15",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 14 +Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+15;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("16").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("16").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("16",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 15+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+16;
                        restoffirstMon=0;
                        first=1;
                    }
                }

                if(snapshot.child("calender").child(month+"_"+year).child("17").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("17").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("17",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 16+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+17;
                        restoffirstMon=0;
                        first=1;
                    }
                }

                if(snapshot.child("calender").child(month+"_"+year).child("18").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("18").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("18",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 17+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+18;
                        restoffirstMon=0;
                        first=1;
                    }
                }

                if(snapshot.child("calender").child(month+"_"+year).child("19").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("19").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("19",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 18+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+19;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("20").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("20").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("20",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 19+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+20;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("21").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("21").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("21",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 20+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+21;
                        restoffirstMon=0;
                        first=1;
                    }
                }

                if(snapshot.child("calender").child(month+"_"+year).child("22").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("22").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("22",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 21+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+22;
                        restoffirstMon=0;
                        first=1;
                    }
                }

                if(snapshot.child("calender").child(month+"_"+year).child("23").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("23").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("23",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 22+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+23;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("24").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("24").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("24",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 23+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+24;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("25").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("25").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("25",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 24+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+25;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("26").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("26").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("26",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 25+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+26;
                        restoffirstMon=0;
                        first=1;
                    }
                }

                if(snapshot.child("calender").child(month+"_"+year).child("27").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("27").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("27",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 26+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+27;
                        restoffirstMon=0;
                        first=1;
                    }
                }

                if(snapshot.child("calender").child(month+"_"+year).child("28").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("28").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("28",month,year);}
                    if(value.equals("booked")){ tmrw.add(Calendar.DAY_OF_MONTH, 27+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+28;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("29").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("29").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("29",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 28+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+29;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("30").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("30").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("30",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 29+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+30;
                        restoffirstMon=0;
                        first=1;
                    }
                }
                if(snapshot.child("calender").child(month+"_"+year).child("31").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("31").getValue().toString();
                    if(value.equals("confirmed")){ MakeRedHighLight("31",month,year);}
                    if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 30+Val1-Val-first);
                        subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                        Val=+31;
                        restoffirstMon=0;
                        first=1;
                    }
                }

                createCalenderPicker();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void MakeRedHighLight(String day,String month,String year)
    {
        String MonNum = "null";
        if(month.equals("Jan")){MonNum="1";}
        if(month.equals("Fab")){MonNum="2";}
        if(month.equals("Mar")){MonNum="3";}
        if(month.equals("Apr")){MonNum="4";}
        if(month.equals("May")){MonNum="5";}
        if(month.equals("Jun")){MonNum="6";}
        if(month.equals("Jul")){MonNum="7";}
        if(month.equals("Aug")){MonNum="8";}
        if(month.equals("Sep")){MonNum="9";}
        if(month.equals("Oct")){MonNum="10";}
        if(month.equals("Nov")){MonNum="11";}
        if(month.equals("Dec")){MonNum="12";}

        // String strdate2 = "26-9-2021";
        strdate = day+"-"+MonNum+"-"+year;
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        try
        {
            Date newdate = dateformat.parse(strdate);
            DeactevatedList.add(newdate);

        } catch (ParseException e) { e.printStackTrace(); }

    }

    private void readSchedualDataInCurrentMonth(String day, String month, String year)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                .child("published").child(adkey);



        int dayvalue = Integer.parseInt(day);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                if (dayvalue < 1) {

                     if (snapshot.child("calender").child(month + "_" + year).child("01").exists()) {
                        String value1 = snapshot.child("calender").child(month + "_" + year).child("01").getValue().toString();
                        if (value1.equals("confirmed")) { MakeRedHighLight("1", month, year); }
                        if(value1.equals("booked"))
                        {
                            tmrw.add(Calendar.DAY_OF_MONTH, 1-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                           Val1=1-dayvalue;

                        }
                    }
                }

                if(dayvalue <2) {

                     if (snapshot.child("calender").child(month + "_" + year).child("02").exists())
                     {
                        String value2 = snapshot.child("calender").child(month + "_" + year).child("02").getValue().toString();
                        if (value2.equals("confirmed")) { MakeRedHighLight("2", month, year); }
                        if(value2.equals("booked"))
                        {  tmrw.add(Calendar.DAY_OF_MONTH, 2-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=2-dayvalue;
                        }
                    }
                }
                if(dayvalue <3) {

                    if (snapshot.child("calender").child(month + "_" + year).child("03").exists()) {
                        String value3 = snapshot.child("calender").child(month + "_" + year).child("03").getValue().toString();
                        if (value3.equals("confirmed")) { MakeRedHighLight("3", month, year); }
                        if(value3.equals("booked"))
                        {  tmrw.add(Calendar.DAY_OF_MONTH, 3-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=3-dayvalue;
                        }
                    }
                }

                if(dayvalue<4) {

                    if (snapshot.child("calender").child(month + "_" + year).child("04").exists()) {
                        String value4 = snapshot.child("calender").child(month + "_" + year).child("04").getValue().toString();
                        if (value4.equals("confirmed")) { MakeRedHighLight("4", month, year); }
                        if(value4.equals("booked"))
                        {  tmrw.add(Calendar.DAY_OF_MONTH, 4-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=4-dayvalue;
                           }
                    }
                }

                if(dayvalue<5) {

                    if (snapshot.child("calender").child(month + "_" + year).child("05").exists()) {
                        String value5 = snapshot.child("calender").child(month + "_" + year).child("05").getValue().toString();
                        if (value5.equals("confirmed")) { MakeRedHighLight("5", month, year); }
                        if(value5.equals("booked"))
                        {
                            tmrw.add(Calendar.DAY_OF_MONTH, 5-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=5-dayvalue;
                        }
                    }
                }
                if(dayvalue<6) {

                    if (snapshot.child("calender").child(month + "_" + year).child("06").exists())
                    {
                        String value6 = snapshot.child("calender").child(month + "_" + year).child("06").getValue().toString();

                        if (value6.equals("confirmed")) { MakeRedHighLight("6", month, year); }
                        if(value6.equals("booked"))
                        {
                            tmrw.add(Calendar.DAY_OF_MONTH, 6-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=6-dayvalue;
                        }
                    }
                }

                if(dayvalue<7) {

                    if (snapshot.child("calender").child(month + "_" + year).child("07").exists())
                    {
                        String value7 = snapshot.child("calender").child(month + "_" + year).child("07").getValue().toString();

                        if (value7.equals("confirmed"))
                        { MakeRedHighLight("7", month, year); }

                        if(value7.equals("booked"))
                        {
                             tmrw.add(Calendar.DAY_OF_MONTH, 7-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=7-dayvalue;
                        }
                    }
                }

                if(dayvalue<8) {

                    if (snapshot.child("calender").child(month + "_" + year).child("08").exists()) {
                        String value8 = snapshot.child("calender").child(month + "_" + year).child("08").getValue().toString();
                        if (value8.equals("confirmed")) { MakeRedHighLight("8", month, year); }

                        if(value8.equals("booked"))
                        {
                            tmrw.add(Calendar.DAY_OF_MONTH, 8-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=8-dayvalue;
                        }
                    }
                }

                if(dayvalue<9) {
                    if (snapshot.child("calender").child(month + "_" + year).child("09").exists()) {
                        String value9 = snapshot.child("calender").child(month + "_" + year).child("09").getValue().toString();
                        if (value9.equals("confirmed")) { MakeRedHighLight("9", month, year); }
                        if(value9.equals("booked"))
                        {
                             tmrw.add(Calendar.DAY_OF_MONTH, 9-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=9-dayvalue;
                        }
                    }
                }


                if(dayvalue<10) {
                    if (snapshot.child("calender").child(month + "_" + year).child("10").exists())
                    {
                        String value10 = snapshot.child("calender").child(month + "_" + year).child("10").getValue().toString();
                        if (value10.equals("confirmed")) { MakeRedHighLight("10", month, year); }
                        if(value10.equals("booked"))
                        {
                            tmrw.add(Calendar.DAY_OF_MONTH, 10-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=10-dayvalue;
                        }
                    }
                }

                if(dayvalue<11) {
                    if (snapshot.child("calender").child(month + "_" + year).child("11").exists()) {
                        String value11 = snapshot.child("calender").child(month + "_" + year).child("11").getValue().toString();
                        if (value11.equals("confirmed")) { MakeRedHighLight("11", month, year); }
                        if(value11.equals("booked"))
                        {  tmrw.add(Calendar.DAY_OF_MONTH, 11-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=11-dayvalue;
                        }
                    }
                }

                if(dayvalue<12) {
                    if (snapshot.child("calender").child(month + "_" + year).child("12").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("12").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("12", month, year); }
                        if(value.equals("booked"))
                        {  tmrw.add(Calendar.DAY_OF_MONTH, 12-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=12-dayvalue;
                        }
                    }
                }

                if(dayvalue<13) {
                    if (snapshot.child("calender").child(month + "_" + year).child("13").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("13").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("13", month, year); }
                        if(value.equals("booked"))
                        {  tmrw.add(Calendar.DAY_OF_MONTH, 13-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=13-dayvalue;
                        }
                    }
                }

                if(dayvalue<14) {
                    if(snapshot.child("calender").child(month+"_"+year).child("14").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("14").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("14", month, year); }
                        if(value.equals("booked")){
                            tmrw.add(Calendar.DAY_OF_MONTH, 14-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=14-dayvalue;
                        }
                    }
                }


                if(dayvalue<15) {
                    if (snapshot.child("calender").child(month + "_" + year).child("15").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("15").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("15", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 15-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=15-dayvalue;
                        }
                    }
                }

                if(dayvalue<16) {
                    if(snapshot.child("calender").child(month+"_"+year).child("16").exists())
                    {
                        String value = snapshot.child("calender").child(month+"_"+year).child("16").getValue().toString();
                        if(value.equals("confirmed")){ MakeRedHighLight("16",month,year);}
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 16-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=16-dayvalue;
                        }
                    }}


                if(dayvalue<17) {
                    if(snapshot.child("calender").child(month+"_"+year).child("17").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("17").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("17", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 17-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=17-dayvalue;
                        }
                    }
                }

                if(dayvalue<18) {
                    if(snapshot.child("calender").child(month+"_"+year).child("18").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("18").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("18", month, year); }
                        if(value.equals("booked")){ tmrw.add(Calendar.DAY_OF_MONTH, 18-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=18-dayvalue;
                        }
                    }
                }

                if(dayvalue<19) {
                    if(snapshot.child("calender").child(month+"_"+year).child("19").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("19").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("19", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 19-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=19-dayvalue;
                        }
                    }
                }

                if(dayvalue<20) {
                    if(snapshot.child("calender").child(month+"_"+year).child("20").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("20").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("20", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 20-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=20-dayvalue;
                        }
                    }
                }

                if(dayvalue<21) {
                    if(snapshot.child("calender").child(month+"_"+year).child("21").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("21").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("21", month, year); }
                        if(value.equals("booked")){   tmrw.add(Calendar.DAY_OF_MONTH, 21-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=21-dayvalue;
                        }
                    }
                }

                if(dayvalue<22) {
                    if(snapshot.child("calender").child(month+"_"+year).child("22").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("22").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("22", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 22-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=22-dayvalue;
                        }
                    }
                }

                if(dayvalue<23) {
                    if(snapshot.child("calender").child(month+"_"+year).child("23").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("23").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("23", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 23-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=23-dayvalue;
                        }
                    }
                }

                if(dayvalue<24) {
                    if(snapshot.child("calender").child(month+"_"+year).child("24").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("24").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("24", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 24-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=24-dayvalue;
                        }
                    }
                }

                if(dayvalue<25) {
                    if(snapshot.child("calender").child(month+"_"+year).child("25").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("25").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("25", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 25-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=25-dayvalue;
                        }
                    }
                }

                if(dayvalue<26) {
                    if(snapshot.child("calender").child(month+"_"+year).child("26").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("26").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("26", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 26-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=26-dayvalue;
                        }
                    }
                }

                if(dayvalue<27) {
                    if(snapshot.child("calender").child(month+"_"+year).child("27").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("27").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("27", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 27-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=27-dayvalue;
                        }
                    }
                }

                if(dayvalue<28) {
                    if(snapshot.child("calender").child(month+"_"+year).child("28").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("28").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("28", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 28-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=28-dayvalue;
                        }
                    }
                }

                if(dayvalue<29) {
                    if(snapshot.child("calender").child(month+"_"+year).child("29").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("29").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("29", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 29-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=29-dayvalue;
                        }
                    }
                }

                if(dayvalue<30) {
                    if(snapshot.child("calender").child(month+"_"+year).child("30").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("30").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("30", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 30-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=30-dayvalue;
                        }
                    }
                }

                if(dayvalue<31) {
                    if(snapshot.child("calender").child(month+"_"+year).child("31").exists()) {
                        String value = snapshot.child("calender").child(month + "_" + year).child("31").getValue().toString();
                        if (value.equals("confirmed")) { MakeRedHighLight("31", month, year); }
                        if(value.equals("booked")){  tmrw.add(Calendar.DAY_OF_MONTH, 31-dayvalue-Val1);
                            subTitles.add(new SubTitle(tmrw.getTime(),getString(R.string.booked) ));
                            Val1=31-dayvalue;
                        }
                    }
                }


                createCalenderPicker();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void MakeDaysBusyOwnerPublishedAds(String ownerkey, String citykey, String roomkey, String stChosenDays)
    {
        DatabaseReference adOwnertRef = FirebaseDatabase.getInstance().getReference().
                child("users").child(ownerkey).child("published").child(roomkey).child("calender");

        DatabaseReference adRef = FirebaseDatabase.getInstance().getReference().
                child("cities").child(citykey).child("ads").child(roomkey).child("calender");

        int len = stChosenDays.length();
        int NoDayes;

        for( NoDayes = len/12;NoDayes>0;NoDayes--)
        {
            String month_day = stChosenDays.substring((11*NoDayes+1+(NoDayes-1))-12,11*NoDayes+(NoDayes-1));

            String month = month_day.substring(0,3);
            String day = month_day.substring(4,6);
            String year = month_day.substring(7,11);
            String monthkey = month+"_"+year;
            adOwnertRef.child(monthkey).child(day).setValue("booked");
            adRef.child(monthkey).child(day).setValue("booked");

        }
    }



    private void MakeDaysUnbookedOwnerPublishedAds(String ownerkey, String citykey, String roomkey, String stChosenDays)
    {
        DatabaseReference adOwnertRef = FirebaseDatabase.getInstance().getReference().
                child("users").child(ownerkey).child("published").child(roomkey).child("calender");
        DatabaseReference adRef = FirebaseDatabase.getInstance().getReference().
                child("cities").child(citykey).child("ads").child(roomkey).child("calender");

        int len = stChosenDays.length();
        int NoDayes;

        for( NoDayes = len/12;NoDayes>0;NoDayes--)
        {
            String month_day = stChosenDays.substring((11*NoDayes+1+(NoDayes-1))-12,11*NoDayes+(NoDayes-1));

            String month = month_day.substring(0,3);
            String day = month_day.substring(4,6);
            String year = month_day.substring(7,11);
            String monthkey = month+"_"+year;
            adOwnertRef.child(monthkey).child(day).setValue("free");
            adRef.child(monthkey).child(day).setValue("free");

        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}