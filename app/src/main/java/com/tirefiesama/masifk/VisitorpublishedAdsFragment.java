package com.tirefiesama.masifk;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;


public class VisitorpublishedAdsFragment  extends Fragment {

    private FirebaseAuth mAuth;

    private Query mQuery;
    private DatabaseReference likeRef;
    private RecyclerView List;
    private FirebaseRecyclerAdapter<Ads_data, VisitorpublishedAdsFragment.EntryViewHolder> firebaseRecyclerAdapter;
    private View itemsview;
    private String userId;
    FirebaseUser mFirebaseUser;

    TextView emptyTxt;
    private Toolbar mtoolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        itemsview= inflater.inflate(R.layout.fragment_visitorpublished_ads, container, false);


        mtoolbar = itemsview.findViewById(R.id.toolbar_tool);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mtoolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.myad));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        emptyTxt= itemsview.findViewById(R.id.emptylisttextView);
        if(mFirebaseUser != null)
        {
            userId = mFirebaseUser.getUid();
            likeRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("published");


            likeRef.keepSynced(true);

            List = itemsview.findViewById(R.id.AdsRecycler);
            List.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setReverseLayout(false);
            linearLayoutManager.setStackFromEnd(false);
            List.setLayoutManager(linearLayoutManager);

            likeRef.addValueEventListener(new ValueEventListener() {
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

        return itemsview;
    }



    @Override
    public void onStart() {
        super.onStart();



        if (mFirebaseUser != null) {
            likeRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("published");
            likeRef.keepSynced(true);


            mQuery = likeRef.orderByChild("adnumber");

            final FirebaseRecyclerOptions<Ads_data> options = new FirebaseRecyclerOptions.Builder<Ads_data>()
                    .setQuery(mQuery, Ads_data.class).build();


            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Ads_data, VisitorpublishedAdsFragment.EntryViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final VisitorpublishedAdsFragment.EntryViewHolder entryViewHolder, final int i, @NonNull final Ads_data data) {
                    String adkey = getRef(i).getKey();


                    entryViewHolder.setPrice(data.getPrice());
                    entryViewHolder.setRooms(data.getRooms());
                    entryViewHolder.setBeds(data.getBeds());

                    entryViewHolder.setImage(data.getImage1());
                    entryViewHolder.setStatus(data.getStatus());
                    entryViewHolder.setWifi(data.getWifi());
                    entryViewHolder.setToilet(data.getToilet());




                    entryViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String cityname = data.getCity();

                            DatabaseReference citykeyRef = FirebaseDatabase.getInstance().getReference()
                                    .child("cities");
                            citykeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    if(snapshot.exists())
                                    {
                                        for(DataSnapshot citykeySnap : snapshot.getChildren())
                                        {
                                            String Stcitieskey = citykeySnap.getKey();
                                            String Stname = snapshot.child(Stcitieskey).child("name").getValue().toString();
                                            if(Stname.equals(cityname))
                                            {
                                               String citykey=Stcitieskey;
                                                SharedPreferences.Editor editor =
                                                        getContext().getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                                                editor.putString("adkey", adkey);
                                                editor.putString("citykey",citykey );
                                                editor.putString("datakey","published");
                                                editor.apply();


                                                final AlertDialog alertadd = new AlertDialog.Builder(getContext()).create();
                                                final LayoutInflater factory = LayoutInflater.from(getContext());
                                                View dialogView = factory.inflate(R.layout.sample, null);
                                                TextView alerttextView = dialogView.findViewById(R.id.dialog_textView);
                                                alerttextView.setText(getString(R.string.chooseone));
                                                Button edit =dialogView.findViewById(R.id.regestrationBtn);
                                                Button display =dialogView.findViewById(R.id.cancelBtn);
                                                edit.setText(R.string.editAd);
                                                display.setText(R.string.displayAd);

                                                edit.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(getContext(), MyPublishedAdDetailsEdit.class);
                                                        startActivity(intent);
                                                    }
                                                });

                                                display.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(getContext(), MyAdpublishedDetailesDisplay.class);
                                                        startActivity(intent);
                                                    }
                                                });




                                                alertadd.setView(dialogView);
                                                alertadd.show();


                                            }
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });


                }

                @NonNull
                @Override
                public VisitorpublishedAdsFragment.EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_ad_layout, parent, false);

                    return new VisitorpublishedAdsFragment.EntryViewHolder(view);

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

        TextView e_price,e_rooms,e_status,e_toilet,e_beds;
        ImageView e_image,Likebtn,e_parking,e_wifi;

        public EntryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            Likebtn= mView.findViewById(R.id.likeimageview);
        }





        public void setPrice(String price){
            e_price = (TextView) mView.findViewById(R.id.priceText);
            e_price.setText(price+" "+getString(R.string.pound));}



        public void setRooms(String rooms){
            e_rooms = (TextView) mView.findViewById(R.id.my_ad_roomsText);
            e_rooms.setText(rooms+" ");}

        public void setBeds(String beds){
            e_beds = (TextView) mView.findViewById(R.id.bedsTextView7);
            e_beds .setText(beds+" ");}

        public void setToilet(String toilet){
            e_toilet = (TextView) mView.findViewById(R.id.toilettextView7);
            e_toilet.setText(toilet+" ");}

        public void setStatus(String status){
            e_status = (TextView) mView.findViewById(R.id.statustextView);
            e_status.setText(status);}

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





    }
}