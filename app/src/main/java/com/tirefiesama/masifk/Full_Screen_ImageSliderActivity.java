package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class Full_Screen_ImageSliderActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private String[] imageUrls ;
    private int current_position=0;
    private String adkey,citykey;
    private DatabaseReference mDatabase;
    private Boolean increase=true,decrease=false;
    private ImageButton exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_slider);


        SharedPreferences preferences = getSharedPreferences("sharedFile",MODE_PRIVATE);
        adkey = preferences.getString("adkey","adkey");
        citykey = preferences.getString("citykey","citykey");
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.dotsContainer);
        exitBtn = findViewById(R.id.exitBtn);




        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                        PagerAdapter adapter = new ViewPagerAdapter(Full_Screen_ImageSliderActivity.this, imageUrls);
                        viewPager.setAdapter(adapter);
                        prepareDotes(current_position++);

                    }
                    else
                    {
                        if ( ! image5St.equals(""))
                        {
                            imageUrls=new String[]{imageScreenshotSt,image1St,image2St,image3St,image4St,image5St};
                            PagerAdapter adapter = new ViewPagerAdapter(Full_Screen_ImageSliderActivity.this, imageUrls);
                            viewPager.setAdapter(adapter);
                            prepareDotes(current_position++);

                        }
                        else
                        {
                            if ( ! image4St.equals(""))
                            {
                                imageUrls=new String[]{imageScreenshotSt,image1St,image2St,image3St,image4St};
                                PagerAdapter adapter = new ViewPagerAdapter(Full_Screen_ImageSliderActivity.this, imageUrls);
                                viewPager.setAdapter(adapter);
                                prepareDotes(current_position++);

                            }
                            else
                            {
                                if ( ! image3St.equals(""))
                                {

                                    imageUrls=new String[]{imageScreenshotSt,image1St,image2St,image3St};
                                    PagerAdapter adapter = new ViewPagerAdapter(Full_Screen_ImageSliderActivity.this, imageUrls);
                                    viewPager.setAdapter(adapter);
                                    prepareDotes(current_position++);

                                }
                                else
                                {
                                    if ( ! image2St.equals(""))
                                    {
                                        imageUrls=new String[]{imageScreenshotSt,image1St,image2St};
                                        PagerAdapter adapter = new ViewPagerAdapter(Full_Screen_ImageSliderActivity.this, imageUrls);
                                        viewPager.setAdapter(adapter);
                                        prepareDotes(current_position++);

                                    }
                                    else
                                    {
                                        if ( ! image1St.equals(""))
                                        {
                                            imageUrls=new String[]{imageScreenshotSt,image1St};
                                            PagerAdapter adapter = new ViewPagerAdapter(Full_Screen_ImageSliderActivity.this, imageUrls);
                                            viewPager.setAdapter(adapter);
                                            prepareDotes(current_position++);

                                        }

                                        else
                                        {

                                            if( ! imageScreenshotSt.equals(""))
                                            {
                                                imageUrls = new String[]{imageScreenshotSt};
                                                PagerAdapter adapter = new ViewPagerAdapter(Full_Screen_ImageSliderActivity.this, imageUrls);
                                                viewPager.setAdapter(adapter);
                                                prepareDotes(current_position++);
                                            }

                                        }

                                    }

                                }
                            }
                        }
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }





        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {

                if(increase)
                {
                    if (current_position < imageUrls.length) { prepareDotes(current_position++); }
                }


                if(decrease)
                {
                    if (current_position <= imageUrls.length) { prepareDotesRevrse(current_position--); }
                }

                if(current_position==imageUrls.length)
                    {   increase=false;   decrease=true; }

                if(current_position==1)
                {   increase=true;    decrease=false; }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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