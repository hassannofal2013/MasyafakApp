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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MyAdRequestsDetailsEdit extends AppCompatActivity {
    String adkey ,userId,imagename,StOldCity;
    private DatabaseReference mDatabase,publishedRef,requestedRef;
    ImageView image1,image2,image3,image4,image5,image6,screenshot;
    LinearLayout linearLayout1,linearLayout2,linearLayout3;
    EditText price,rooms,description,city,locationETxt,area,beds,toilet;
    TextView adNumber,requestTime,status;
    FloatingActionButton call;
    private Toolbar mtoolbar;


    private CardView delete,save,locationRequest;
    FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    final static int Gallary_pic = 1;
    boolean selectImage1, selectImage2, selectImage3, selectImage4, selectImage5, selectImage6,
            loadedimage1, loadedimage2, loadedimage3, loadedimage4, loadedimage5, loadedimage6,
            saveboolean=false;
    private Uri ImageData, Image1Data, Image2Data, Image3Data, Image4Data, Image5Data, Image6Data;

    StorageReference UserProfileImageRef;
    DatabaseReference userRef;
    DatabaseReference adsRequestRef;
    FusedLocationProviderClient fusedLocationProviderClient;

    private CheckBox parrkingCheckBox,wifiCheckBox;
    private PlacesClient mPlacesClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;



    CardView ok,cancel;
    GoogleMap mMap;

    Boolean screenshotteken=false;
    LinearLayout maplayout;


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
    String Stlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ad_request_details_edit);

        SharedPreferences preferences = getSharedPreferences("sharedFile",MODE_PRIVATE);
        adkey = preferences.getString("adkey","adkey");


        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        userId=mAuth.getUid().toString();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MyAdRequestsDetailsEdit.this);

        call = findViewById(R.id.fab);
        price = findViewById(R.id.pricetextView3);
        rooms = findViewById(R.id.roomstextView);
        description = findViewById(R.id.descriptiontextView);
        adNumber = findViewById(R.id.adnutextView);
        city = findViewById(R.id.cityTextView);
        city.setEnabled(false);
        requestTime  = findViewById(R.id.requestingtimetextView45);
        locationETxt = findViewById(R.id.locationtextView46);
        currentlocationImage  = findViewById(R.id.currentlocationimageView);

        maplayout = findViewById(R.id.maplayout);
        cancel = findViewById(R.id.cancelcardview);
        ok = findViewById(R.id.okcardview);

        area= findViewById(R.id.areatextView3);
        area.setEnabled(false);
        beds= findViewById(R.id.bedtextView);
        toilet= findViewById(R.id.toilettextView3);

        status = findViewById(R.id.statustextView22);
        save = findViewById(R.id.savecard);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                hideSoftKeyboard(MyAdRequestsDetailsEdit.this);
            }
        });
        locationRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             openmap();
            hideSoftKeyboard(MyAdRequestsDetailsEdit.this);

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new androidx.appcompat.app.AlertDialog.Builder(MyAdRequestsDetailsEdit.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.removingad))
                        .setMessage(getString(R.string.sureRemove))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("ads").child(userId)
                                        .child(adkey);
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
                                                child(userId).child("adsrequests").child(adkey);

                                        requestedRef = FirebaseDatabase.getInstance().getReference().child("adsrequests").
                                                child(adkey);
                                        publishedRef= FirebaseDatabase.getInstance().getReference().child("cities").child(StOldCity).
                                                child("ads").child(adkey);

                                        publishedRef.removeValue();
                                        mDatabase.removeValue();
                                        requestedRef.removeValue();

                                        Intent intent = new Intent(MyAdRequestsDetailsEdit.this,MainActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(MyAdRequestsDetailsEdit.this, R.string.deletead, Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();



            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveboolean=true;
                if(saveboolean)
                {
                    saveboolean=false;
                Calendar calFordDate = Calendar.getInstance();
                SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
                String saveCurrentDate = Date.format(calFordDate.getTime()) + time.format(calFordDate.getTime());


                String stprice, strooms, stdescription, stcity, stlocation, stbeds, sttoilet, starea;
                stprice = price.getText().toString();
                strooms = rooms.getText().toString();
                stcity = city.getText().toString();
                stdescription = description.getText().toString();
                stlocation = locationETxt.getText().toString();
                stbeds = beds.getText().toString();
                sttoilet = toilet.getText().toString();
                starea = area.getText().toString();

                if (!TextUtils.isEmpty(stprice)) {
                    if (!TextUtils.isEmpty(strooms)) {
                        if (!TextUtils.isEmpty(stcity)) {
                            if (!TextUtils.isEmpty(stbeds)) {
                                if (!TextUtils.isEmpty(sttoilet)) {
                                    if (!TextUtils.isEmpty(starea)) {
                                        if (!TextUtils.isEmpty(stdescription)) {
                                            if (!TextUtils.isEmpty(stlocation)) {

                                                uploadimages();
                                                HashMap admap = new HashMap();
                                                admap.put("description", stdescription);
                                                admap.put("location", stlocation);
                                                admap.put("price", stprice);
                                                admap.put("rooms", strooms);
                                                admap.put("time", saveCurrentDate);
                                                admap.put("adnumber", adkey);
                                                admap.put("city", stcity);
                                                admap.put("status", "جاري العرض على المشرف");
                                                admap.put("userkey", userId);
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

                                                mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId).
                                                        child("adsrequests").child(adkey);
                                                adsRequestRef = FirebaseDatabase.getInstance().getReference().child("adsrequests").child(adkey);
                                                adsRequestRef.updateChildren(admap);
                                                mDatabase.updateChildren(admap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        publishedRef = FirebaseDatabase.getInstance().getReference().child("cities").child(StOldCity).
                                                                child("ads").child(adkey);
                                                        publishedRef.removeValue();

                                                        Toast.makeText(MyAdRequestsDetailsEdit.this, R.string.Uploaded, Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(MyAdRequestsDetailsEdit.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });

                                            } else {
                                                Toast.makeText(MyAdRequestsDetailsEdit.this, R.string.locationfirst, Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(MyAdRequestsDetailsEdit.this, R.string.descriptionfirst, Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(MyAdRequestsDetailsEdit.this, R.string.areafirst, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MyAdRequestsDetailsEdit.this, R.string.toiletfirst, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MyAdRequestsDetailsEdit.this, R.string.bedssfirst, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MyAdRequestsDetailsEdit.this, R.string.cityfirst, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MyAdRequestsDetailsEdit.this, R.string.roomsfirst, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MyAdRequestsDetailsEdit.this, R.string.pricefirst, Toast.LENGTH_SHORT).show();
                }
            }

        }
        });




        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog alertadd = new AlertDialog.Builder(MyAdRequestsDetailsEdit.this).create();
                final LayoutInflater factory = LayoutInflater.from(MyAdRequestsDetailsEdit.this);
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
                        String url = "https://api.whatsapp.com/send?phone="+"+201554603000";
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
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("adsrequests").child(adkey);
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

    private void openmap()
    {
        String apiKey ="AIzaSyDbgYN-PQFjltxMiJmYwf1ywyusL8MpzMY";
        Places.initialize(getApplicationContext(), apiKey);

        maplayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        mtoolbar.setVisibility(View.GONE);
        mPlacesClient = Places.createClient(MyAdRequestsDetailsEdit.this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MyAdRequestsDetailsEdit.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MyAdRequestsDetailsEdit.this::onMapReady);
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
            Toast.makeText(MyAdRequestsDetailsEdit.this, R.string.Selectimage, Toast.LENGTH_SHORT).show();
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
        UserProfileImageRef.child(adkey).child(image).putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                UserProfileImageRef.child(adkey).child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).
                                child("adsrequests").child(adkey).child(image);
                        adsRequestRef = FirebaseDatabase.getInstance().getReference().child("adsrequests").child(adkey).child(image);

                        userRef.setValue(String.valueOf(uri));
                        adsRequestRef.setValue(String.valueOf(uri));


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
                        Toast.makeText(MyAdRequestsDetailsEdit.this, "Failure", Toast.LENGTH_LONG).show();

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
                                        child("adsrequests").child(adkey).child("screenshot");
                                adsRequestRef = FirebaseDatabase.getInstance().getReference().child("adsrequests").child(adkey).child("screenshot");

                                userRef.setValue(String.valueOf(uri));
                                adsRequestRef.setValue(String.valueOf(uri));


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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }





}