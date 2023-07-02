package com.tirefiesama.masifk;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class VisitorMainFragment extends Fragment {

    private NavigationView navigationView;
    private BottomNavigationView bottomNav;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private TextView headername;

    String UserID;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private CircleImageView NavProfileImage;
    private TextView NavProfileName;



    private Query mQuery;
    private DatabaseReference mDatabase,userRef;
    private RecyclerView List;
    private FirebaseRecyclerAdapter<Ads_data, EntryViewHolder> firebaseRecyclerAdapter;
    private View itemsview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        itemsview= inflater.inflate(R.layout.fragment_visitor_main, container, false);

        FirebaseApp.initializeApp(getContext());
        mAuth=FirebaseAuth.getInstance();
//        UserID = mAuth.getUid().toString();




        drawerLayout = itemsview.findViewById(R.id.drawer_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.Drawer_open, R.string.Drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        navigationView=itemsview.findViewById(R.id.navigation_view);





        mDatabase = FirebaseDatabase.getInstance().getReference().child("cities");
        mDatabase.keepSynced(true);
        List = itemsview.findViewById(R.id.AdsRecycler);
        List.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        List.setLayoutManager(linearLayoutManager);

        return itemsview;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
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

                    String body = getString(R.string.hurry)+"\n"+"  "+  "https://play.google.com/store/apps/details?id="+getActivity().getPackageName();

                    share.putExtra(Intent.EXTRA_TEXT,body);
                    Intent chooser =Intent.createChooser(share,"choose");
                    startActivity(chooser);

                }
                catch (Exception ex)
                {
                    Toast.makeText(getActivity(),ex.toString(),Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.logout:
                try
                {
                    mAuth=FirebaseAuth.getInstance();
                    mAuth.signOut();
                    Intent mainintent = new Intent(getActivity(),MainActivity.class);
                    startActivity(mainintent);

                }
                catch (Exception ex)
                {
                    Toast.makeText(getActivity(),ex.toString(),Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.login:
                try
                {

                    Intent mainintent = new Intent(getActivity(),LoginActivity.class);
                    startActivity(mainintent);

                }
                catch (Exception ex)
                {
                    Toast.makeText(getActivity(),ex.toString(),Toast.LENGTH_SHORT).show();
                }
                break;



            case R.id.search:
                try
                {

                    Intent searchintent = new Intent(getActivity(),SearchActivity.class);

                    startActivity(searchintent);
                }
                catch (Exception ex)
                {
                    Toast.makeText(getActivity(),ex.toString(),Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.rateus:
                try {
                    Uri uri= Uri.parse("market://details?id="+getActivity().getPackageName());
                    Intent googleplay = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(googleplay);
                }
                catch (ActivityNotFoundException e)
                {
                    Uri uri= Uri.parse("market://details?id="+getActivity().getPackageName());
                    Intent googleplay = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(googleplay);
                }
                break;

            case R.id.make_ad:
                try
                {
                    mUser = mAuth.getCurrentUser();
                    if(mUser != null) {

                        Intent intent = new Intent(getActivity(), MakeAd.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getActivity(), R.string.noaccount, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), R.string.loginfirst, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);


                    }

                }
                catch (ActivityNotFoundException e)
                {
                    Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
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
                            Intent intent =new Intent(getActivity(),MainActivity.class);
                            startActivity(intent);
                            break;

                    }
                    if (selectedfragment != null){
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container_home, selectedfragment).commit();
                    }



                    return true;
                }
            };









    @Override
    public void onStart() {
        super.onStart();




        mDatabase = FirebaseDatabase.getInstance().getReference().child("cities");
        mDatabase.keepSynced(true);


        mQuery = mDatabase.orderByChild("adnumber");

        final FirebaseRecyclerOptions<Ads_data> options = new FirebaseRecyclerOptions.Builder<Ads_data>()
                .setQuery(mQuery, Ads_data.class).build();




        firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Ads_data, EntryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final EntryViewHolder entryViewHolder, final int i, @NonNull final Ads_data data)
            {
                String citykey1 =  getRef(i).getKey();

                entryViewHolder.setImage(data.getImage());
                entryViewHolder.setName(data.getName());


                entryViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String citykey =  citykey1;
                        String  cityname =  data.getName().toString();

                        SharedPreferences.Editor editor =getActivity().getSharedPreferences("sharedFile",MODE_PRIVATE).edit();
                        editor.putString("citykey",citykey);
                        editor.putString("cityname",cityname);
                        editor.apply();

                        Intent intent = new Intent(getActivity(), CityActivity.class);
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
}