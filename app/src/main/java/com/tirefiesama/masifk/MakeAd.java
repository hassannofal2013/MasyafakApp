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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import com.google.firebase.auth.FirebaseAuth;
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
import java.io.File;



import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MakeAd extends AppCompatActivity implements OnMapReadyCallback {
    private Spinner spinner,area,beds,toilet, rooms;
    private ArrayList<String> arrayList = new ArrayList<>();

    private LinearLayout image2layout, image3layout;
    private ImageView image1, image2, image3, image4, image5, image6;
    final static int Gallary_pic = 1 , screenshotint = 2;
    private Uri screenshotImageData, Image1Data, Image2Data, Image3Data, Image4Data, Image5Data, Image6Data;
    boolean selectImage1, selectImage2, selectImage3, selectImage4, selectImage5, selectImage6,
            loadedscreenshot,loadedimage1, loadedimage2, loadedimage3, loadedimage4, loadedimage5,
            anothercity = false, loadedimage6;
    private CardView publishing_request, locationRequest,cardimage2,cardimage3,cardimage4,cardimage5,cardimage6;
    private String Stwifi,Stparking,Sttoilet,Starea,imagename, userId, Stdescription, Stprice, Strooms,Stbeds, Stlocation, saveCurrentDate, adNumber, cityname;
    private EditText description, price, newcity;
    private CheckBox parrkingCheckBox,wifiCheckBox;

    private StorageReference UserProfileImageRef;

    private Toolbar mtoolbar;
    private FirebaseAuth mAuth;
    private CheckBox anothercityChBox;
    String StNumber;
    DatabaseReference adsRequestRef, userRef;
    CardView locationPicker;
    int PLACE_PICKER_REQUEST=1;


    CardView ok,cancel;
    GoogleMap mMap;
    EditText locationETxt;
    ImageView imagescreenshot;
    Boolean openpreesed=false,screenshotboolean=false;
    LinearLayout maplayout;
    private PlacesClient mPlacesClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final String TAG = "MapsActivity";

    private Location mLastKnownLocation;


    private final LatLng mDefaultLocation = new LatLng(31.600116, 31.089007);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted,checkdataexistBoolean=false;

    // Used for selecting the Current Place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    ScrollView scrollView;
    ImageView currentlocationImage;

    byte[] screenshotdata;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_ad);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid().toString();
        scrollView  = findViewById(R.id.Scrollview);
        currentlocationImage  = findViewById(R.id.currentlocationimageView);
        imagescreenshot = findViewById(R.id.screenshot_imageView);


        anothercityChBox = findViewById(R.id.checkBox);
        newcity = findViewById(R.id.anthercityname);
        beds = findViewById(R.id.bedsTxt2);
        ArrayList <String>  bedsArrayList = new ArrayList<String>();
        bedsArrayList.add("1");
        bedsArrayList.add("2");
        bedsArrayList.add("3");
        bedsArrayList.add("4");
        bedsArrayList.add("5");
        bedsArrayList.add("6");
        bedsArrayList.add("7");

        ArrayAdapter<String> bedsArrayAdapter = new
                ArrayAdapter<>(MakeAd.this, R.layout.support_simple_spinner_dropdown_item, bedsArrayList);
        beds.setAdapter(bedsArrayAdapter);

        area = findViewById(R.id.areaTxt);
        ArrayList <String>  areaArrayList = new ArrayList<String>();
        areaArrayList.add("50-75");
        areaArrayList.add("75-100");
        areaArrayList.add("100-125");
        areaArrayList.add("125-150");
        areaArrayList.add("150-175");
        areaArrayList.add("175-200");
        ArrayAdapter<String> areaArrayAdapter = new
                ArrayAdapter<>(MakeAd.this, R.layout.support_simple_spinner_dropdown_item, areaArrayList);
        area.setAdapter(areaArrayAdapter);

        toilet=findViewById(R.id.toiletTxt);
        ArrayList <String>  toiletArrayList = new ArrayList<String>();
        toiletArrayList.add("1");
        toiletArrayList.add("2");
        toiletArrayList.add("3");
        ArrayAdapter<String> toiletArrayAdapter = new
                ArrayAdapter<>(MakeAd.this, R.layout.support_simple_spinner_dropdown_item, toiletArrayList);
        toilet.setAdapter(toiletArrayAdapter);

        wifiCheckBox = findViewById(R.id.wificheckBox);
        parrkingCheckBox=findViewById(R.id.parkingcheckBox);
        locationPicker=findViewById(R.id.pickerCard);

        image2layout = findViewById(R.id.image2Layout);
        image3layout = findViewById(R.id.image3Layout);
        image1 = findViewById(R.id.image1View);
        image2 = findViewById(R.id.image2View2);
        image3 = findViewById(R.id.image3View);
        image4 = findViewById(R.id.image4View2);
        image5 = findViewById(R.id.image5View);
        image6 = findViewById(R.id.image6View2);
        cardimage2 = findViewById(R.id.image2cardview);
        cardimage3 = findViewById(R.id.image3cardview);
        cardimage4 = findViewById(R.id.image4cardview);
        cardimage5 = findViewById(R.id.image5cardview);
        cardimage6 = findViewById(R.id.image6cardview);

        publishing_request = findViewById(R.id.publish);
        description = findViewById(R.id.descriptionTxt);
        price = findViewById(R.id.priceTxt);
        rooms = findViewById(R.id.roomsTxt2);
        ArrayList <String>  roomsArrayList = new ArrayList<String>();
        roomsArrayList.add("1");
        roomsArrayList.add("2");
        roomsArrayList.add("3");
        roomsArrayList.add("4");
        roomsArrayList.add("5");
        ArrayAdapter<String> roomsArrayAdapter = new
                ArrayAdapter<>(MakeAd.this, R.layout.support_simple_spinner_dropdown_item, roomsArrayList);
        rooms.setAdapter(roomsArrayAdapter);

        locationETxt = findViewById(R.id.locationTxt);
        locationRequest = findViewById(R.id.locationcard2);

        mtoolbar = findViewById(R.id.toolbar_tool);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.makead));

        cancel = findViewById(R.id.cancelcardview);
        ok = findViewById(R.id.okcardview);
        ok.setEnabled(false);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maplayout.setVisibility(View.GONE);
                mtoolbar.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
                mtoolbar.setVisibility(View.VISIBLE);
                locationETxt.setText("");
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureScreen();
                maplayout.setVisibility(View.GONE);
                mtoolbar.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);

            }
        });

        currentlocationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCurrentPlace();
            }
        });

        locationPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openpreesed=true;
                maplayout.setVisibility(View.VISIBLE);
                mtoolbar.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                String apiKey ="AIzaSyDbgYN-PQFjltxMiJmYwf1ywyusL8MpzMY";
                Places.initialize(getApplicationContext(), apiKey);

                hideSoftKeyboard(MakeAd.this);
                mPlacesClient = Places.createClient(MakeAd.this);
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MakeAd.this);

                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);


                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(MakeAd.this);
            }
        });





        anothercityChBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!anothercity) {
                    anothercity = true;
                    newcity.setVisibility(View.VISIBLE);
                    spinner.setEnabled(false);
                } else if (anothercity) {
                    anothercity = false;
                    newcity.setVisibility(View.INVISIBLE);
                    spinner.setEnabled(true);
                }
            }
        });
        getAdNumber();
        publishing_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                enableAllFalse();
                checkdataexist();


            }
        });

        spinner = findViewById(R.id.spinner2);
        showDatainSpinner();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (anothercity) {
                    newcity.setVisibility(View.VISIBLE);

                } else {
                    newcity.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallary(v);
                selectImage1 = true;
                cardimage2.setVisibility(View.VISIBLE);
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallary(v);
                selectImage2 = true;
                image2layout.setVisibility(View.VISIBLE);
                cardimage3.setVisibility(View.VISIBLE);
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallary(v);
                selectImage3 = true;
                cardimage4.setVisibility(View.VISIBLE);
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallary(v);
                selectImage4 = true;
                image3layout.setVisibility(View.VISIBLE);
                cardimage5.setVisibility(View.VISIBLE);
            }
        });
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallary(v);
                selectImage5 = true;
                cardimage6.setVisibility(View.VISIBLE);
            }
        });
        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallary(v);
                selectImage6 = true;
            }
        });




        maplayout = findViewById(R.id.maplayout);











    }

    private void enableAllTrue()
    {
        description.setEnabled(true);
        area.setEnabled(true);
        price.setEnabled(true);
        rooms.setEnabled(true);
        toilet.setEnabled(true);
        beds.setEnabled(true);
        locationPicker.setEnabled(true);
        wifiCheckBox.setEnabled(true);
        parrkingCheckBox.setEnabled(true);
    }
    private void enableAllFalse()
    {
        description.setEnabled(false);
        area.setEnabled(false);
        price.setEnabled(false);
        rooms.setEnabled(false);
        toilet.setEnabled(false);
        beds.setEnabled(false);
        locationPicker.setEnabled(false);
        wifiCheckBox.setEnabled(false);
        parrkingCheckBox.setEnabled(false);
    }

    private void uploadscreenshot1()
    {
        SharedPreferences preferences = getSharedPreferences("sharedFile", MODE_PRIVATE);
        adNumber = preferences.getString("adkey", "adNumber");
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("ads").child(userId)
                .child(StNumber);
        UploadTask uploadTask = UserProfileImageRef.child("screenshot").putBytes(screenshotdata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(MakeAd.this, "screenshot upload failed", Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                UserProfileImageRef.child("screenshot").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        String adNo  = String.valueOf(Integer.valueOf(adNumber) - 1);
                        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).
                                child("adsrequests").child(adNo).child("screenshot");
                        adsRequestRef = FirebaseDatabase.getInstance().getReference().child
                                ("adsrequests").child(adNo).child("screenshot");

                        userRef.setValue(String.valueOf(uri));
                        adsRequestRef.setValue(String.valueOf(uri));

                        createRequestOnDataBase();


                    }
                });
            }
        });

    }


    private void changecounting(String newNumber) {

        DatabaseReference adsNumberRef
                = FirebaseDatabase.getInstance().getReference().child("counting");

        adsNumberRef.setValue(newNumber);




    }

    private void getAdNumber() {

        DatabaseReference adsNumberRef = FirebaseDatabase.getInstance().getReference().child("counting");
        adsNumberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    StNumber = snapshot.getValue().toString();
                    adNumber = String.valueOf(Integer.valueOf(StNumber) + 1);

                    SharedPreferences.Editor editor = getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                    editor.putString("adkey", adNumber);
                    editor.apply();
                    changecounting(adNumber);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void createRequestOnDataBase() {


        if(wifiCheckBox.isChecked()){Stwifi="true";}else {Stwifi="false";}
        if(parrkingCheckBox.isChecked()){Stparking="true";}else {Stparking="false";}
        cityname = spinner.getSelectedItem().toString();
        if (anothercity) {
            cityname = newcity.getText().toString();
        }


        HashMap admap = new HashMap();
        admap.put("description", Stdescription);
        admap.put("location", Stlocation);
        admap.put("price", Stprice);
        admap.put("rooms", Strooms);
        admap.put("beds", Stbeds);
        admap.put("toilet", Sttoilet);
        admap.put("area", Starea);
        admap.put("wifi", Stwifi);
        admap.put("parking", Stparking);
        admap.put("time", saveCurrentDate);
        admap.put("adnumber", StNumber);
        admap.put("city", cityname);
        admap.put("status","جاري العرض على المشرف");
        admap.put("ownerkey",userId);


        String stNo = String.valueOf(Integer.valueOf(adNumber)-1);

        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).
                child("adsrequests").child(stNo);
      adsRequestRef= FirebaseDatabase.getInstance().getReference().child("adsrequests").child(stNo);



        userRef.updateChildren(admap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

               adsRequestRef.updateChildren(admap);

                sendemailToAdmin(stNo);



            }
        });


    }

    private void uploadimages() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat Date = new SimpleDateFormat("dd-MMMM-yyyy");
        SimpleDateFormat time = new SimpleDateFormat("hh-mm-ss");
        saveCurrentDate = Date.format(calFordDate.getTime()) + time.format(calFordDate.getTime());


        userId = mAuth.getUid().toString();


        SharedPreferences preferences = getSharedPreferences("sharedFile", MODE_PRIVATE);
        adNumber = preferences.getString("adNumber", "adNumber");

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("ads").child(userId)
                .child(StNumber);

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

        uploadscreenshot1();


    }

    private void uploadimage(String image, Uri data) {
        UserProfileImageRef.child(image).putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                UserProfileImageRef.child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {

                      DatabaseReference  userImageRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).
                                child("adsrequests").child(StNumber).child(image);
                        DatabaseReference adsRequestImageRef = FirebaseDatabase.getInstance().getReference().child("adsrequests").child(StNumber).child(image);

                        userImageRef.setValue(String.valueOf(uri));
                        adsRequestImageRef.setValue(String.valueOf(uri));


                    }
                });
            }
        });
    }

    private void checkdataexist() {
        cityname = spinner.getSelectedItem().toString();
        if (anothercity) {
            cityname = newcity.getText().toString();
        }
        if (loadedimage1) {
            Stdescription = description.getText().toString();
            Stprice = price.getText().toString();
            Stlocation = locationETxt.getText().toString();
            Strooms = rooms.getSelectedItem().toString();
            Stbeds = beds.getSelectedItem().toString();
            Sttoilet = toilet.getSelectedItem().toString();
            Starea = area.getSelectedItem().toString();

            if (!TextUtils.isEmpty(Stdescription)) {
                if (!TextUtils.isEmpty(Stprice)) {
                    if (!TextUtils.isEmpty(Starea)) {
                        if (!TextUtils.isEmpty(Sttoilet)) {
                        if (!TextUtils.isEmpty(Stlocation)) {
                            if (!TextUtils.isEmpty(Strooms)) {
                                if (!TextUtils.isEmpty(Stbeds)) {
                                    if (!TextUtils.isEmpty(cityname))
                                    {
                                      uploadimages();
                                        progressDialog = ProgressDialog.show(MakeAd.this,
                                                getString(R.string.wait),getString(R.string.uploading),false,false);
                                    }
                                    else {
                                        Toast.makeText(MakeAd.this, R.string.cityfirst, Toast.LENGTH_SHORT).show();
                                        enableAllTrue();
                                    }
                                } else {
                                    Toast.makeText(MakeAd.this, R.string.bedssfirst, Toast.LENGTH_SHORT).show();
                                    enableAllTrue();
                                }
                            } else {
                                Toast.makeText(MakeAd.this, R.string.roomsfirst, Toast.LENGTH_SHORT).show();
                                enableAllTrue();
                            }

                        } else {
                            Toast.makeText(MakeAd.this, R.string.locationfirst, Toast.LENGTH_SHORT).show();
                            enableAllTrue();
                        }
                        }  else {
                            Toast.makeText(MakeAd.this, R.string.toiletfirst, Toast.LENGTH_SHORT).show();
                            enableAllTrue();
                        }
                    }  else {
                        Toast.makeText(MakeAd.this, R.string.areafirst, Toast.LENGTH_SHORT).show();
                        enableAllTrue();
                    }

                } else {
                    Toast.makeText(MakeAd.this, R.string.pricefirst, Toast.LENGTH_SHORT).show();
                    enableAllTrue();
                }

            } else {
                Toast.makeText(MakeAd.this, R.string.descriptionfirst, Toast.LENGTH_SHORT).show();
                enableAllTrue();
            }
        } else {
            Toast.makeText(MakeAd.this, R.string.imagefirst, Toast.LENGTH_SHORT).show();
            enableAllTrue();
        }


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == screenshotint)
        {
            if (resultCode == RESULT_OK)
            {
                if (loadedscreenshot)
                {
                 screenshotImageData = data.getData();
                    Picasso.get().load(screenshotImageData).into(imagescreenshot);
                }
            }
        }

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
            Toast.makeText(MakeAd.this, R.string.Selectimage, Toast.LENGTH_SHORT).show();
        }
    }


    public void openGallary(View view) {
        Intent gallaryIntint = new Intent();
        gallaryIntint.setAction(Intent.ACTION_GET_CONTENT);
        gallaryIntint.setType("image/*");
        startActivityForResult(gallaryIntint, Gallary_pic);

    }

    private void showDatainSpinner() {
        DatabaseReference marketRef = FirebaseDatabase.getInstance().getReference()
                .child("cities");
        marketRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    arrayList.clear();
                    for (DataSnapshot city : dataSnapshot.getChildren()) {
                        arrayList.add(city.child("name").getValue().toString());
                    }
                    ArrayAdapter<String> arrayAdapter = new
                            ArrayAdapter<>(MakeAd.this, R.layout.support_simple_spinner_dropdown_item, arrayList);
                    spinner.setAdapter(arrayAdapter);
                }
                else
                    {
                        spinner.setEnabled(false);
                        newcity.setVisibility(View.VISIBLE);
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        SendToMainActivity();

        return super.onOptionsItemSelected(item);
    }

    private void SendToMainActivity()
    {
        Intent intent = new Intent(MakeAd.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendemailToAdmin(String adNo)
    {
        String message1 = getResources().getString(R.string.newAdMaded);
        String subject1 = getResources().getString(R.string.newAdNo) +"\n"+
                " "+adNo +
                getResources().getString(R.string.madedsucc) +"\n";

        JavaMailAPI javaMailAPI1 = new JavaMailAPI(MakeAd.this, "masifkapp@gmail.com",message1,subject1);
        javaMailAPI1.execute();
        progressDialog.dismiss();
        SendToMainActivity();
        Toast.makeText(MakeAd.this, getResources().getString(R.string.Uploaded), Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
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




    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng balteem = new LatLng(31.600116, 31.089007);
        mMap.addMarker(new MarkerOptions().position(balteem).title("Marker in bltim"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(balteem));

        //
        // PASTE THE LINES BELOW THIS COMMENT
        //

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
                ok.setEnabled(true);



            }
        });

        pickCurrentPlace();
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

              imagescreenshot.setImageBitmap(snapshot);
                imagescreenshot.setVisibility(View.VISIBLE);
                loadedscreenshot=true;
                // Get the data from an ImageView as bytes
                imagescreenshot.setDrawingCacheEnabled(true);
                imagescreenshot.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imagescreenshot.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                screenshotdata = baos.toByteArray();

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

        InputMethodManager inputMethodManager = (InputMethodManager)
                        activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);


    }




    private void copytoAdRequests( String adkey) {


        final DatabaseReference adRef = FirebaseDatabase.getInstance().getReference().
                child("users").child(userId).child("adsrequests").child(adkey);

        adRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                adsRequestRef = FirebaseDatabase.getInstance().getReference().child
                        ("adsrequests").child(adkey);


                adsRequestRef.child(adkey).child("adkey").setValue(adkey);
                if(dataSnapshot.child("adnumber").exists())
                {
                    String stavilabile=dataSnapshot.child("adnumber").getValue().toString();
                    adsRequestRef.child(adkey).child("adnumber").setValue(stavilabile);
                }
                if(dataSnapshot.child("citykey").exists())
                {
                    String stavilabile=dataSnapshot.child("citykey").getValue().toString();
                    adsRequestRef.child("adkey").child("cityky").setValue(stavilabile);
                }

                if(dataSnapshot.child("area").exists())
                {
                    String stavilabile=dataSnapshot.child("area").getValue().toString();
                    adsRequestRef.child(adkey).child("area").setValue(stavilabile);
                }

                if(dataSnapshot.child("beds").exists())
                {
                    String stcomments=dataSnapshot.child("beds").getValue().toString();
                    adsRequestRef.child(adkey ).child("beds").setValue(stcomments);
                }

                if(dataSnapshot.child("description").exists())
                {
                    String stdescription=dataSnapshot.child("description").getValue().toString();
                    adsRequestRef.child(adkey ).child("description").setValue(stdescription);
                }

                if(dataSnapshot.child("image1").exists())
                {
                    String stimage1=dataSnapshot.child("image1").getValue().toString();
                    adsRequestRef.child(adkey ).child("image1").setValue(stimage1);
                }

                if(dataSnapshot.child("image2").exists())
                {
                    String stimage2=dataSnapshot.child("image2").getValue().toString();
                    adsRequestRef.child(adkey ).child("image2").setValue(stimage2);
                }

                if(dataSnapshot.child("image3").exists())
                {
                    String stimage3=dataSnapshot.child("image3").getValue().toString();
                    adsRequestRef.child(adkey ).child("image3").setValue(stimage3);
                }

                if(dataSnapshot.child("image4").exists())
                {
                    String stimage4=dataSnapshot.child("image4").getValue().toString();
                    adsRequestRef.child(adkey ).child("image4").setValue(stimage4);
                }
                if(dataSnapshot.child("image5").exists())
                {
                    String stimage4=dataSnapshot.child("image5").getValue().toString();
                    adsRequestRef.child(adkey ).child("image5").setValue(stimage4);
                }
                if(dataSnapshot.child("image6").exists())
                {
                    String stimage4=dataSnapshot.child("image6").getValue().toString();
                    adsRequestRef.child(adkey ).child("image6").setValue(stimage4);
                }

                if(dataSnapshot.child("location").exists())
                {
                    String stinbasket=dataSnapshot.child("location").getValue().toString();
                    adsRequestRef.child(adkey ).child("location").setValue(stinbasket);
                }

                if(dataSnapshot.child("parking").exists())
                {
                    String stinbasket=dataSnapshot.child("parking").getValue().toString();
                    adsRequestRef.child(adkey ).child("parking").setValue(stinbasket);
                }

                if(dataSnapshot.child("price").exists())
                {
                    String stprice=dataSnapshot.child("price").getValue().toString();
                    adsRequestRef.child(adkey ).child("price").setValue(stprice);
                }

                if(dataSnapshot.child("rooms").exists())
                {
                    String stlikes=dataSnapshot.child("rooms").getValue().toString();
                    adsRequestRef.child(adkey ).child("rooms").setValue(stlikes);
                }

                if(dataSnapshot.child("toilet").exists())
                {
                    String stprice=dataSnapshot.child("toilet").getValue().toString();
                    adsRequestRef.child(adkey ).child("toilet").setValue(stprice);
                }
                if(dataSnapshot.child("wifi").exists())
                {
                    String stprice=dataSnapshot.child("wifi").getValue().toString();
                    adsRequestRef.child(adkey ).child("wifi").setValue(stprice);
                }


                if(dataSnapshot.child("likes").exists())
                {
                    for(DataSnapshot userId : dataSnapshot.child("likes").getChildren() )
                    {
                        String uid = userId.getKey();
                        String stliketime= dataSnapshot.child("likes").child(uid).child("time").getValue().toString();
                        adsRequestRef.child(adkey ).child("likes").child(uid).child("time").setValue(stliketime);
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
                            adsRequestRef.child(adkey ).child("calender").child(month).child(day).setValue(stliketime);

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