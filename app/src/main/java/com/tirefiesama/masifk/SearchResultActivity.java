package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class SearchResultActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Query mQuery;
    private DatabaseReference mDatabase,UsersRef;
    private RecyclerView List;
    private FirebaseRecyclerAdapter<Ads_data, SearchResultActivity.EntryViewHolder> firebaseRecyclerAdapter;
    private View itemsview;
    private String userId,citykey;
    FirebaseUser mFirebaseUser;
    Boolean LikeChecker =false,Liking=false;
    TextView emptyTxt;
    CardView returntoSearch;

    String Stcitykey,Stadkey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        returntoSearch= findViewById(R.id.returncardview);
        returntoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResultActivity.this,SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
        emptyTxt= findViewById(R.id.emptylisttextView35);
        if(mFirebaseUser != null)
        {
            userId = mFirebaseUser.getUid();

            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                    .child("search");

            mDatabase.keepSynced(true);

            List = findViewById(R.id.AdsRecycler);
            List.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setReverseLayout(false);
            linearLayoutManager.setStackFromEnd(false);
            List.setLayoutManager(linearLayoutManager);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(!snapshot.exists())
                    {
                        emptyTxt.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else { emptyTxt.setVisibility(View.VISIBLE); }


    }

    @Override
    public void onStart() {
        super.onStart();



        if (mFirebaseUser != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                    .child("search");
            mDatabase.keepSynced(true);


            mQuery = mDatabase.orderByChild("price");

            final FirebaseRecyclerOptions<Ads_data> options = new FirebaseRecyclerOptions.Builder<Ads_data>()
                    .setQuery(mQuery, Ads_data.class).build();


            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Ads_data, SearchResultActivity.EntryViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final SearchResultActivity.EntryViewHolder entryViewHolder, final int i, @NonNull final Ads_data data) {
                    String adkey = getRef(i).getKey();


                    entryViewHolder.setPrice(data.getPrice());
                    entryViewHolder.setRooms(data.getRooms());
                    entryViewHolder.setImage(data.getImage1());

                    entryViewHolder.setBeds(data.getBeds());
                    entryViewHolder.setWifi(data.getWifi());
                    entryViewHolder.setToilet(data.getToilet());

                    Stadkey=data.getAdkey();
                    Stcitykey=data.getCity();


                    if(mFirebaseUser != null) {  entryViewHolder.setLikeButtonStatus(adkey); }

                    entryViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Stadkey=data.getAdkey();
                            Stcitykey=data.getCity();

                            SharedPreferences.Editor editor =getSharedPreferences("sharedFile",MODE_PRIVATE).edit();
                            editor.putString("adkey",Stadkey);
                            editor.putString("citykey",Stcitykey);

                            editor.apply();

                            //Toast.makeText(SearchResultActivity.this, Stadkey, Toast.LENGTH_SHORT).show();
                          //  Toast.makeText(SearchResultActivity.this, Stcitykey, Toast.LENGTH_SHORT).show();

                               Intent intent = new Intent(SearchResultActivity.this, RoomDetailes.class);
                            startActivity(intent);

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
                                                        mDatabase.child(adkey).child("likes").child(userId).removeValue();
                                                    }
                                                });

                                            }
                                            else
                                            {
                                                Liking = false;
                                                mDatabase.child(adkey).child("likes").child(userId).child("time").setValue(saveCurrentDate)
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
                                final AlertDialog alertadd = new AlertDialog.Builder(SearchResultActivity.this).create();
                                final LayoutInflater factory = LayoutInflater.from(SearchResultActivity.this);
                                View dialogView = factory.inflate(R.layout.sample, null);
                                TextView alerttextView = dialogView.findViewById(R.id.dialog_textView);
                                alerttextView.setText(getString(R.string.noaccount));
                                Button cancel =dialogView.findViewById(R.id.cancelBtn);
                                Button register =dialogView.findViewById(R.id.regestrationBtn);
                                register.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(SearchResultActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();
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
                public SearchResultActivity.EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_layout, parent, false);

                    return new SearchResultActivity.EntryViewHolder(view);

                }
            };

            List.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();


        }else  { emptyTxt.setVisibility(View.VISIBLE); }

    }



    @Override
    public void onStop() {
        super.onStop();
        if (mFirebaseUser != null) { firebaseRecyclerAdapter.stopListening();}
    }


    public  class EntryViewHolder extends RecyclerView.ViewHolder {
        View mView;

        TextView e_price,e_rooms,e_toilet,e_beds;
        ImageView e_image,Likebtn,e_parking,e_wifi;

        public EntryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            Likebtn= mView.findViewById(R.id.likeimageview);
        }

        public void setLikeButtonStatus(final String Postkey)
        {
            UsersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
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
            e_rooms.setText(rooms+" ");}



        public void setImage(String image){
            e_image = mView.findViewById(R.id.roomImage);
            Picasso.get().load(image).into(e_image);
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

        public void setBeds(String beds){
            e_beds = (TextView) mView.findViewById(R.id.bedsTextView7);
            e_beds .setText(beds+" ");}

        public void setToilet(String toilet){
            e_toilet = (TextView) mView.findViewById(R.id.toilettextView7);
            e_toilet.setText(toilet+" ");}


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SearchResultActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}