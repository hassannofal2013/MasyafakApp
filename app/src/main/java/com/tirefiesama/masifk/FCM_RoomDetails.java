package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class FCM_RoomDetails extends AppCompatActivity {
    String adkey ,citykey;
    private DatabaseReference mDatabase;
    TextView price,rooms,beds,description,adNumber,area,toilet;
    ImageView wificheck,parkingcheck;
    FloatingActionButton call;
    private Toolbar mtoolbar;
    CardView book;
    FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private Timer timer;
    private int current_position=0;
    ViewPager viewPager;
    private LinearLayout dotsLayout;
    private String[] imageUrls ;
    ImageView zoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm_room_details);

        SharedPreferences preferences = getSharedPreferences("sharedFile",MODE_PRIVATE);
        adkey = preferences.getString("adkey","adkey");
        citykey = preferences.getString("citykey","citykey");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        call = findViewById(R.id.fab);
        price = findViewById(R.id.pricetextView3);
        rooms = findViewById(R.id.roomstextView);
        beds = findViewById(R.id.bedstextView);
        description = findViewById(R.id.descriptiontextView);
        adNumber = findViewById(R.id.adnutextView);
        book = findViewById(R.id.book);
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.dotsContainer);
        area= findViewById(R.id.areaextView);
        toilet =findViewById(R.id.toiletextView12);
        parkingcheck =findViewById(R.id.parkcheckIcon);
        wificheck =findViewById(R.id.wificheckIcon);
        zoom =findViewById(R.id.zoom_outimageView17);



        mtoolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFirebaseUser != null)
                {
                    String userId= mAuth.getUid().toString();
                    DatabaseReference   userRef= FirebaseDatabase.getInstance().getReference().child("users");
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if (snapshot.child(userId).child("phone").exists()
                                    && snapshot.child(userId).child("email").exists())
                            {
                                SharedPreferences.Editor editor = getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                                editor.putBoolean("fromcity", true);
                                editor.apply();

                                Intent intent = new Intent(FCM_RoomDetails.this, FreeDaysActivity.class);
                                startActivity(intent);


                            }
                            else
                            {

                                Toast.makeText(FCM_RoomDetails.this, R.string.completeprofile, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(FCM_RoomDetails.this,RegisterActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });


                }
                else
                {
                    final AlertDialog alertadd = new AlertDialog.Builder(FCM_RoomDetails.this).create();
                    final LayoutInflater factory = LayoutInflater.from(FCM_RoomDetails.this);
                    View dialogView = factory.inflate(R.layout.sample, null);
                    TextView alerttextView = dialogView.findViewById(R.id.dialog_textView);
                    alerttextView.setText(getString(R.string.noaccount));
                    Button cancel =dialogView.findViewById(R.id.cancelBtn);
                    Button register =dialogView.findViewById(R.id.regestrationBtn);
                    register.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(FCM_RoomDetails.this,LoginActivity.class);
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

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(mFirebaseUser != null)
                {
                    final AlertDialog alertadd = new AlertDialog.Builder(FCM_RoomDetails.this).create();
                    final LayoutInflater factory = LayoutInflater.from(FCM_RoomDetails.this);
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
                else
                {
                    final AlertDialog alertadd = new AlertDialog.Builder(FCM_RoomDetails.this).create();
                    final LayoutInflater factory = LayoutInflater.from(FCM_RoomDetails.this);
                    View dialogView = factory.inflate(R.layout.sample, null);
                    TextView alerttextView = dialogView.findViewById(R.id.dialog_textView);
                    alerttextView.setText(getString(R.string.noaccount));
                    Button cancel =dialogView.findViewById(R.id.cancelBtn);
                    Button register =dialogView.findViewById(R.id.regestrationBtn);
                    register.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(FCM_RoomDetails.this,LoginActivity.class);
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
            mDatabase = FirebaseDatabase.getInstance().getReference().child("cities").child(citykey).child("ads").child(adkey);
            mDatabase.keepSynced(true);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    String imageScreenshotSt= "",image1St= "",image2St= "",image3St= "",image4St= "",image5St= "",image6St = "";

                    if(snapshot.child("screenshot").exists())
                    {
                        imageScreenshotSt = snapshot.child("screenshot").getValue().toString();
                    }

                    if(snapshot.child("image1").exists())
                    {
                        image1St = snapshot.child("image1").getValue().toString();
                    }

                    if(snapshot.child("image2").exists())
                    {
                        image2St= snapshot.child("image2").getValue().toString();
                    }

                    if(snapshot.child("image3").exists())
                    {
                        image3St = snapshot.child("image3").getValue().toString();
                    }

                    if(snapshot.child("image4").exists())
                    {
                        image4St = snapshot.child("image4").getValue().toString();
                    }

                    if(snapshot.child("image5").exists())
                    {
                        image5St = snapshot.child("image5").getValue().toString();
                    }

                    if(snapshot.child("image6").exists())
                    {
                        image6St = snapshot.child("image6").getValue().toString();
                    }

                    if ( ! image6St.equals(""))
                    {
                        imageUrls=new String[]{imageScreenshotSt,image1St,image2St,image3St,image4St,image5St,image6St};
                        PagerAdapter adapter = new ViewPagerAdapter(FCM_RoomDetails.this, imageUrls);
                        viewPager.setAdapter(adapter);
                        prepareDotes(current_position++);
                        creatSlideShow();
                    }
                    else
                    {
                        if ( ! image5St.equals(""))
                        {
                            imageUrls=new String[]{imageScreenshotSt,image1St,image2St,image3St,image4St,image5St};
                            PagerAdapter adapter = new ViewPagerAdapter(FCM_RoomDetails.this, imageUrls);
                            viewPager.setAdapter(adapter);
                            prepareDotes(current_position++);
                            creatSlideShow();
                        }
                        else
                        {
                            if ( ! image4St.equals(""))
                            {
                                imageUrls=new String[]{imageScreenshotSt,image1St,image2St,image3St,image4St};
                                PagerAdapter adapter = new ViewPagerAdapter(FCM_RoomDetails.this, imageUrls);
                                viewPager.setAdapter(adapter);
                                prepareDotes(current_position++);
                                creatSlideShow();
                            }
                            else
                            {
                                if ( ! image3St.equals(""))
                                {
                                    imageUrls=new String[]{imageScreenshotSt,image1St,image2St,image3St};
                                    PagerAdapter adapter = new ViewPagerAdapter(FCM_RoomDetails.this, imageUrls);
                                    viewPager.setAdapter(adapter);
                                    prepareDotes(current_position++);
                                    creatSlideShow();
                                }
                                else
                                {
                                    if ( ! image2St.equals(""))
                                    {
                                        imageUrls=new String[]{imageScreenshotSt,image1St,image2St};
                                        PagerAdapter adapter = new ViewPagerAdapter(FCM_RoomDetails.this, imageUrls);
                                        viewPager.setAdapter(adapter);
                                        prepareDotes(current_position++);
                                        creatSlideShow();
                                    }
                                    else
                                    {
                                        if ( ! image1St.equals(""))
                                        {
                                            imageUrls=new String[]{imageScreenshotSt,image1St};
                                            PagerAdapter adapter = new ViewPagerAdapter(FCM_RoomDetails.this, imageUrls);
                                            viewPager.setAdapter(adapter);
                                            prepareDotes(current_position++);
                                            creatSlideShow();
                                        }
                                        else
                                        {
                                            imageUrls=new String[]{imageScreenshotSt,image1St};
                                            PagerAdapter adapter = new ViewPagerAdapter(FCM_RoomDetails.this, imageUrls);
                                            viewPager.setAdapter(adapter);
                                            prepareDotes(current_position++);
                                            creatSlideShow();
                                        }

                                    }

                                }
                            }
                        }
                    }

                    if(snapshot.child("price").exists())
                    {
                        String St = snapshot.child("price").getValue().toString();
                        price.setText(St+" "+getString(R.string.pound));
                    }

                    if(snapshot.child("rooms").exists())
                    {
                        String St = snapshot.child("rooms").getValue().toString();
                        rooms.setText(St+" "+getString(R.string.rooms));
                    }

                    if(snapshot.child("beds").exists())
                    {
                        String St = snapshot.child("beds").getValue().toString();
                        beds.setText(St+" "+getString(R.string.beds));
                    }
                    if(snapshot.child("description").exists())
                    {
                        String St = snapshot.child("description").getValue().toString();
                        description.setText(St);
                    }


                    if(snapshot.child("userkey").exists())
                    {
                        String St = snapshot.child("userkey").getValue().toString();
                        SharedPreferences.Editor editor = getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                        editor.putString("ownerkey", St);
                        editor.apply();
                    }


                    if(snapshot.child("adnumber").exists())
                    {
                        String St = snapshot.child("adnumber").getValue().toString();

                        mtoolbar.setTitle(getString(R.string.adnuis)+St);
                        adNumber.setText(getString(R.string.adnuis)+St);

                    }

                    if(snapshot.child("area").exists())
                    {
                        String St = snapshot.child("area").getValue().toString();
                        area.setText(St+" "+getString(R.string.miters));
                    }
                    if(snapshot.child("toilet").exists())
                    {
                        String St = snapshot.child("toilet").getValue().toString();
                        toilet.setText(St+" "+getString(R.string.toilet));
                    }
                    if(snapshot.child("wifi").exists())
                    {
                        String St = snapshot.child("wifi").getValue().toString();
                        if(St.equals("false"))
                        {
                            wificheck.setImageResource(R.drawable.rong);
                        }
                    }

                    if(snapshot.child("parking").exists())
                    {
                        String St = snapshot.child("parking").getValue().toString();
                        if(St.equals("false"))
                        {
                            parkingcheck.setImageResource(R.drawable.rong);
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
                Intent intent = new Intent(FCM_RoomDetails.this,Full_Screen_ImageSliderActivity.class);
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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

       Intent intent = new Intent(FCM_RoomDetails.this,MainActivity.class);
       startActivity(intent);
        return super.onOptionsItemSelected(item);
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
}