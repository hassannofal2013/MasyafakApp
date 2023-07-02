package com.tirefiesama.masifk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    Animation topToDown,downToTop;

    ImageView logoHead,logoword;
    String Activityname = "";
    String roomkey = "";
    String citykey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);



      //  Intent intentBackgroundService = new Intent(this, FirebasePushNotificationClass.class);
      //  startService(intentBackgroundService);

        logoHead = findViewById(R.id.logo_head);
        logoword = findViewById(R.id.imageView17);


        topToDown = AnimationUtils.loadAnimation(this,R.anim.top_down);
        downToTop = AnimationUtils.loadAnimation(this,R.anim.down_top);


        logoHead.setAnimation(topToDown);
        logoword.setAnimation(downToTop);



        Runnable animation = new Runnable() {
            @Override
            public void run() {
                MediaPlayer mediaPlayer = MediaPlayer.create(SplashActivity.this,R.raw.completion);
                mediaPlayer.start();
            }
        };

        Intent startingIntent = getIntent();
        if (startingIntent != null)
        {
            Activityname = startingIntent.getStringExtra("click_action");
            roomkey = startingIntent.getStringExtra("roomkey");
            citykey = startingIntent.getStringExtra("citykey");
        }
        if(!TextUtils.isEmpty(Activityname))
        {
            SharedPreferences.Editor editor =getSharedPreferences("sharedFile",MODE_PRIVATE).edit();
            editor.putString("roomkey",roomkey);
            editor.putString("citykey",citykey);
            editor.apply();
        }

        Runnable gotomainactivity = new Runnable() {
            @Override
            public void run() {
                if(  TextUtils.isEmpty(Activityname))
                {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                    {
                        if(Activityname.equals("RoomDetailes"))
                        {
                            Intent intent = new Intent(SplashActivity.this,FCM_RoomDetails.class);
                            startActivity(intent);
                        }
                    }
            }
        };



        Handler handler = new Handler();

        handler.postDelayed(animation,400);

        handler.postDelayed(gotomainactivity ,2500);


    }

   



}