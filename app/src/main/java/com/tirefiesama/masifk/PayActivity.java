package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PayActivity extends AppCompatActivity {


    private static final String ACTIVITY_NUMBER = "activity_num";
    private static final String LOG_TAG = "iabv3";

    // PRODUCT & SUBSCRIPTION IDS
    private static final String PRODUCT_ID = "masifak5pound";
    private static final String SUBSCRIPTION_ID = "com.anjlab.test.iab.subs1";
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk1shcW6sy/02UMzOjEOqnApxoL3F43iiwg6cluGgRfF+c/MWkhrX+S5v9/WfldfVYcSWnCbE4MkifvS0PtbzA/wtANj2wJiZ14Q/qBlejOiM9lCtOE5zBq/kOkafRbz3GT80VDKLgeknaARnKnfKmd1vCk91E786ogy+cl8C2oWIhVwWi4cNIaiLAD6marTPZwLgA3MauSfBrJStNr2DmVoNIG+QjJuI0lfj/LU7t40VoOkTmllvWoqvfEo1hgPURyvdR8YOtpSd1VZSjCThbsWkc9jn/u1zyYNm9JS2//ZZ5NDH3btHxx9VtGXt80Tjn7cx5DW07ZZJ+SC9iXQycQIDAQAB"; // PUT YOUR MERCHANT KEY HERE;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID= "5830707462990685011";

    private BillingProcessor bp;
    private boolean readyToPurchase = false;
    String userId;
    FirebaseAuth mAuth;

    TextView TestBtn;
    String roomkey,citykey,StChosenDays,ownerkey;
    DatabaseReference adRef,rentedRef;

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mAuth=FirebaseAuth.getInstance();
        userId= mAuth.getUid();

        SharedPreferences preferences = getSharedPreferences("sharedFile",MODE_PRIVATE);
        roomkey = preferences.getString("roomkey","roomkey");
        citykey = preferences.getString("citykey","citykey");
        ownerkey = preferences.getString("ownerkey","ownerkey");
        StChosenDays= preferences.getString("StChosenDays","StChosenDays");


      adRef = FirebaseDatabase.getInstance().getReference().
                child("cities").child(citykey).child("ads").child(roomkey);

        rentedRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("rented");


        TestBtn=findViewById(R.id.TestBtn);

        TextView title = (TextView)findViewById(R.id.titleTextView);
        title.setText(String.format(roomkey, getIntent().getIntExtra(ACTIVITY_NUMBER, 1)));


        TestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                copytorented(citykey,roomkey);
                MakeDaysBusy(citykey,roomkey,StChosenDays);

               // updateTextViews();
                //sendConfermationEmail();
              //  sendToMainActivity();
            }
        });

        if(!BillingProcessor.isIabServiceAvailable(this)) {
            showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }
        bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

                MakeDaysBusy(citykey,roomkey,StChosenDays);
                copytorented(citykey,roomkey);
                updateTextViews();
                sendConfermationEmail();
               // sendToMainActivity();
            }
            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                showToast("onBillingError: " + Integer.toString(errorCode));
            }
            @Override
            public void onBillingInitialized() {
               // showToast("onBillingInitialized");
                readyToPurchase = true;
                updateTextViews();
            }
            @Override
            public void onPurchaseHistoryRestored() {
                showToast("onPurchaseHistoryRestored");
                for(String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });
    }



    private void MakeDaysBusyOwnerPublishedAds(String ownerkey, String citykey, String roomkey, String stChosenDays)
    {
        DatabaseReference adOwnertRef = FirebaseDatabase.getInstance().getReference().
                child("users").child(ownerkey).child("published").child(roomkey).child("calender");
        int len = stChosenDays.length();
        int NoDayes;

        for( NoDayes = len/12;NoDayes>0;NoDayes--)
        {
            String month_day = stChosenDays.substring((11*NoDayes+1+(NoDayes-1))-12,11*NoDayes+(NoDayes-1));

            String month = month_day.substring(0,3);
            String day = month_day.substring(4,6);
            String year = month_day.substring(7,11);
            String monthkey = month+"_"+year;
            adOwnertRef.child(monthkey).child(day).setValue("confirmed");

        }
    }

    private void MakeDaysBusy(String citykey, String roomkey, String stChosenDays)
    {
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
            adRef.child(monthkey).child(day).setValue("confirmed");

        }


    }


    @Override
    protected void onResume() {
        super.onResume();

        updateTextViews();
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateTextViews() {
        TextView text = (TextView)findViewById(R.id.productIdTextView);
        text.setText(String.format("%s is%s purchased", PRODUCT_ID, bp.isPurchased(PRODUCT_ID) ? "" : " not"));
        text.setText(String.format("%s is%s subscribed", SUBSCRIPTION_ID, bp.isSubscribed(SUBSCRIPTION_ID) ? "" : " not"));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onClick(View v) {
        if (!readyToPurchase) {
            showToast("Billing not initialized.");
            return;
        }
        switch (v.getId()) {
            case R.id.purchaseButton:
                bp.purchase(this,PRODUCT_ID);
                break;

            default:
                break;
        }
    }

    private void copytorented(String city, String adkey)
    {



        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat Date = new SimpleDateFormat("dd-MMMM-yyyy");
        SimpleDateFormat time = new SimpleDateFormat("hh-mm-ss");
        String  saveCurrentDate = Date.format(calFordDate.getTime()) + time.format(calFordDate.getTime());
        rentedRef.child(adkey).child("citykey").setValue(city);
        rentedRef.child(adkey).child("rentingtime").setValue(saveCurrentDate);

        adRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {


                for (DataSnapshot info : dataSnapshot.getChildren())
                {
                    String infoName = info.getKey();
                    String infoValue = dataSnapshot.child(infoName).getValue().toString();
                    rentedRef.child(adkey).child(infoName).setValue(infoValue);


                }
                rentedRef.child(adkey).child("calender").removeValue();
                rentedRef.child(adkey).child("ownerkey").removeValue();
                for (DataSnapshot info : dataSnapshot.child("ownerkey").getChildren())
                {
                    String infoName = info.getKey();
                    String infoValue = dataSnapshot.child("ownerkey").child(infoName).getValue().toString();
                    rentedRef.child(adkey).child("ownerkey").child(infoName).setValue(infoValue);


                }
              //  sendToMainActivity();
               /*
                if(dataSnapshot.child("adnumber").exists())
                {
                    String stavilabile=dataSnapshot.child("adnumber").getValue().toString();
                    rentedRef.child(adkey).child("adnumber").setValue(stavilabile);
                }

                if(dataSnapshot.child("area").exists())
                {
                    String stavilabile=dataSnapshot.child("area").getValue().toString();
                    rentedRef.child(adkey).child("area").setValue(stavilabile);
                }
                if(dataSnapshot.child("city").exists())
                {
                    String stavilabile=dataSnapshot.child("city").getValue().toString();
                    rentedRef.child(adkey).child("city").setValue(stavilabile);
                }

                if(dataSnapshot.child("beds").exists())
                {
                    String stcomments=dataSnapshot.child("beds").getValue().toString();
                    rentedRef.child(adkey ).child("beds").setValue(stcomments);
                }

                if(dataSnapshot.child("description").exists())
                {
                    String stdescription=dataSnapshot.child("description").getValue().toString();
                    rentedRef.child(adkey ).child("description").setValue(stdescription);
                }

                if(dataSnapshot.child("image1").exists())
                {
                    String stimage1=dataSnapshot.child("image1").getValue().toString();
                    rentedRef.child(adkey ).child("image1").setValue(stimage1);
                }

                if(dataSnapshot.child("image2").exists())
                {
                    String stimage2=dataSnapshot.child("image2").getValue().toString();
                    rentedRef.child(adkey ).child("image2").setValue(stimage2);
                }

                if(dataSnapshot.child("image3").exists())
                {
                    String stimage3=dataSnapshot.child("image3").getValue().toString();
                    rentedRef.child(adkey ).child("image3").setValue(stimage3);
                }

                if(dataSnapshot.child("image4").exists())
                {
                    String stimage4=dataSnapshot.child("image4").getValue().toString();
                    rentedRef.child(adkey ).child("image4").setValue(stimage4);
                }
                if(dataSnapshot.child("image5").exists())
                {
                    String stimage4=dataSnapshot.child("image5").getValue().toString();
                    rentedRef.child(adkey ).child("image5").setValue(stimage4);
                }
                if(dataSnapshot.child("image6").exists())
                {
                    String stimage4=dataSnapshot.child("image6").getValue().toString();
                    rentedRef.child(adkey ).child("image6").setValue(stimage4);
                }

                if(dataSnapshot.child("location").exists())
                {
                    String stinbasket=dataSnapshot.child("location").getValue().toString();
                    rentedRef.child(adkey ).child("location").setValue(stinbasket);
                }

                if(dataSnapshot.child("parking").exists())
                {
                    String stinbasket=dataSnapshot.child("parking").getValue().toString();
                    rentedRef.child(adkey ).child("parking").setValue(stinbasket);
                }


                if(dataSnapshot.child("ownerkey").exists()) {
                    if (dataSnapshot.child("ownerkey").child("phone").exists()) {
                        String stphone = dataSnapshot.child("ownerkey").child("phone").getValue().toString();
                        rentedRef.child(adkey).child("ownerkey").child("phone").setValue(stphone);
                    }
                    if (dataSnapshot.child("ownerkey").exists()) {
                        if (dataSnapshot.child("ownerkey").child("name").exists()) {
                            String stname = dataSnapshot.child("ownerkey").child("name").getValue().toString();
                            rentedRef.child(adkey).child("ownerkey").child("name").setValue(stname);
                        }


                        if (dataSnapshot.child("price").exists()) {
                            String stprice = dataSnapshot.child("price").getValue().toString();
                            rentedRef.child(adkey).child("price").setValue(stprice);
                        }

                        if (dataSnapshot.child("rooms").exists()) {
                            String stlikes = dataSnapshot.child("rooms").getValue().toString();
                            rentedRef.child(adkey).child("rooms").setValue(stlikes);
                        }

                        if (dataSnapshot.child("toilet").exists()) {
                            String stprice = dataSnapshot.child("toilet").getValue().toString();
                            rentedRef.child(adkey).child("toilet").setValue(stprice);
                        }
                        if (dataSnapshot.child("wifi").exists()) {
                            String stprice = dataSnapshot.child("wifi").getValue().toString();
                            rentedRef.child(adkey).child("wifi").setValue(stprice);
                        }



                    }
                }

                */
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void sendConfermationEmail()
    {


        DatabaseReference emailRef= FirebaseDatabase.getInstance().getReference().child("users")
                .child(userId);
        DatabaseReference ownerRef= FirebaseDatabase.getInstance().getReference().child("cities")
                .child(citykey).child("ads").child(roomkey).child("ownerkey");

        ownerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                 String   ownerName = snapshot.child("name").getValue().toString();
                  String  ownerPhone= snapshot.child("phone").getValue().toString();
                  String ownerEmail= snapshot.child("email").getValue().toString();
                    String ownerKey= snapshot.child("key").getValue().toString();

                    MakeDaysBusyOwnerPublishedAds(ownerKey,citykey,roomkey,StChosenDays);




                    emailRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if(snapshot.exists())
                            {
                                String  useremail = snapshot.child("email").getValue().toString();
                                String  userphone= snapshot.child("phone").getValue().toString();
                                String  username= snapshot.child("name").getValue().toString();

                                String message = getResources().getString(R.string.confirmed);
                                String subject = getResources().getString(R.string.bookingconfirmed) +"\n"+
                                        getResources().getString(R.string.fordays) +" "+StChosenDays +"\n"+
                                        getResources().getString(R.string.contactwithowner) +"\n"+
                                        ownerName +"\n"+
                                        ownerPhone +"\n"+
                                        getResources().getString(R.string.adnuis)+"  " +roomkey;

                                JavaMailAPI javaMailAPI = new JavaMailAPI(PayActivity.this, useremail,subject,message);
                                javaMailAPI.execute();


                                String message1 = getResources().getString(R.string.confirmed);
                                String subject1 = getResources().getString(R.string.bookingconfirmed) +"\n"+
                                        getResources().getString(R.string.fordays) +" "+StChosenDays +"\n"+
                                        getResources().getString(R.string.contactwithuser) +"\n"+
                                        ownerName +"\n"+
                                        ownerPhone +"\n"+
                                        getResources().getString(R.string.adnuis)+"  " +roomkey;

                                JavaMailAPI javaMailAPI1 = new JavaMailAPI(PayActivity.this, ownerEmail,subject1,message1);
                                javaMailAPI1.execute();



                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }






}