package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CityActivity extends AppCompatActivity {


    private Toolbar mtoolbar;


    String citykey,cityname,Stpostion;

    LinearLayoutManager linearLayoutManager;

    private Query mQuery;
    private DatabaseReference mDatabase,UsersRef,likeRef;
    private RecyclerView List;
    private FirebaseRecyclerAdapter<Ads_data, CityActivity.EntryViewHolder> firebaseRecyclerAdapter;
    private String userId;
    private FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;

    Boolean LikeChecker =false,Liking=false;

    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        SharedPreferences preferences = getSharedPreferences("sharedFile",MODE_PRIVATE);
        citykey = preferences.getString("citykey","citykey");
        cityname = preferences.getString("cityname","cityname");

        Stpostion = getIntent().getStringExtra("Stpostion");


        bottomNavigation = findViewById(R.id.navigation_bottom_main);

        mtoolbar = findViewById(R.id.toolbar_tool);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(cityname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.like));


        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item)
            {
                Fragment selectedfragment = null;

                switch (item.getId()) {
                    case 1:
                        selectedfragment = new ProfileFragment();
                        mtoolbar.setVisibility(View.GONE);
                        SharedPreferences.Editor editor = getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                        editor.putString("prefralfragemrnt", "");
                        editor.apply();
                        break;

                    case 2:
                        selectedfragment = new VisitorMainFragment();
                        mtoolbar.setVisibility(View.VISIBLE);
                        SharedPreferences.Editor editor1 = getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                        editor1.putString("prefralfragemrnt", "");
                        editor1.apply();
                        break;

                    case 3:
                        selectedfragment = new VisitorWishesFragment();
                        mtoolbar.setVisibility(View.VISIBLE);
                        SharedPreferences.Editor editor2 = getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                        editor2.putString("prefralfragemrnt", "");
                        editor2.apply();
                        break;
                }

                loadfragment(selectedfragment);

            }
        });

       // bottomNavigation.show(2,true);

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {

            }
        });
        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });


        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser != null) {
            userId = mFirebaseUser.getUid();}

     //   drawerLayout = findViewById(R.id.drawer_view);
    //    actionBarDrawerToggle = new ActionBarDrawerToggle(CityActivity.this, drawerLayout, R.string.Drawer_open, R.string.Drawer_close);
      //  drawerLayout.addDrawerListener(actionBarDrawerToggle);
       // actionBarDrawerToggle.syncState();



       // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ibaseline_menu_24);
        //  getSupportActionBar().setIcon(R.drawable.ibaseline_menu_24);


    //    bottomNav  = findViewById(R.id.navigation_bottom_main);
      //  bottomNav.setOnNavigationItemSelectedListener(navlistener);

        /*
        navigationView=findViewById(R.id.navigation_view);
        menu = navigationView.getMenu();
        View navview = navigationView.inflateHeaderView(R.layout.navigation_header);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UsermenuSelector(menuItem);
                return false;
            }
        });

         */


        mDatabase = FirebaseDatabase.getInstance().getReference().child("cities").child(citykey).child("ads");
        likeRef  = FirebaseDatabase.getInstance().getReference().child("likes").child(citykey).child("ads");
        mDatabase.keepSynced(true);
        List = findViewById(R.id.AdsRecycler);
        List.setHasFixedSize(true);




    }

    private void loadfragment(Fragment selectedfragment)
    {
        if (selectedfragment != null)
        {

            getSupportFragmentManager().beginTransaction().replace(R.id.main_container_home, selectedfragment).commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent = new Intent(CityActivity.this,MainActivity.class);
        startActivity(intent);
        finish();

        return super.onOptionsItemSelected(item);
    }


    private void UsermenuSelector(MenuItem menuItem) {

        switch (menuItem.getItemId()) {


            case R.id.share:
                try
                {

                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_SUBJECT,getResources().getString(R.string.app_name));

                    String body = getString(R.string.hurry)+"\n"+"  "+  "https://play.google.com/store/apps/details?id="+getPackageName();

                    share.putExtra(Intent.EXTRA_TEXT,body);
                    Intent chooser =Intent.createChooser(share,"choose");
                    startActivity(chooser);

                }
                catch (Exception ex)
                {
                    Toast.makeText(CityActivity.this,ex.toString(),Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.logout:
                try
                {
                    mAuth= FirebaseAuth.getInstance();
                    mAuth.signOut();
                    Intent mainintent = new Intent(CityActivity.this,MainActivity.class);
                    startActivity(mainintent);
                    finish();
                }
                catch (Exception ex)
                {
                    Toast.makeText(CityActivity.this,ex.toString(),Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.search:
                try
                {

                    Intent addCourseintent = new Intent(CityActivity.this,SearchActivity.class);
                    startActivity(addCourseintent);
                }
                catch (Exception ex)
                {
                    Toast.makeText(CityActivity.this,ex.toString(),Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.rateus:
                try {
                    Uri uri= Uri.parse("market://details?id="+getPackageName());
                    Intent googleplay = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(googleplay);
                }
                catch (ActivityNotFoundException e)
                {
                    Uri uri= Uri.parse("market://details?id="+getPackageName());
                    Intent googleplay = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(googleplay);
                }
                break;

            case R.id.make_ad:
                try
                {
                    Intent showRequests = new Intent(CityActivity.this,MakeAd.class);
                    startActivity(showRequests);

                }
                catch (ActivityNotFoundException e)
                {
                    Toast.makeText(CityActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
                break;


        }

    }





    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedfragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_my_ad:
                            selectedfragment = new VisitorAdsFragment();
                            break;

                        case R.id.nav_wishlist:
                            selectedfragment = new VisitorWishesFragment();
                            break;

                        case R.id.nav_my_apartment:
                            selectedfragment = new VisitorRentedFragment();
                            break;
                        case R.id.nav_home:
                            Intent intent =new Intent(CityActivity.this,MainActivity.class);
                            startActivity(intent);
                            break;

                    }
                    if (selectedfragment != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container_home, selectedfragment).commit();
                    }



                    return true;
                }
            };

    @Override
    public void onStart() {
        super.onStart();



        likeRef  = FirebaseDatabase.getInstance().getReference().child("likes").child(citykey).child("ads");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("cities").child(citykey).child("ads");
        mDatabase.keepSynced(true);

         linearLayoutManager = new LinearLayoutManager(CityActivity.this);
        SharedPreferences preferences = getSharedPreferences("sharedFile",MODE_PRIVATE);


       // linearLayoutManager.setReverseLayout(false);
       // linearLayoutManager.setStackFromEnd(false);
      //  linearLayoutManager.scrollToPosition(3);


      //  Stpostion = getIntent().getStringExtra("Stpostion");
        List.setLayoutManager(linearLayoutManager);
       if( ! TextUtils.isEmpty(Stpostion))
       {List.smoothScrollToPosition(Integer.parseInt(Stpostion));}


        mQuery = mDatabase.orderByChild("adnumber");

        final FirebaseRecyclerOptions<Ads_data> options = new FirebaseRecyclerOptions.Builder<Ads_data>()
                .setQuery(mQuery, Ads_data.class).build();




        firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Ads_data, EntryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CityActivity.EntryViewHolder entryViewHolder, final int i, @NonNull final Ads_data data)
            {
                String adkey =  getRef(i).getKey();





                entryViewHolder.setPrice(data.getPrice());
                entryViewHolder.setRooms(data.getRooms());
                entryViewHolder.setImage(data.getImage1());
                entryViewHolder.setBeds(data.getBeds());

                entryViewHolder.setWifi(data.getWifi());
                entryViewHolder.setToilet(data.getToilet());
                entryViewHolder.setParking(data.getParking());

                if(mFirebaseUser != null) {  entryViewHolder.setLikeButtonStatus(adkey); }

                entryViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        SharedPreferences.Editor editor =getSharedPreferences("sharedFile",MODE_PRIVATE).edit();
                        editor.putString("adkey",adkey);
                        editor.putString("citykey",citykey);
                        editor.putString("position",String.valueOf(i));

                        editor.apply();

                       DatabaseReference ViewRef = mDatabase.child(adkey).child("views");
                       ViewRef.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot)
                           {
                               if(snapshot.exists())
                               {
                                   int oldint = Integer.parseInt(snapshot.getValue().toString());
                                   ViewRef.setValue(String.valueOf(oldint+1));
                               }
                               else
                                   {
                                       ViewRef.setValue("1");
                                   }

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });

                        Intent intent = new Intent(CityActivity.this, RoomDetailes.class);
                        intent.putExtra("Stpostion", String.valueOf(i));
                        startActivity(intent);
                        finish();
                    }
                });




                entryViewHolder.Likebtn.setOnClickListener(new View.OnClickListener() {
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
                                                    entryViewHolder.Likebtn.setImageResource(R.drawable.dislike);
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
                                                            map.put("image1",data.getImage1());
                                                            map.put("rooms",data.getRooms());
                                                            map.put("beds",data.getBeds());
                                                            map.put("parking",data.getParking());
                                                            map.put("toilet",data.getToilet());
                                                            map.put("wifi",data.getWifi());
                                                            map.put("price",data.getPrice());
                                                            map.put("time",saveCurrentDate);

                                                            UsersRef.child("likes").child(adkey).updateChildren(map);
                                                            entryViewHolder.Likebtn.setImageResource(R.drawable.like);

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
                            final AlertDialog alertadd = new AlertDialog.Builder(CityActivity.this).create();
                            final LayoutInflater factory = LayoutInflater.from(CityActivity.this);
                            View dialogView = factory.inflate(R.layout.sample, null);
                            TextView alerttextView = dialogView.findViewById(R.id.dialog_textView);
                            alerttextView.setText(getString(R.string.noaccount));
                            Button cancel =dialogView.findViewById(R.id.cancelBtn);
                            Button register =dialogView.findViewById(R.id.regestrationBtn);
                            register.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(CityActivity.this,LoginActivity.class);
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




            }

            @NonNull
            @Override
            public CityActivity.EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_layout, parent, false);

                return new CityActivity.EntryViewHolder(view);

            }
        };

        List.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();






    }




    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }


    public  class EntryViewHolder extends RecyclerView.ViewHolder {
        View mView;
        //   TextView e_title,e_likes;
        TextView e_price,e_rooms,e_beds,e_toilet;
        ImageView e_image,Likebtn,e_parking,e_wifi;

        public EntryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            Likebtn= mView.findViewById(R.id.likeimageview);
        }



        public void setLikeButtonStatus(final String Postkey)
        {
            UsersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            UsersRef.child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        if (dataSnapshot.child(Postkey).exists())
                        {
                            Likebtn.setImageResource(R.drawable.like);
                        }
                        else
                        {
                            Likebtn.setImageResource(R.drawable.dislike);
                        }
                    }
                    else
                    {
                        Likebtn.setImageResource(R.drawable.dislike);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void setPrice(String price){
            e_price = (TextView) mView.findViewById(R.id.priceText);
            e_price.setText(price+" "+getString(R.string.pound));}



        public void setRooms(String rooms){
            e_rooms = (TextView) mView.findViewById(R.id.my_ad_roomsText);
            e_rooms.setText(rooms+" ");}

        public void setBeds(String beds){
            e_beds = (TextView) mView.findViewById(R.id.bedsTextView7);
            e_beds.setText(beds+" ");
        }
        public void setToilet(String toilet){
            e_toilet = (TextView) mView.findViewById(R.id.toilettextView7);
            e_toilet.setText(toilet+" ");}


        public void setImage(String image){
            e_image = mView.findViewById(R.id.roomImage);
            Picasso.get().load(image).into(e_image);
        }

        public void setLikebtn(String image){
            Likebtn = mView.findViewById(R.id.likeimageview);

        }
        public void setWifi(String wifi){
            e_wifi = mView.findViewById(R.id.wifiIcon);
            if( !TextUtils.isEmpty(wifi)) {
                if (wifi.equals("true")) {
                    e_wifi.setVisibility(View.VISIBLE);
                }
            }
        }


        public void setParking(String parking){
            e_parking = mView.findViewById(R.id.parkingIcon);

            if( !TextUtils.isEmpty(parking)) {
                if (parking.equals("true")) {
                    e_parking.setVisibility(View.VISIBLE);
                }
            }
        }



    }




    @Override
    public void onBackPressed() {

        Intent intent = new Intent(CityActivity.this,MainActivity.class);
        startActivity(intent);
        finish();

        super.onBackPressed();
    }
}