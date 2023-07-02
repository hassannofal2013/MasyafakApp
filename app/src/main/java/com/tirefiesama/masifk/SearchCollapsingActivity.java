package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchCollapsingActivity extends AppCompatActivity {

    private Spinner spinner;
    private ArrayList<String> arrayList = new ArrayList<>();
    Calendar calendar;
    TextView startDay,endDay;
    String date;
    private ImageButton minusBtn,plusBtn;
    private EditText NoRooms;
    private Toolbar mtoolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Menu menu;
    ProgressBar progressBar;
    boolean progressing= false;
    DatabaseReference searchRef;
    private FirebaseAuth mAuth;
    String UserID,Ststartdate,Stenddate,Ststartmonth,Stendmonth,Ststartyear,Stendyear,citykey;
    FirebaseUser mFirebaseUser;
    private CircleImageView NavProfileImage;
    private TextView NavProfileName;

    private Query mQuery;
    private DatabaseReference mDatabase,UsersRef;
    private RecyclerView List;
    private FirebaseRecyclerAdapter<Ads_data, EntryViewHolder> firebaseRecyclerAdapter;
    Boolean LikeChecker =false,Liking=false;


    LinearLayout toolbarLay,searchLay;
    FrameLayout recyclerLay;
    CardView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_collapsing);


        toolbarLay= findViewById(R.id.toolbarLayout);
        searchLay= findViewById(R.id.searchLayout);
        recyclerLay= findViewById(R.id.main_container_home);
        search = findViewById(R.id.searchTxtCard);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLay.setVisibility(View.INVISIBLE);
                toolbarLay.setVisibility(View.INVISIBLE);
                recyclerLay.setVisibility(View.VISIBLE);
            }
        });



        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            UserID = mAuth.getUid();}
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(UserID)
                .child("search");
        mDatabase.keepSynced(true);
        List = findViewById(R.id.AdsRecycler);
        List.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchCollapsingActivity.this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        List.setLayoutManager(linearLayoutManager);

    }

    @Override
    public void onStart() {
        super.onStart();



        mDatabase =  FirebaseDatabase.getInstance().getReference().child("users").child(UserID)
                .child("search");
                mDatabase.keepSynced(true);


        mQuery = mDatabase.orderByChild("adnumber");

        final FirebaseRecyclerOptions<Ads_data> options = new FirebaseRecyclerOptions.Builder<Ads_data>()
                .setQuery(mQuery, Ads_data.class).build();



        firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Ads_data, EntryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final EntryViewHolder entryViewHolder, final int i, @NonNull final Ads_data data)
            {
                String adkey =  getRef(i).getKey();



                entryViewHolder.setPrice(data.getPrice());
                entryViewHolder.setRooms(data.getRooms());
                entryViewHolder.setImage(data.getImage1());
                entryViewHolder.setBeds(data.getBeds());

                if(mFirebaseUser != null) {  entryViewHolder.setLikeButtonStatus(adkey); }

                entryViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        SharedPreferences.Editor editor =getSharedPreferences("sharedFile",MODE_PRIVATE).edit();
                        editor.putString("adkey",adkey);
                        editor.apply();

                        Intent intent = new Intent(SearchCollapsingActivity.this, RoomDetailes.class);
                        startActivity(intent);
                    }
                });




                entryViewHolder.Likebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mFirebaseUser != null)
                        {
                            UserID = mFirebaseUser.getUid();
                            LikeChecker = true;
                            Liking = true;
                            UsersRef = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
                            UsersRef.child("likes").child(adkey).addValueEventListener(new ValueEventListener() {
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
                                                    mDatabase.child(adkey).child("likes").child(UserID).removeValue();
                                                }
                                            });

                                        }
                                        else
                                        {
                                            Liking = false;
                                            mDatabase.child(adkey).child("likes").child(UserID).child("time").setValue(saveCurrentDate)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            HashMap map = new HashMap();
                                                            map.put("citykey",citykey);
                                                            map.put("adkey",adkey);
                                                            map.put("image1",data.getImage1());
                                                            map.put("rooms",data.getRooms());
                                                            map.put("price",data.getPrice());
                                                            map.put("time",saveCurrentDate);
                                                            UsersRef.child("likes").child(adkey).updateChildren(map);

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
                            final AlertDialog alertadd = new AlertDialog.Builder(SearchCollapsingActivity.this).create();
                            final LayoutInflater factory = LayoutInflater.from(SearchCollapsingActivity.this);
                            View dialogView = factory.inflate(R.layout.sample, null);
                            TextView alerttextView = dialogView.findViewById(R.id.dialog_textView);
                            alerttextView.setText(getString(R.string.noaccount));
                            Button cancel =dialogView.findViewById(R.id.cancelBtn);
                            Button register =dialogView.findViewById(R.id.regestrationBtn);
                            register.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(SearchCollapsingActivity.this,LoginActivity.class);
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
            public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_layout, parent, false);

                return new  EntryViewHolder(view);

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
        TextView e_price,e_rooms,e_beds;
        ImageView e_image,Likebtn;

        public EntryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            Likebtn= mView.findViewById(R.id.likeimageview);
        }



        public void setLikeButtonStatus(final String Postkey)
        {
            UsersRef = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
            UsersRef.child("likes").addValueEventListener(new ValueEventListener() {
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
            e_rooms.setText(rooms+" "+getString(R.string.rooms));}

        public void setBeds(String beds){
           // e_beds = (TextView) mView.findViewById(R.id.bedsText);
            e_beds.setText(beds+" "+getString(R.string.beds));}



        public void setImage(String image){
            e_image = mView.findViewById(R.id.roomImage);
            Picasso.get().load(image).into(e_image);
        }

        public void setLikebtn(String image){
            Likebtn = mView.findViewById(R.id.likeimageview);

        }



    }


}