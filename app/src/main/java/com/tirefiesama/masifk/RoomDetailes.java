package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RoomDetailes extends AppCompatActivity {

    String adkey ,citykey,Stpostion;
    private DatabaseReference mDatabase,likeRef,UsersRef;
    private Boolean increase=true,decrease=false,automatic = true;

    TextView price,rooms,beds,description,adNumber,area,toilet,roomratingTextView,ownerratingTextView;
    ImageView wificheck,parkingcheck;

    FloatingActionButton call;
    private Toolbar mtoolbar;
    RatingBar roomrating,ownerrating;
    FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private Timer timer;
    private int current_position=0;
    ViewPager viewPager;
    private LinearLayout dotsLayout,book,ratingLayout;
    String Norate1,Norate2,Norate3,Norate4,Norate5;
    String Norate1owner,Norate2owner,Norate3owner,Norate4owner,Norate5owner;
    private String[] imageUrls ;
    ImageView zoom;
    ImageView Likebtn;
    Boolean LikeChecker =false,Liking=false,WifiBol = false, ParkingBol=false ;
    private String userId;

    String imageScreenshotSt= "",image1St= "",image2St= "",image3St= "",image4St= "",image5St= "",image6St = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detailes);

        SharedPreferences preferences = getSharedPreferences("sharedFile",MODE_PRIVATE);
        adkey = preferences.getString("adkey","adkey");
        citykey = preferences.getString("citykey","citykey");
        Stpostion = getIntent().getStringExtra("Stpostion");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser != null) {
            userId = mFirebaseUser.getUid();}

        Likebtn = findViewById(R.id.likeimageview);
        roomratingTextView = findViewById(R.id.roomevaluationTextView);
        ownerratingTextView = findViewById(R.id.ownerevaluationTextView);
        ratingLayout = findViewById(R.id.ratinglayout);
        roomrating = findViewById(R.id.Roomrating);
        ownerrating = findViewById(R.id.Ownerrating);
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
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if (snapshot.child(userId).child("phone").exists()
                            && snapshot.child(userId).child("email").exists())
                            {
                                SharedPreferences.Editor editor = getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                                editor.putBoolean("fromcity", true);
                                editor.apply();

                                Intent intent = new Intent(RoomDetailes.this, FreeDaysActivity.class);
                                startActivity(intent);


                            }
                            else
                            {

                                Toast.makeText(RoomDetailes.this, R.string.completeprofile, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RoomDetailes.this,RegisterActivity.class);
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
                    final AlertDialog alertadd = new AlertDialog.Builder(RoomDetailes.this).create();
                    final LayoutInflater factory = LayoutInflater.from(RoomDetailes.this);
                    View dialogView = factory.inflate(R.layout.sample, null);
                    TextView alerttextView = dialogView.findViewById(R.id.dialog_textView);
                    alerttextView.setText(getString(R.string.noaccount));
                    Button cancel =dialogView.findViewById(R.id.cancelBtn);
                    Button register =dialogView.findViewById(R.id.regestrationBtn);
                    register.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(RoomDetailes.this,LoginActivity.class);
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




        ratingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final AlertDialog alertadd = new AlertDialog.Builder(RoomDetailes.this).create();
                final LayoutInflater factory = LayoutInflater.from(RoomDetailes.this);
                View dialogView = factory.inflate(R.layout.rating_details, null);
                TextView roomR5 = dialogView.findViewById(R.id.roomR5);
                TextView roomR4 = dialogView.findViewById(R.id.roomR4);
                TextView roomR3 = dialogView.findViewById(R.id.roomR3);
                TextView roomR2 = dialogView.findViewById(R.id.roomR2);
                TextView roomR1 = dialogView.findViewById(R.id.roomR1);
                roomR5.setText(Norate5);
                roomR4.setText(Norate4);
                roomR3.setText(Norate3);
                roomR2.setText(Norate2);
                roomR1.setText(Norate1);

                TextView ownerR5 = dialogView.findViewById(R.id.ownerR5);
                TextView ownerR4 = dialogView.findViewById(R.id.ownerR4);
                TextView ownerR3 = dialogView.findViewById(R.id.ownerR3);
                TextView ownerR2 = dialogView.findViewById(R.id.ownerR2);
                TextView ownerR1 = dialogView.findViewById(R.id.ownerR1);
                ownerR5.setText(Norate5owner);
                ownerR4.setText(Norate4owner);
                ownerR3.setText(Norate3owner);
                ownerR2.setText(Norate2owner);
                ownerR1.setText(Norate1owner);

                alertadd.setView(dialogView);
                alertadd.show();



            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(mFirebaseUser != null)
                {
                    final AlertDialog alertadd = new AlertDialog.Builder(RoomDetailes.this).create();
                    final LayoutInflater factory = LayoutInflater.from(RoomDetailes.this);
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
                    final AlertDialog alertadd = new AlertDialog.Builder(RoomDetailes.this).create();
                    final LayoutInflater factory = LayoutInflater.from(RoomDetailes.this);
                    View dialogView = factory.inflate(R.layout.sample, null);
                    TextView alerttextView = dialogView.findViewById(R.id.dialog_textView);
                    alerttextView.setText(getString(R.string.noaccount));
                    Button cancel =dialogView.findViewById(R.id.cancelBtn);
                    Button register =dialogView.findViewById(R.id.regestrationBtn);
                    register.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(RoomDetailes.this,LoginActivity.class);
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





        if(adkey != null )
        {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("cities").child(citykey).child("ads").child(adkey);
            likeRef  = FirebaseDatabase.getInstance().getReference().child("likes").child(citykey).child("ads").child(adkey);
          if( ! TextUtils.isEmpty(userId)) {
              UsersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId); }
          mDatabase.keepSynced(true);
            likeRef.keepSynced(true);

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {

                    if (snapshot.child("rating").exists())
                    {
                        ratingLayout.setVisibility(View.VISIBLE);

                        Float intNorate1=Float.valueOf(0),intNorate2=Float.valueOf(0),intNorate3=Float.valueOf(0),intNorate4=Float.valueOf(0),intNorate5=Float.valueOf(0),intNoUsers=Float.valueOf(0);
                        Float intTotal;
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
                        intTotal = (intNorate1+intNorate2+intNorate3+intNorate4+intNorate5)/intNoUsers;
                        roomratingTextView.setText(String.valueOf(intTotal).substring(0,3)+"  "+getString(R.string.roomevaluation));
                        roomrating.setRating(intTotal);



                        Float intNorate1owner = Float.valueOf(0),intNorate2owner= Float.valueOf(0),intNorate3owner= Float.valueOf(0),intNorate4owner= Float.valueOf(0)
                                ,intNorate5owner= Float.valueOf(0),intNoUsersowner= Float.valueOf(0),intTotalowner= Float.valueOf(0);

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
                        ownerratingTextView.setText(String.valueOf(intTotalowner).substring(0,3)+"  "+getString(R.string.Ownervaluation));
                        ownerrating.setRating(intTotalowner);


                    }
                    else { ratingLayout.setVisibility(View.GONE);}

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
                        PagerAdapter adapter = new ViewPagerAdapter(RoomDetailes.this, imageUrls);
                        viewPager.setAdapter(adapter);
                        prepareDotes(current_position++);
                     if( automatic)   creatSlideShow();
                    }
                    else
                    {
                        if ( ! image5St.equals(""))
                        {
                            imageUrls=new String[]{imageScreenshotSt,image1St,image2St,image3St,image4St,image5St};
                            PagerAdapter adapter = new ViewPagerAdapter(RoomDetailes.this, imageUrls);
                            viewPager.setAdapter(adapter);
                            prepareDotes(current_position++);
                            if(automatic)      creatSlideShow();
                        }
                        else
                        {
                            if ( ! image4St.equals(""))
                            {
                                imageUrls=new String[]{imageScreenshotSt,image1St,image2St,image3St,image4St};
                                PagerAdapter adapter = new ViewPagerAdapter(RoomDetailes.this, imageUrls);
                                viewPager.setAdapter(adapter);
                                prepareDotes(current_position++);
                                if(automatic)       creatSlideShow();
                            }
                            else
                            {
                                if ( ! image3St.equals(""))
                                {
                                    imageUrls=new String[]{imageScreenshotSt,image1St,image2St,image3St};
                                    PagerAdapter adapter = new ViewPagerAdapter(RoomDetailes.this, imageUrls);
                                    viewPager.setAdapter(adapter);
                                    prepareDotes(current_position++);
                                    if(automatic)       creatSlideShow();
                                }
                                else
                                {
                                    if ( ! image2St.equals(""))
                                    {
                                        imageUrls=new String[]{imageScreenshotSt,image1St,image2St};
                                        PagerAdapter adapter = new ViewPagerAdapter(RoomDetailes.this, imageUrls);
                                        viewPager.setAdapter(adapter);
                                        prepareDotes(current_position++);
                                        if(automatic)     creatSlideShow();
                                    }
                                    else
                                    {
                                        if ( ! image1St.equals(""))
                                        {
                                            imageUrls=new String[]{imageScreenshotSt,image1St};
                                            PagerAdapter adapter = new ViewPagerAdapter(RoomDetailes.this, imageUrls);
                                            viewPager.setAdapter(adapter);
                                            prepareDotes(current_position++);
                                            if(automatic)      creatSlideShow();
                                        }
                                        else
                                        {
                                            imageUrls=new String[]{imageScreenshotSt,image1St};
                                            PagerAdapter adapter = new ViewPagerAdapter(RoomDetailes.this, imageUrls);
                                            viewPager.setAdapter(adapter);
                                            prepareDotes(current_position++);
                                            if(automatic)      creatSlideShow();
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
                        String Stcity=" ";
                        if(snapshot.child("city").exists())
                        {
                             Stcity = snapshot.child("city").getValue().toString();
                        }



                        mtoolbar.setTitle(Stcity+" "+getString(R.string.adnuis)+St);
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
                            WifiBol = false;
                        }
                    }

                    if(snapshot.child("parking").exists())
                    {
                        String St = snapshot.child("parking").getValue().toString();
                        if(St.equals("false"))
                        {
                            parkingcheck.setImageResource(R.drawable.rong);
                            ParkingBol =false;
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }





        Likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFirebaseUser != null)
                {
                    userId = mFirebaseUser.getUid();
                    LikeChecker = true;
                    Liking = true;
                    UsersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                    UsersRef.child("likes").child(adkey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Calendar calFordDate = Calendar.getInstance();
                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                            String saveCurrentDate = currentDate.format(calFordDate.getTime());


                            if (Liking)
                            {

                                if(dataSnapshot.exists())
                                {
                                    Liking = false;
                                    UsersRef.child("likes").child(adkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            likeRef.child(adkey).child("likes").child(userId).removeValue();
                                            Likebtn.setColorFilter(getResources().getColor(R.color.grey));
                                                 }
                                    });

                                }
                                else
                                {
                                    Liking = false;
                                    likeRef.child(adkey).child("likes").child(userId).child("time").setValue(saveCurrentDate)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    HashMap map = new HashMap();
                                                    map.put("citykey",citykey);
                                                    map.put("adkey",adkey);
                                                    map.put("image1",image1St);
                                                    map.put("rooms",rooms.getText().toString());
                                                    map.put("beds",beds.getText().toString());
                                                    map.put("parking",String.valueOf(ParkingBol.booleanValue()));
                                                    map.put("toilet",toilet.getText().toString());
                                                    map.put("wifi",String.valueOf(WifiBol.booleanValue()));
                                                    map.put("price",price.getText().toString());
                                                    map.put("time",saveCurrentDate);

                                                    UsersRef.child("likes").child(adkey).updateChildren(map);
                                                    Likebtn.setColorFilter(getResources().getColor(R.color.red));

                                                }
                                            });

                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                else
                {
                    final AlertDialog alertadd = new AlertDialog.Builder(RoomDetailes.this).create();
                    final LayoutInflater factory = LayoutInflater.from(RoomDetailes.this);
                    View dialogView = factory.inflate(R.layout.sample, null);
                    TextView alerttextView = dialogView.findViewById(R.id.dialog_textView);
                    alerttextView.setText(getString(R.string.noaccount));
                    Button cancel =dialogView.findViewById(R.id.cancelBtn);
                    Button register =dialogView.findViewById(R.id.regestrationBtn);
                    register.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(RoomDetailes.this,LoginActivity.class);
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
                    alertadd.show();
                }

            }


        });






        zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomDetailes.this,Full_Screen_ImageSliderActivity.class);
                startActivity(intent);
            }
        });


        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(RoomDetailes.this,Full_Screen_ImageSliderActivity.class);
                startActivity(intent);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position)
            {
                if (current_position < imageUrls.length )
                {

                    prepareDotes(current_position);
                    current_position++;


                }

                else if (current_position >= imageUrls.length )
                {
                    current_position=0;
                    prepareDotes(0);
                    current_position++;
                }

            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });





    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent = new Intent(RoomDetailes.this, CityActivity.class);
        intent.putExtra("Stpostion", Stpostion);
        startActivity(intent);
        finish();

        return super.onOptionsItemSelected(item);
    }



    private void creatSlideShow()
    {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run()
            {


                if (current_position < imageUrls.length )
                {
                    viewPager.setCurrentItem(current_position,true);
                    }

               else if (current_position >= imageUrls.length )
                {
                    viewPager.setCurrentItem(0,true);
                }


            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        },1300,2500);
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

    public void setLikeButtonStatus(final String Postkey) {
        if (!TextUtils.isEmpty(userId)) {

            UsersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            UsersRef.child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.child(Postkey).exists()) {
                            //   Likebtn.setImageResource(R.drawable.like);
                            Likebtn.setColorFilter(getResources().getColor(R.color.red));
                        } else {
                            // Likebtn.setImageResource(R.drawable.dislike);
                            Likebtn.setColorFilter(getResources().getColor(R.color.grey));
                        }
                    } else {
                        //   Likebtn.setImageResource(R.drawable.dislike);
                        Likebtn.setColorFilter(getResources().getColor(R.color.grey));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }


    private void prepareDotesRevrse(int currentSlidePosition)
    {
        if(dotsLayout.getChildCount()>0)
            dotsLayout.removeAllViews();


        ImageView dots[] = new ImageView[imageUrls.length];

        currentSlidePosition--;
        for (int i = 0 ; i<imageUrls.length ; i++)
        {

            dots[i] = new ImageView(this);
            if(i==currentSlidePosition-1)
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.active_dot));
            else
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.inactive_dot));
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(4,0,4,0);
            dotsLayout.addView(dots[i],layoutParams);

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        setLikeButtonStatus(adkey);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(RoomDetailes.this, CityActivity.class);
        intent.putExtra("Stpostion", Stpostion);
        startActivity(intent);
        finish();

    }
}