package com.tirefiesama.masifk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {


    private CircleImageView NavProfileImage;
    private TextView ProfileName,phoneNo,myapartNo,myadNo,log,myPubishedNo;
    private DatabaseReference userRef;
    private CardView MyAdCard,MyApartCard,ChangeProfileCard,MyPublishedAds;
    private View itemsview;
    String userId;
    FirebaseAuth mAuth;

    private Toolbar mtoolbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        itemsview=  inflater.inflate(R.layout.fragment_profile, container, false);
        NavProfileImage = itemsview.findViewById(R.id.head_circle_shap);
        ProfileName = itemsview. findViewById(R.id.username);
        phoneNo = itemsview. findViewById(R.id.phoneNotextVi);
        myadNo = itemsview. findViewById(R.id.myad_textVi);
        myPubishedNo= itemsview. findViewById(R.id.publisedAdsNo);
        myapartNo = itemsview. findViewById(R.id.myapartNotextVi);
        log = itemsview. findViewById(R.id.logtextView);
        MyAdCard = itemsview. findViewById(R.id.myadcard);
        MyApartCard= itemsview. findViewById(R.id.myapart);
        ChangeProfileCard= itemsview. findViewById(R.id.changemyprofile);
        MyPublishedAds= itemsview. findViewById(R.id.mypublishedadcard);


        mtoolbar = itemsview.findViewById(R.id.toolbar_tool);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mtoolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()). getSupportActionBar().setHomeAsUpIndicator(R.drawable.ibaseline_menu_24);

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

                if(mFirebaseUser != null)
                {
                    mAuth.signOut();
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }
            }
        });


        ChangeProfileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

                if(mFirebaseUser != null)
                {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                    editor.putString("prefralfragemrnt", "changeprof");
                    editor.apply();

                    Intent intent = new Intent(getActivity(), ChangeProfileActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.loginfirst), Toast.LENGTH_SHORT).show();
                }
            }
        });


        MyPublishedAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

                if(mFirebaseUser != null) {
                    Fragment selectedfragment = null;
                    selectedfragment = new VisitorpublishedAdsFragment();
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                    editor.putString("prefralfragemrnt", "myPublishedAds");
                    editor.apply();

                    loadfragment(selectedfragment);
                }else {  Toast.makeText(getContext(), getString(R.string.loginfirst), Toast.LENGTH_SHORT).show();
                }
            }
        });
        MyAdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

                if(mFirebaseUser != null) {
                    Fragment selectedfragment = null;
                    selectedfragment = new VisitorAdsFragment();

                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                    editor.putString("prefralfragemrnt", "myad");
                    editor.apply();

                    loadfragment(selectedfragment);
                }else {  Toast.makeText(getContext(), getString(R.string.loginfirst), Toast.LENGTH_SHORT).show();
                }
            }
        });

        MyApartCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

                if(mFirebaseUser != null) {
                    Fragment selectedfragment = null;
                    selectedfragment = new VisitorRentedFragment();

                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
                    editor.putString("prefralfragemrnt", "myrented");
                    editor.apply();

                    loadfragment(selectedfragment);
                }else {  Toast.makeText(getContext(), getString(R.string.loginfirst), Toast.LENGTH_SHORT).show();
                }
            }
        });

        readProfileInNavigation();

        return itemsview;
    }

    private void readProfileInNavigation()
    {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

        if(mFirebaseUser != null)
        {
            userId  = mFirebaseUser.getUid();


            userRef = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(userId);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {

                    if(snapshot.child("rented").exists())
                    {
                        long adsNo = snapshot.child("rented").getChildrenCount();
                        myapartNo.setText(String.valueOf(adsNo));
                    }


                    if(snapshot.child("adsrequests").exists())
                    {
                        long adsNo = snapshot.child("adsrequests").getChildrenCount();
                        myadNo.setText(String.valueOf(adsNo));
                    }


                    if(snapshot.child("published").exists())
                    {
                        long adsNo = snapshot.child("published").getChildrenCount();
                        myPubishedNo.setText(String.valueOf(adsNo));
                    }

                    if(snapshot.child("image").exists())
                    {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(NavProfileImage);
                    }
                    if(snapshot.child("name").exists())
                    {
                        String name = snapshot.child("name").getValue().toString();
                        ProfileName.setText(name);
                    }
                    if(snapshot.child("phone").exists())
                    {
                        String phone = snapshot.child("phone").getValue().toString();
                        phoneNo.setText(phone);

                    }

                    if(!(getActivity() == null))
                    log.setText(getActivity().getString(R.string.logout));

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {
                }
            });
        }
        else
        {
            log.setText(getActivity().getString(R.string.login));
        }

    }

    private void loadfragment(Fragment selectedfragment)
    {
        if (selectedfragment != null)
        {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container_home, selectedfragment).commit();
        }
    }

}