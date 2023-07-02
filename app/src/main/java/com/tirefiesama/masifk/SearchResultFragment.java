package com.tirefiesama.masifk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class SearchResultFragment extends Fragment {


    private FirebaseAuth mAuth;

    private Query mQuery;
    private DatabaseReference  mDatabase,UsersRef;
    private RecyclerView List;
    private FirebaseRecyclerAdapter<Ads_data, SearchResultFragment.EntryViewHolder> firebaseRecyclerAdapter;
    private View itemsview;
    private String userId,citykey;
    FirebaseUser mFirebaseUser;
    Boolean LikeChecker =false,Liking=false;
    TextView emptyTxt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        itemsview= inflater.inflate(R.layout.fragment_search_result, container, false);



        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        emptyTxt= itemsview.findViewById(R.id.emptylisttextView35);
        if(mFirebaseUser != null)
        {
            userId = mFirebaseUser.getUid();

            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                    .child("search");

            mDatabase.keepSynced(true);

            List = itemsview.findViewById(R.id.AdsRecycler);
            List.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
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

        return itemsview;
    }

    @Override
    public void onStart() {
        super.onStart();



        if (mFirebaseUser != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                    .child("search");
            mDatabase.keepSynced(true);


            mQuery = mDatabase.orderByChild("adnumber");

            final FirebaseRecyclerOptions<Ads_data> options = new FirebaseRecyclerOptions.Builder<Ads_data>()
                    .setQuery(mQuery, Ads_data.class).build();


            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Ads_data, SearchResultFragment.EntryViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final SearchResultFragment.EntryViewHolder entryViewHolder, final int i, @NonNull final Ads_data data) {
                    String adkey = getRef(i).getKey();


                    entryViewHolder.setPrice(data.getPrice());
                    entryViewHolder.setRooms(data.getRooms());
                    entryViewHolder.setImage(data.getImage1());

                    entryViewHolder.setBeds(data.getBeds());
                    entryViewHolder.setWifi(data.getWifi());
                    entryViewHolder.setToilet(data.getToilet());


                    entryViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            SharedPreferences.Editor editor = getContext().getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                            editor.putString("adkey", adkey);
                            editor.apply();

                            Intent intent = new Intent(getContext(), MyAdRequestsDetailsEdit.class);
                            startActivity(intent);
                        }
                    });


                }

                @NonNull
                @Override
                public SearchResultFragment.EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_layout, parent, false);

                    return new SearchResultFragment.EntryViewHolder(view);

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
}