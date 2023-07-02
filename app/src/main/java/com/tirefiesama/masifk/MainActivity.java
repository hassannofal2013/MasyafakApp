package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
  //  private BottomNavigationView bottomNav;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar mtoolbar;
    private TextView headername;
    private Menu menu;

    String UserID;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private CircleImageView NavProfileImage;
    private TextView NavProfileName;
    private Query mQuery;
    private DatabaseReference mDatabase,userRef;
    private RecyclerView List;
    private FirebaseRecyclerAdapter<Ads_data, EntryViewHolder> firebaseRecyclerAdapter;
    Boolean menuSelected=false;
    MeowBottomNavigation bottomNavigation;
    String Activityname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
//        UserID = mAuth.getUid().toString();

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network

        }
        else
        {
            Toast.makeText(MainActivity.this, getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }

        bottomNavigation = findViewById(R.id.navigation_bottom_main);


        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.like));


        mtoolbar = findViewById(R.id.toolbar_tool);
        setSupportActionBar(mtoolbar);


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

        bottomNavigation.show(2,true);

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


        drawerLayout = findViewById(R.id.drawer_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.Drawer_open, R.string.Drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();



        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ibaseline_menu_24);
        navigationView=findViewById(R.id.navigation_view);
        menu = navigationView.getMenu();




        View navview = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = navview.findViewById(R.id.head_circle);
        NavProfileName = navview. findViewById(R.id.headerUserName);
     //   bottomNav  = findViewById(R.id.navigation_bottom_main);
       // bottomNav.setOnNavigationItemSelectedListener(navlistener);

        readProfileInNavigation();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UsermenuSelector(menuItem);
                return false;
            }
        });



        mDatabase = FirebaseDatabase.getInstance().getReference().child("cities");
        mDatabase.keepSynced(true);
        List = findViewById(R.id.AdsRecycler);
        List.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        List.setLayoutManager(linearLayoutManager);


    }

    private void loadfragment(Fragment selectedfragment)
    {
        if (selectedfragment != null)
        {

            getSupportFragmentManager().beginTransaction().replace(R.id.main_container_home, selectedfragment).commit();
        }
    }

    private void readProfileInNavigation()
    {
        String userId;    FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

        if(mFirebaseUser != null)
        {
            userId  = mFirebaseUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.child("image").exists())
                    {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(NavProfileImage);
                    }

                    if(snapshot.child("name").exists())
                    {
                        String name = snapshot.child("name").getValue().toString();
                        NavProfileName.setText(name);
                        menu.findItem(R.id.login).setVisible(false);
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
            menu.findItem(R.id.logout).setVisible(false);
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences preferences = getSharedPreferences("sharedFile",MODE_PRIVATE);
     String   prefralfragemrnt = preferences.getString("prefralfragemrnt","prefralfragemrnt");
      if(prefralfragemrnt.equals("myad") || prefralfragemrnt.equals("myrented")
              || prefralfragemrnt.equals("changeprof")  || prefralfragemrnt.equals("myPublishedAds") )
      {
          Fragment selectedfragment = new ProfileFragment();
           getSupportFragmentManager().beginTransaction().replace(R.id.main_container_home, selectedfragment).commit();

          SharedPreferences.Editor editor = getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
          editor.putString("prefralfragemrnt", "");
          editor.apply();
      }
      else
      {
          if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
              return true;
          }
      }
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
                    Toast.makeText(MainActivity.this,ex.toString(),Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.logout:
                try
                {
                    mAuth=FirebaseAuth.getInstance();
                    mAuth.signOut();
                    LoginManager.getInstance().logOut();
                    Intent mainintent = new Intent(MainActivity.this,MainActivity.class);
                    startActivity(mainintent);
                    finish();
                }
                catch (Exception ex)
                {
                    Toast.makeText(MainActivity.this,ex.toString(),Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.login:
                try
                {

                    Intent mainintent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(mainintent);
                    finish();
                }
                catch (Exception ex)
                {
                    Toast.makeText(MainActivity.this,ex.toString(),Toast.LENGTH_SHORT).show();
                }
                break;



            case R.id.see_page:
                try
                {

                    Intent pageintent = new Intent(MainActivity.this,PageLink.class);

                    startActivity(pageintent);
                }
                catch (Exception ex)
                {
                    Toast.makeText(MainActivity.this,ex.toString(),Toast.LENGTH_SHORT).show();
                }
                break;



            case R.id.search:
                try
                {

                    Intent searchintent = new Intent(MainActivity.this,SearchActivity.class);

                    startActivity(searchintent);
                }
                catch (Exception ex)
                {
                    Toast.makeText(MainActivity.this,ex.toString(),Toast.LENGTH_SHORT).show();
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

                menuSelected=true;

                try
                {
                    mUser = mAuth.getCurrentUser();
                    if(mUser != null)
                    {

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if(snapshot.child("phone").exists() && snapshot.child("email").exists())
                                {
                                    String stphone =snapshot.child("phone").getValue().toString();
                                    if ( ! stphone.equals(""))
                                    {

                                        if (menuSelected) {
                                            menuSelected = false;
                                            Intent intent = new Intent(MainActivity.this, MakeAd.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }else
                                    {
                                        UserID=mUser.getUid().toString();
                                        SharedPreferences.Editor editor = getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                                        editor.putString("phone", "000000000");
                                        editor.putString("userkey", UserID);
                                        editor.apply();
                                        Toast.makeText(MainActivity.this, R.string.completeprofile, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                                        startActivity(intent);
                                    }
                                }
                                else
                                {
                                    UserID=mUser.getUid().toString();
                                    SharedPreferences.Editor editor = getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                                    editor.putString("phone", "000000000");
                                    editor.putString("userkey", UserID);
                                    editor.apply();
                                    Toast.makeText(MainActivity.this, R.string.completeprofile, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    else {


                        Toast.makeText(MainActivity.this, R.string.noaccount, Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, R.string.loginfirst, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);


                    }

                }
                catch (ActivityNotFoundException e)
                {
                    Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
                break;


        }

    }




    @Override
    public void onStart() {
        super.onStart();


        Intent intent = getIntent();
        Activityname = intent.getStringExtra("Activityname");






        mDatabase = FirebaseDatabase.getInstance().getReference().child("cities");
        mDatabase.keepSynced(true);


        mQuery = mDatabase.orderByChild("adnumber");

        final FirebaseRecyclerOptions<Ads_data> options = new FirebaseRecyclerOptions.Builder<Ads_data>()
                .setQuery(mQuery, Ads_data.class).build();




        firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Ads_data, MainActivity.EntryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MainActivity.EntryViewHolder entryViewHolder, final int i, @NonNull final Ads_data data)
            {
                String citykey1 =  getRef(i).getKey();

                entryViewHolder.setImage(data.getImage());
                entryViewHolder.setName(data.getName());


                entryViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String citykey =  citykey1;
                        String  cityname =  data.getName().toString();

                        SharedPreferences.Editor editor =getSharedPreferences("sharedFile",MODE_PRIVATE).edit();
                        editor.putString("citykey",citykey);
                        editor.putString("cityname",cityname);

                        editor.apply();

                        Intent intent = new Intent(MainActivity.this, CityActivity.class);
                        intent.putExtra("Stpostion","0");
                        startActivity(intent);



                    }
                });


            }

            @NonNull
            @Override
            public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cities_layout, parent, false);

                return new EntryViewHolder(view);

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

        TextView e_name;
        ImageView e_image;

        public EntryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){
            e_name = (TextView) mView.findViewById(R.id.citynametextView);
            e_name.setText(name);}


        /*
                public void setTitle(String title){
                    e_title = (TextView) mView.findViewById(R.id.titleText);
                    e_title.setText(title);}


         */
        public void setImage(String image){
            e_image = mView.findViewById(R.id.courseImage);
            Picasso.get().load(image).into(e_image);
        }



    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.ClosingApp))
                .setMessage(getString(R.string.sureExite))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ActivityCompat.finishAffinity(MainActivity.this);
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();


    }



}