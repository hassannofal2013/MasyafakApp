package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class RentedRoomDetailes extends AppCompatActivity {
    String adkey ,citykey,phone,ownerkey;
    private DatabaseReference mDatabase,OriginData;
    Boolean roomrated = false,ownerrated= false;

    TextView price,rooms,beds,description,adNumber,area,toilet,
            bookedDays,roomratingTextView,ownerratingTextView;
    ImageView wificheck,parkingcheck;

    FloatingActionButton call;
    private Toolbar mtoolbar;
    CardView cancelbooking,evaluation;
    FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private Timer timer;
    private int current_position=0;
    ViewPager viewPager;
    private LinearLayout dotsLayout,ratingLayout;
    RatingBar roomrating,ownerrating;

    private String[] imageUrls ;
    ImageView zoom;
    TextView ownerdetails,locationTxT;
    LinearLayout locationRequest, maplayout,copylayout;
    private PlacesClient mPlacesClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    GoogleMap mMap;
    private Location mLastKnownLocation;
    Boolean clicked= false;


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
    CardView ok;
    DatabaseReference   userRentedRef;
    String userName,userPhone,ownerName,ownerPhone,ownerEmail,userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rented_room_detailes);


        SharedPreferences preferences = getSharedPreferences("sharedFile",MODE_PRIVATE);
        adkey = preferences.getString("adkey","adkey");


        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        call = findViewById(R.id.fab);

        ratingLayout = findViewById(R.id.ratinglayout);
        roomrating = findViewById(R.id.Roomrating);
        ownerrating = findViewById(R.id.Ownerrating);

        ok = findViewById(R.id.okcardview);
        maplayout = findViewById(R.id.maplayout);
        scrollView  = findViewById(R.id.scrollView);
        locationRequest = findViewById(R.id.locationLayout);
        copylayout = findViewById(R.id.CopyLayout);
        ownerdetails = findViewById(R.id.ownerdetalies);
        locationTxT = findViewById(R.id.locationTxt);
        price = findViewById(R.id.pricetextView3);
        rooms = findViewById(R.id.roomstextView);
        beds = findViewById(R.id.bedstextView);
        description = findViewById(R.id.descriptiontextView);
        adNumber = findViewById(R.id.adnutextView);
        cancelbooking = findViewById(R.id.cancelebookingCard);
        evaluation= findViewById(R.id.ratingCard);
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.dotsContainer);
        area= findViewById(R.id.areaextView);
        toilet =findViewById(R.id.toiletextView12);
        parkingcheck =findViewById(R.id.parkcheckIcon);
        wificheck =findViewById(R.id.wificheckIcon);
        zoom =findViewById(R.id.zoom_outimageView17);
        bookedDays=findViewById(R.id.bookeddays);

        evaluation.setVisibility(View.GONE);
        mtoolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        copylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ClipboardManager cm = (ClipboardManager)RentedRoomDetailes.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copy", locationTxT.getText());
                cm.setPrimaryClip(clip);
                Toast.makeText(RentedRoomDetailes.this, R.string.Copiedtoclipboard, Toast.LENGTH_SHORT).show();

            }
        });

        locationRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openmap();
                hideSoftKeyboard(RentedRoomDetailes.this);
            }
        });



        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                maplayout.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                mtoolbar.setVisibility(View.VISIBLE);


            }
        });

        evaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFirebaseUser != null)
                {
                    String userId= mAuth.getUid().toString();
                  DatabaseReference adRef=  FirebaseDatabase.getInstance().getReference()
                            .child("cities").child(citykey).child("ads").child(adkey).child("rating");
                    final AlertDialog alertadd = new AlertDialog.Builder(RentedRoomDetailes.this).create();
                    final LayoutInflater factory = LayoutInflater.from(RentedRoomDetailes.this);
                    View dialogView = factory.inflate(R.layout.evaluation_layout, null);


                    RatingBar roomrating = dialogView.findViewById(R.id.Roomrating);
                    RatingBar ownerrating = dialogView.findViewById(R.id.Ownerrating);
                   TextView submit =dialogView.findViewById(R.id.textView17);
                   roomrating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                       @Override
                       public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                           roomrated = true;
                             }
                   });

                   ownerrating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                       @Override
                       public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                           ownerrated = true;
                       }
                   });


                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar calendar = Calendar.getInstance();

                            calendar.add(Calendar.MONTH, +0);
                            String month = calendar.getTime().toString().substring(4,7);
                            String  year = calendar.getTime().toString().substring(30,34);
                            String  day = calendar.getTime().toString().substring(8,10);
                            String month_year = month+"_"+year;

                        Float roomrate = roomrating.getRating();
                        String stroomrate = String.valueOf(roomrate).substring(0,1);
                        Float ownerate = ownerrating.getRating();
                            String stownerrate = String.valueOf(ownerate).substring(0,1);
                            userRentedRef= FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(userId).child("rented").child(adkey);
                            DatabaseReference evaluationMonitor = FirebaseDatabase.getInstance().getReference()
                                    .child("transactions").child("evaluation").child(month_year).child(day).child(adkey);
                            HashMap map2 = new HashMap();
                            map2.put("userkey",userId);
                            map2.put("ownerkey",ownerkey);
                            map2.put("citykey",citykey);
                            map2.put("adnumber",adkey);
                            map2.put("ownerrating",stownerrate);
                            map2.put("roomrating",stownerrate);
                            map2.put("time",calendar.getTime().toString());

                            DatabaseReference ownerRef =FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(ownerkey).child("ownerrating");
                        if( ownerrated && roomrated)
                        {
                            userRentedRef.child("rating").child("ownerrating").setValue(stownerrate);
                            userRentedRef.child("rating").child("roomrating").setValue(stroomrate);
                            evaluationMonitor.updateChildren(map2);

                            ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    if (snapshot.exists())
                                    {

                                        ownerRef.child("ratingusers").setValue(String.valueOf( Integer.parseInt(
                                                snapshot.child("ratingusers").getValue().toString() ) +1 ));


                                        if(snapshot.child("ownerrating").child(stownerrate).exists())
                                        {
                                            String oldNu =snapshot.child("ownerrating").child(stownerrate).getValue().toString();

                                            ownerRef.child("ownerrating").child(stownerrate).
                                                    setValue(String.valueOf(Integer.parseInt(oldNu) + 1));
                                        }
                                        else { ownerRef.child("ownerrating").child(stownerrate).setValue("1"); }
                                    }
                                    else
                                    {
                                        ownerRef.child("ratingusers").setValue("1");
                                        ownerRef.child("ownerrating").child(stownerrate).setValue("1");
                                    }
                                    alertadd.dismiss();

                                    Intent intent = new Intent(RentedRoomDetailes.this,RoomDetailes.class);
                                    startActivity(intent);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                            adRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    if(snapshot.exists())
                                    {

                                        adRef.child("ratingusers").
                                                setValue(String.valueOf( Integer.parseInt( snapshot.child("ratingusers").getValue().toString() ) +1 ));
                                        if(snapshot.child("roomrating").child(stroomrate).exists())
                                        {
                                            String oldNu=snapshot.child("roomrating").child(stroomrate).getValue().toString();
                                             adRef.child("roomrating").child(stroomrate).setValue(String.valueOf(Integer.parseInt(oldNu) + 1));
                                        }else { adRef.child("roomrating").child(stroomrate).setValue("1"); }

                                        if(snapshot.child("ownerrating").child(stownerrate).exists()) {
                                            String oldNu =snapshot.child("ownerrating").child(stownerrate).getValue().toString();

                                             adRef.child("ownerrating").child(stownerrate).
                                                    setValue(String.valueOf(Integer.parseInt(oldNu) + 1));
                                        }
                                        else { adRef.child("ownerrating").child(stownerrate).setValue("1"); }
                                        alertadd.dismiss();
                                    }
                                    else
                                    {
                                        adRef.child("ratingusers").setValue("1");
                                         adRef.child("roomrating").child(stroomrate).setValue("1");
                                        adRef.child("ownerrating").child(stownerrate).setValue("1");
                                        alertadd.dismiss();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                              }
                        else
                            {
                                Toast.makeText(RentedRoomDetailes.this, getString(R.string.mimimumRatis), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });



                    alertadd.setView(dialogView);
                    alertadd.show();

                }
                else
                {
                    Toast.makeText(RentedRoomDetailes.this, "Login first", Toast.LENGTH_SHORT).show();
                }
            }
        });


        cancelbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();

                calendar.add(Calendar.MONTH, +0);
                String month = calendar.getTime().toString().substring(4,7);
                String  year = calendar.getTime().toString().substring(30,34);
                String  day = calendar.getTime().toString().substring(8,10);
                String month_year = month+"_"+year;

                if(mFirebaseUser != null )
                {
                    String userId= mAuth.getUid().toString();

                      userRentedRef= FirebaseDatabase.getInstance().getReference()
                            .child("users").child(userId).child("rented").child(adkey);

                      DatabaseReference monitorRef= FirebaseDatabase.getInstance().getReference()
                              .child("transactions").child("canceled").child(month_year).child(day)
                              .child(adkey);
                     HashMap hashMap = new HashMap();
                     hashMap.put("adnumber",adkey);
                     hashMap.put("citykey",citykey);
                     hashMap.put("ownerkey",ownerkey);
                     hashMap.put("userkey",userId);
                    hashMap.put("time",calendar.getTime().toString());
                    monitorRef.updateChildren(hashMap);


                    userRentedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            String citykey,ownerkey;
                            if(snapshot.child("citykey").exists())
                            {
                                citykey = snapshot.child("citykey").getValue().toString();
                                if(snapshot.child("ownerkey").child("key").exists())
                                {
                                    ownerkey = snapshot.child("ownerkey").child("key").getValue().toString();
                                    if(snapshot.child("calender").exists())
                                    {

                                        final AlertDialog alertadd = new AlertDialog.Builder(RentedRoomDetailes.this).create();
                                        final LayoutInflater factory = LayoutInflater.from(RentedRoomDetailes.this);
                                        View dialogView = factory.inflate(R.layout.sample_dialog_send_email, null);

                                        EditText otp = dialogView.findViewById(R.id.otp);
                                        Button submit =dialogView.findViewById(R.id.submitBtn);

                                        submit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                clicked=true;
                                                String Stnotes = otp.getText().toString();

                                                if(! TextUtils.isEmpty(Stnotes))
                                                {
                                                     for(DataSnapshot month:snapshot.child("calender").child(userId).getChildren())
                                                    {
                                                        String Stmonth = month.getKey();

                                                        for(DataSnapshot days :snapshot.child("calender").child(userId).child(Stmonth).getChildren())
                                                        {
                                                            String Stdays = days.getKey();

                                                            DatabaseReference owneradRef = FirebaseDatabase.getInstance().getReference()
                                                                    .child("users").child(ownerkey).child("published").child(adkey).
                                                                            child("calender").child(Stmonth);

                                                            DatabaseReference AdRef = FirebaseDatabase.getInstance().getReference()
                                                                    .child("cities").child(citykey).child("ads").child(adkey)
                                                                    .child("calender").child(Stmonth);

                                                            HashMap hash = new HashMap();
                                                            hash.put(Stdays,"free");

                                                            AdRef.updateChildren(hash);
                                                            monitorRef.child("calender").child(Stmonth).updateChildren(hash);
                                                            owneradRef.updateChildren(hash);

                                                            Toast.makeText(RentedRoomDetailes.this, getString(R.string.bookingcanceled), Toast.LENGTH_SHORT).show();

                                                            sendEmail(Stnotes,userId,ownerkey,adkey);

                                                        }

                                                    }

                                                }
                                                else {
                                                    Toast.makeText(RentedRoomDetailes.this, getString(R.string.writecause), Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });



                                        alertadd.setView(dialogView);
                                        alertadd.show();


                                    }
                                }
                            }


                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




                }
                else
                {
                    Toast.makeText(RentedRoomDetailes.this, "Login first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(mFirebaseUser != null)
                {
                    final AlertDialog alertadd = new AlertDialog.Builder(RentedRoomDetailes.this).create();
                    final LayoutInflater factory = LayoutInflater.from(RentedRoomDetailes.this);
                    View dialogView = factory.inflate(R.layout.sample_dialog_call, null);

                    CardView Call = dialogView.findViewById(R.id.callcard);

                    CardView Whats =dialogView.findViewById(R.id.whatsCard);

                    Call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {  String prefex="tel:";
                            if(phone.startsWith("05")) { prefex ="tel:+966";  phone=phone.substring(1); }
                            else if(phone.startsWith("5")) { prefex ="tel:+966"; }
                            if(phone.startsWith("01")) { prefex ="tel:+20";  phone=phone.substring(1); }
                            else if(phone.startsWith("1")) { prefex ="tel:+20";  }
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse(prefex+phone));
                            startActivity(intent);
                            alertadd.dismiss();

                        }
                    });

                    Whats.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            String prefex="+";
                            if(phone.startsWith("05")) { prefex ="+966";  phone=phone.substring(1); }
                            else if(phone.startsWith("5")) { prefex ="+966"; }
                            if(phone.startsWith("01")) { prefex ="+20";  phone=phone.substring(1); }
                            else if(phone.startsWith("1")) { prefex ="+20";  }
                            String url = "https://api.whatsapp.com/send?phone="+prefex+phone;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                            alertadd.dismiss();
                        }
                    });




                    alertadd.setView(dialogView);
                    alertadd.show();





                }
                else
                {
                    final AlertDialog alertadd = new AlertDialog.Builder(RentedRoomDetailes.this).create();
                    final LayoutInflater factory = LayoutInflater.from(RentedRoomDetailes.this);
                    View dialogView = factory.inflate(R.layout.sample, null);
                    TextView alerttextView = dialogView.findViewById(R.id.dialog_textView);
                    alerttextView.setText(getString(R.string.noaccount));
                    Button cancel =dialogView.findViewById(R.id.cancelBtn);
                    Button register =dialogView.findViewById(R.id.regestrationBtn);
                    register.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(RentedRoomDetailes.this,LoginActivity.class);
                            startActivity(intent);
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertadd.dismiss();
                        }
                    });




                    alertadd.setView(dialogView);
                    alertadd.show();   }




            }
        });





        if(adkey != null)
        {
            String userid = mAuth.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(userid).child("rented").child(adkey);


            mDatabase.keepSynced(true);

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String imageScreenshotSt = "", image1St = "", image2St = "", image3St = "", image4St = "", image5St = "", image6St = "";

                        if (snapshot.child("rating").exists())
                        {
                            ratingLayout.setVisibility(View.VISIBLE);
                            evaluation.setVisibility(View.GONE);
                            citykey=snapshot.child("citykey").getValue().toString();

                            //  Toast.makeText(RentedRoomDetailes.this, citykey, Toast.LENGTH_SHORT).show();

                            OriginData= FirebaseDatabase.getInstance().getReference().child("cities")
                                    .child(citykey).child("ads").child(adkey);

                            OriginData.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {

                                   /*
                                    if(snapshot.child("rating").child("roomrating").exists())

                                    {
                                        String rate = snapshot.child("rating").child("roomrating").getValue().toString();
                                        Float ratefloat = Float.valueOf(rate);
                                        roomrating.setRating(ratefloat);
                                        roomrating.setEnabled(false);
                                    }
                                    if(snapshot.child("rating").child("ownerrating").exists())
                                    {
                                        String rate = snapshot.child("rating").child("ownerrating").getValue().toString();
                                        Float ratefloat = Float.valueOf(rate);
                                        ownerrating.setRating(ratefloat);
                                        ownerrating.setEnabled(false);
                                    }

                                    */
                                    Float intNorate1=Float.valueOf(0),intNorate2=Float.valueOf(0),intNorate3=Float.valueOf(0),intNorate4=Float.valueOf(0),intNorate5=Float.valueOf(0),intNoUsers=Float.valueOf(0);
                                    Float intTotal;
                                    String Norate1,Norate2,Norate3,Norate4,Norate5;

                                    if(snapshot.child("rating").child("roomrating").child("1").exists())
                                    {
                                        Norate1 = snapshot.child("rating").child("roomrating").child("1").getValue().toString();
                                        intNorate1= Float.valueOf(Norate1)*1;

                                    }
                                    if(snapshot.child("rating").child("roomrating").child("2").exists())
                                    {
                                        Norate2 = snapshot.child("rating").child("roomrating").child("2").getValue().toString();
                                        intNorate2= Float.valueOf(Norate2)*2;
                                    }
                                    if(snapshot.child("rating").child("roomrating").child("3").exists())
                                    {
                                        Norate3 = snapshot.child("rating").child("roomrating").child("3").getValue().toString();
                                        intNorate3= Float.valueOf(Norate3)*3;
                                    }
                                    if(snapshot.child("rating").child("roomrating").child("4").exists())
                                    {
                                        Norate4 = snapshot.child("rating").child("roomrating").child("4").getValue().toString();
                                        intNorate4 = Float.valueOf(Norate4)*4;
                                    }
                                    if(snapshot.child("rating").child("roomrating").child("5").exists())
                                    {
                                        Norate5 = snapshot.child("rating").child("roomrating").child("5").getValue().toString();
                                        intNorate5 = Float.valueOf(Norate5)*5;
                                    }
                                    if(snapshot.child("rating").child("ratingusers").exists())
                                    {
                                        String stratingusers = snapshot.child("rating").child("ratingusers").getValue().toString();
                                        intNoUsers=Float.valueOf((stratingusers));
                                    }
                                   // roomratingTextView = findViewById(R.id.roomevaluation);
                                    intTotal = (intNorate1+intNorate2+intNorate3+intNorate4+intNorate5)/intNoUsers;
                                   // roomratingTextView.setText(String.valueOf(intTotal).substring(0,3)+"  "+getString(R.string.roomevaluation));
                                    roomrating.setRating(intTotal);
                                    roomrating.setEnabled(false);



                                    Float intNorate1owner = Float.valueOf(0),intNorate2owner= Float.valueOf(0),intNorate3owner= Float.valueOf(0),intNorate4owner= Float.valueOf(0)
                                            ,intNorate5owner= Float.valueOf(0),intNoUsersowner= Float.valueOf(0),intTotalowner= Float.valueOf(0);
                                    String Norate1owner,Norate2owner,Norate3owner,Norate4owner,Norate5owner;

                                    if(snapshot.child("rating").child("ownerrating").child("1").exists())
                                    {
                                        Norate1owner = snapshot.child("rating").child("ownerrating").child("1").getValue().toString();
                                        intNorate1owner= Float.valueOf(Norate1owner)*1;

                                    }
                                    if(snapshot.child("rating").child("ownerrating").child("2").exists())
                                    {
                                        Norate2owner = snapshot.child("rating").child("ownerrating").child("2").getValue().toString();
                                        intNorate2owner= Float.valueOf(Norate2owner)*2;
                                    }
                                    if(snapshot.child("rating").child("ownerrating").child("3").exists())
                                    {
                                        Norate3owner = snapshot.child("rating").child("ownerrating").child("3").getValue().toString();
                                        intNorate3owner= Float.valueOf(Norate3owner)*3;
                                    }
                                    if(snapshot.child("rating").child("ownerrating").child("4").exists())
                                    {
                                        Norate4owner = snapshot.child("rating").child("ownerrating").child("4").getValue().toString();
                                        intNorate4owner = Float.valueOf(Norate4owner)*4;
                                    }
                                    if(snapshot.child("rating").child("ownerrating").child("5").exists())
                                    {
                                        Norate5owner = snapshot.child("rating").child("ownerrating").child("5").getValue().toString();
                                        intNorate5owner = Float.valueOf(Norate5owner)*5;
                                    }
                                    if(snapshot.child("rating").child("ratingusers").exists())
                                    {
                                        String stratingusers = snapshot.child("rating").child("ratingusers").getValue().toString();
                                        intNoUsersowner=Float.valueOf(stratingusers);
                                    }
                                    intTotalowner = (intNorate1owner+intNorate2owner+intNorate3owner+intNorate4owner+intNorate5owner)/intNoUsersowner;
                                  //  ownerratingTextView = findViewById(R.id.ownerevaluationTextView);
                                    //ownerratingTextView.setText(String.valueOf(intTotalowner).substring(0,3)+"  "+getString(R.string.Ownervaluation));
                                    ownerrating.setRating(intTotalowner);
                                    ownerrating.setEnabled(false);




                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });










                        }
                        else { ratingLayout.setVisibility(View.GONE);}

                        if (snapshot.child("screenshot").exists())
                        {
                            imageScreenshotSt = snapshot.child("screenshot").getValue().toString();
                        }

                        if (snapshot.child("location").exists()) {
                            String s = snapshot.child("location").getValue().toString();
                            locationTxT.setText(s);
                        }

                        if (snapshot.child("ownerkey").exists()) {
                            ownerkey  = snapshot.child("ownerkey").child("key").getValue().toString();
                            String name = snapshot.child("ownerkey").child("name").getValue().toString();
                             phone = snapshot.child("ownerkey").child("phone").getValue().toString();
                            String email = snapshot.child("ownerkey").child("email").getValue().toString();
                            ownerdetails.setText(name + "\n" + phone + "\n" + email);
                        }

                        if (snapshot.child("image1").exists()) {
                            image1St = snapshot.child("image1").getValue().toString();
                        }

                        if (snapshot.child("image2").exists()) {
                            image2St = snapshot.child("image2").getValue().toString();
                        }

                        if (snapshot.child("image3").exists()) {
                            image3St = snapshot.child("image3").getValue().toString();
                        }

                        if (snapshot.child("image4").exists()) {
                            image4St = snapshot.child("image4").getValue().toString();
                        }

                        if (snapshot.child("image5").exists()) {
                            image5St = snapshot.child("image5").getValue().toString();
                        }

                        if (snapshot.child("image6").exists()) {
                            image6St = snapshot.child("image6").getValue().toString();
                        }

                        if (!image6St.equals("")) {
                            imageUrls = new String[]{imageScreenshotSt, image1St, image2St, image3St, image4St, image5St, image6St};
                            PagerAdapter adapter = new ViewPagerAdapter(RentedRoomDetailes.this, imageUrls);
                            viewPager.setAdapter(adapter);
                            prepareDotes(current_position++);
                            creatSlideShow();
                        } else {
                            if (!image5St.equals("")) {
                                imageUrls = new String[]{imageScreenshotSt, image1St, image2St, image3St, image4St, image5St};
                                PagerAdapter adapter = new ViewPagerAdapter(RentedRoomDetailes.this, imageUrls);
                                viewPager.setAdapter(adapter);
                                prepareDotes(current_position++);
                                creatSlideShow();
                            } else {
                                if (!image4St.equals("")) {
                                    imageUrls = new String[]{imageScreenshotSt, image1St, image2St, image3St, image4St};
                                    PagerAdapter adapter = new ViewPagerAdapter(RentedRoomDetailes.this, imageUrls);
                                    viewPager.setAdapter(adapter);
                                    prepareDotes(current_position++);
                                    creatSlideShow();
                                } else {
                                    if (!image3St.equals("")) {
                                        imageUrls = new String[]{imageScreenshotSt, image1St, image2St, image3St};
                                        PagerAdapter adapter = new ViewPagerAdapter(RentedRoomDetailes.this, imageUrls);
                                        viewPager.setAdapter(adapter);
                                        prepareDotes(current_position++);
                                        creatSlideShow();
                                    } else {
                                        if (!image2St.equals("")) {
                                            imageUrls = new String[]{imageScreenshotSt, image1St, image2St};
                                            PagerAdapter adapter = new ViewPagerAdapter(RentedRoomDetailes.this, imageUrls);
                                            viewPager.setAdapter(adapter);
                                            prepareDotes(current_position++);
                                            creatSlideShow();
                                        } else {
                                            if (!image1St.equals("")) {
                                                imageUrls = new String[]{imageScreenshotSt, image1St};
                                                PagerAdapter adapter = new ViewPagerAdapter(RentedRoomDetailes.this, imageUrls);
                                                viewPager.setAdapter(adapter);
                                                prepareDotes(current_position++);
                                                creatSlideShow();
                                            } else {
                                                imageUrls = new String[]{imageScreenshotSt, image1St};
                                                PagerAdapter adapter = new ViewPagerAdapter(RentedRoomDetailes.this, imageUrls);
                                                viewPager.setAdapter(adapter);
                                                prepareDotes(current_position++);
                                                creatSlideShow();
                                            }

                                        }

                                    }
                                }
                            }
                        }

                        if (snapshot.child("price").exists()) {
                            String St = snapshot.child("price").getValue().toString();
                            price.setText(St + " " + getString(R.string.pound));
                        }

                        if (snapshot.child("rooms").exists()) {
                            String St = snapshot.child("rooms").getValue().toString();
                            rooms.setText(St + " " + getString(R.string.rooms));
                        }

                        if (snapshot.child("citykey").exists()) {
                            citykey = snapshot.child("citykey").getValue().toString();

                        }


                        if (snapshot.child("calender").child(userid).exists()) {

                            for (DataSnapshot dataSnapshot : snapshot.child("calender").child(userid).getChildren()) {
                                String monthName = dataSnapshot.getKey();

                                for (DataSnapshot data : snapshot.child("calender").child(userid).child(monthName).getChildren())
                                {
                                    String dayValue = data.getKey();
                                    String monthValue = monthName.substring(0, 3);
                                    String yearVlue = monthName.substring(4);

                                    String yearAndMonth = monthName.replace("_", " ");

                                    String oldTXT = bookedDays.getText().toString();



                                    if (TextUtils.isEmpty(oldTXT))
                                    {
                                        bookedDays.setText(dayValue + " " + yearAndMonth + "\n");
                                        setCancelVisibility(dayValue, monthValue, yearVlue);
                                    } else
                                        {
                                            if(! oldTXT.contains(dayValue + " " + yearAndMonth + "\n"))
                                        bookedDays.setText(oldTXT + dayValue + " " + yearAndMonth + "\n");
                                        }
                                }


                            }
                        }


                        if (snapshot.child("beds").exists()) {
                            String St = snapshot.child("beds").getValue().toString();
                            beds.setText(St + " " + getString(R.string.beds));
                        }
                        if (snapshot.child("description").exists()) {
                            String St = snapshot.child("description").getValue().toString();
                            description.setText(St);
                        }


                        if (snapshot.child("userkey").exists()) {
                            String St = snapshot.child("userkey").getValue().toString();
                            SharedPreferences.Editor editor = getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                            editor.putString("ownerkey", St);
                            editor.apply();
                        }


                        if (snapshot.child("adnumber").exists()) {
                            String St = snapshot.child("adnumber").getValue().toString();

                            mtoolbar.setTitle(getString(R.string.adnuis) + St);
                            adNumber.setText(getString(R.string.adnuis) + St);

                        }

                        if (snapshot.child("area").exists()) {
                            String St = snapshot.child("area").getValue().toString();
                            area.setText(St + " " + getString(R.string.miters));
                        }
                        if (snapshot.child("toilet").exists()) {
                            String St = snapshot.child("toilet").getValue().toString();
                            toilet.setText(St + " " + getString(R.string.toilet));
                        }
                        if (snapshot.child("wifi").exists()) {
                            String St = snapshot.child("wifi").getValue().toString();
                            if (St.equals("false")) {
                                wificheck.setImageResource(R.drawable.rong);
                            }
                        }

                        if (snapshot.child("parking").exists()) {
                            String St = snapshot.child("parking").getValue().toString();
                            if (St.equals("false")) {
                                parkingcheck.setImageResource(R.drawable.rong);
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }




        zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                editor.putString("citykey",citykey);
                editor.apply();

                Intent intent = new Intent(RentedRoomDetailes.this,Full_Screen_ImageSliderActivity.class);
                startActivity(intent);


            }
        });



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {
                if(current_position==imageUrls.length)
                {current_position=0;
                    prepareDotes(current_position++);}
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });






    }

    private void setCancelVisibility(String bookeddayValue, String bookedmonth, String bookedyear)
    {

        Calendar calendar = Calendar.getInstance();
        String month1 = calendar.getTime().toString().substring(4, 7);
        String year1 = calendar.getTime().toString().substring(30, 34);
        String day1 = calendar.getTime().toString().substring(8, 10);

        String MonNum = "null",MonNum1="null";

        if(month1.equals("Jan")){MonNum1="1";}
        if(month1.equals("Fab")){MonNum1="2";}
        if(month1.equals("Mar")){MonNum1="3";}
        if(month1.equals("Apr")){MonNum1="4";}
        if(month1.equals("May")){MonNum1="5";}
        if(month1.equals("Jun")){MonNum1="6";}
        if(month1.equals("Jul")){MonNum1="7";}
        if(month1.equals("Aug")){MonNum1="8";}
        if(month1.equals("Sep")){MonNum1="9";}
        if(month1.equals("Oct")){MonNum1="10";}
        if(month1.equals("Nov")){MonNum1="11";}
        if(month1.equals("Dec")){MonNum1="12";}

        if(bookedmonth.equals("Jan")){MonNum="1";}
        if(bookedmonth.equals("Fab")){MonNum="2";}
        if(bookedmonth.equals("Mar")){MonNum="3";}
        if(bookedmonth.equals("Apr")){MonNum="4";}
        if(bookedmonth.equals("May")){MonNum="5";}
        if(bookedmonth.equals("Jun")){MonNum="6";}
        if(bookedmonth.equals("Jul")){MonNum="7";}
        if(bookedmonth.equals("Aug")){MonNum="8";}
        if(bookedmonth.equals("Sep")){MonNum="9";}
        if(bookedmonth.equals("Oct")){MonNum="10";}
        if(bookedmonth.equals("Nov")){MonNum="11";}
        if(bookedmonth.equals("Dec")){MonNum="12";}

       if(Integer.parseInt(bookedyear)
               <Integer.parseInt(year1))
       {
           cancelbooking.setVisibility(View.GONE);
           evaluation.setVisibility(View.VISIBLE);
           }
       else if( Integer.parseInt(bookedyear)
               == Integer.parseInt(year1) )
         {
             if(Integer.parseInt(MonNum)<Integer.parseInt(MonNum1))
             {
                 cancelbooking.setVisibility(View.GONE);
                 evaluation.setVisibility(View.VISIBLE);
                 }
             else if(Integer.parseInt(MonNum)==Integer.parseInt(MonNum1))
             {
                 if(Integer.parseInt(bookeddayValue)<Integer.parseInt(day1))
                 {
                     cancelbooking.setVisibility(View.GONE);
                     evaluation.setVisibility(View.VISIBLE);
                        }
             }


         }

    }


    private void sendEmail(String notes,String userId, String ownerkey, String roomkey)
    {



        DatabaseReference AllRef = FirebaseDatabase.getInstance().getReference().
                child("users");
        AllRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                if(snapshot.child(userId).exists())
                  {
                    if(snapshot.child(userId).child("name").exists())
                    {userName = snapshot.child(userId).child("name").getValue().toString();}
                    if(snapshot.child(userId).child("phone").exists())
                    {userPhone= snapshot.child(userId).child("phone").getValue().toString();}
                    if(snapshot.child(userId).child("email").exists())
                    {userEmail= snapshot.child(userId).child("email").getValue().toString();}
                     if(snapshot.child(ownerkey).exists())
                    {
                        if(snapshot.child(ownerkey).child("name").exists())
                        {ownerName = snapshot.child(ownerkey).child("name").getValue().toString();}
                        if(snapshot.child(ownerkey).child("phone").exists())
                        {ownerPhone= snapshot.child(ownerkey).child("phone").getValue().toString();}
                        if(snapshot.child(ownerkey).child("email").exists())
                        {ownerEmail = snapshot.child(ownerkey).child("email").getValue().toString();}

                    }

                     else {
                         Toast.makeText(RentedRoomDetailes.this, "the owner is not registered", Toast.LENGTH_SHORT).show();
                     }
                }
                else {
                    Toast.makeText(RentedRoomDetailes.this, "you are not registered", Toast.LENGTH_SHORT).show();
                     }


                String StbookedDays = bookedDays.getText().toString();

                String subject1 ="Booking canceled "+"\n"+"تم إلغاء الحجز";
                String message1 = "Reservation of the following days"+"\n"+
                        StbookedDays+"\n"+
                        "has been cancelled by" +"\n"+
                        userName+"\n"+
                        userPhone +"\n"+
                        "for the following reasons" +"\n"+
                        notes+"\n"+
                        "Ad number is "+"  " +roomkey+"\n"+
                        "to contact with owner"+"\n"+
                        ownerName+"\n"+
                        ownerPhone
                        +"\n" +"\n"+"\n" +"\n"+

                        "تم إلغاء حجز الأيام الأتيه " +"\n"+
                        StbookedDays+"\n"+
                        "بواسطة"+"\n"+
                        userName+"\n"+
                        userPhone +"\n"+
                        "وذلك للاسباب الاتية" +"\n"+
                        notes+"\n"+
                        "رقم الاعلان هو"+"  " +roomkey+"\n"+
                        "للتواصل مع المالك"+"\n"+
                        ownerName+"\n"+
                        ownerPhone

                        ;


                if(clicked) {
                    Toast.makeText(RentedRoomDetailes.this, " clicked=true", Toast.LENGTH_SHORT).show();
                    JavaMailAPI javaMailAPI = new JavaMailAPI(RentedRoomDetailes.this, ownerEmail, subject1, message1);
                    javaMailAPI.execute();
                    JavaMailAPI javaMailAPI1 = new JavaMailAPI(RentedRoomDetailes.this, userEmail, subject1, message1);
                    javaMailAPI1.execute();
                    JavaMailAPI javaMailAPI2 = new JavaMailAPI(RentedRoomDetailes.this, "masifkapp@gmail.com", subject1, message1);
                    javaMailAPI2.execute();
                    clicked=false;
                    Intent intent = new Intent(RentedRoomDetailes.this,MainActivity.class);
                    startActivity(intent);
                }


                userRentedRef.removeValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

       onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void creatSlideShow()
    {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run()
            {
                viewPager.setCurrentItem(current_position,true);
                current_position++;
                if(current_position==imageUrls.length){current_position=0;}
                prepareDotes(current_position);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        },300,2500);
    }

    private void prepareDotes(int currentSlidePosition)
    {
        if(dotsLayout.getChildCount()>0)
            dotsLayout.removeAllViews();
        ImageView dots[] = new ImageView[imageUrls.length];

        for (int i = 0 ; i<imageUrls.length ; i++)
        {

            dots[i] = new ImageView(this);
            if(i==currentSlidePosition)
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.active_dot));
            else
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.inactive_dot));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(4,0,4,0);
            dotsLayout.addView(dots[i],layoutParams);

        }
    }

    private void openmap()
    {
        String apiKey ="AIzaSyDbgYN-PQFjltxMiJmYwf1ywyusL8MpzMY";
        Places.initialize(getApplicationContext(), apiKey);

        maplayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        mtoolbar.setVisibility(View.GONE);
        mPlacesClient = Places.createClient(RentedRoomDetailes.this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RentedRoomDetailes.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(RentedRoomDetailes.this::onMapReady);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       String Stlocation = locationTxT.getText().toString().replace("https://www.google.com/maps/search/?api=1&query=","");
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

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }










}