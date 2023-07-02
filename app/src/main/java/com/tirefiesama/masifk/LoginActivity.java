package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog loadingBar;

    private Spinner spinner;
    private EditText phone;
    private CardView buttoncard;
    private FirebaseAuth mAuth;
    private String verificationid,userId;
    private DatabaseReference userRef;
    private boolean userexist =false;
    CallbackManager callbackManager;
    AlertDialog alertadd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        FacebookSdk.sdkInitialize(this.getApplicationContext());
        AppEventsLogger.activateApp(getApplication());


        phone = findViewById(R.id.phonetextView);


        buttoncard = findViewById(R.id.buttonwcard);

        loadingBar = new ProgressDialog(this);

        spinner = findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button_face);

        loginButton.setReadPermissions("email");


        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {

                handleFacebookAccesstoken(loginResult.getAccessToken(),loginResult);
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(LoginActivity.this, getString(R.string.logincanceled), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(LoginActivity.this, exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });




        buttoncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];

                String Stphone=phone.getText().toString().trim();
                if (Stphone.startsWith("0")){Stphone=Stphone.substring(1);}
                final String number = code +Stphone ;
                if (number.isEmpty() || number.length() < 9)
                {
                    phone.setError("Valid number is required");
                    phone.requestFocus();
                    return;
                }

                sendVerificationCode(number);
                 alertadd = new AlertDialog.Builder(LoginActivity.this).create();
                final LayoutInflater factory = LayoutInflater.from(LoginActivity.this);
                View dialogView = factory.inflate(R.layout.sample_dialog_otp, null);

                EditText otp = dialogView.findViewById(R.id.otp);


                Button submit =dialogView.findViewById(R.id.submitBtn);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertadd.dismiss();
                        String Stotp = otp.getText().toString();
                        if(! TextUtils.isEmpty(Stotp)){verifyCode(Stotp);}

                    }
                });



                alertadd.setView(dialogView);
                alertadd.show();

            }
        });

    }

    private void handleFacebookAccesstoken(AccessToken accessToken,LoginResult loginResult)
    {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                     if (task.isSuccessful())
                     {

                        String first_name = Profile.getCurrentProfile().getFirstName();
                         String last_name = Profile.getCurrentProfile().getLastName();
                         String name = first_name+" "+last_name;
                         String facebookID= Profile.getCurrentProfile().getId();
                         String image= Profile.getCurrentProfile().getProfilePictureUri(150, 150).toString();

                         CheckAccountexist(name,facebookID,image);
                         Toast.makeText(LoginActivity.this, getString(R.string.loginsucceed), Toast.LENGTH_SHORT).show();
                         Intent intent =new Intent(LoginActivity.this,MainActivity.class);

                         startActivity(intent);
                         finish();
                     }
                     else
                     {
                         Toast.makeText(LoginActivity.this, getString(R.string.loginfailed), Toast.LENGTH_SHORT).show();
                     }
                    }
                });
    }

    private void CheckAccountexist(String name,String facebookID,String image)
    {
        userId= mAuth.getUid().toString();
        userRef= FirebaseDatabase.getInstance().getReference().child("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child(userId).exists())
                {
                    userexist =true;
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    HashMap hashMap = new HashMap();
                    hashMap.put("name",name);
                    hashMap.put("faceID",facebookID);
                    hashMap.put("image",image);
                    userRef.child(userId).updateChildren(hashMap);


                    Toast.makeText(LoginActivity.this, R.string.loginsucceed, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }


    private void sendVerificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                LoginActivity.this,
                mCallBack
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // Toast.makeText(PhoneOTPActivity.this, "sent", Toast.LENGTH_SHORT).show();
            verificationid = s;

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                // progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();

        }
    };




    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
        signInWithCredential(credential);



    }

    private void checkUserExist()
    {
        userId= mAuth.getUid().toString();
        userRef= FirebaseDatabase.getInstance().getReference().child("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child(userId).exists())
                {
                    userexist =true;
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    SharedPreferences.Editor editor =getSharedPreferences("sharedFile",MODE_PRIVATE).edit();
                    editor.putString("userkey",userId);
                    editor.apply();
                    finish();
                }
                else
                {
                    userexist = false;
                    SharedPreferences.Editor editor =getSharedPreferences("sharedFile",MODE_PRIVATE).edit();
                    editor.putString("phone",phone.getText().toString().trim());
                    editor.putString("userkey",userId);
                    editor.apply();


                    Toast.makeText(LoginActivity.this, R.string.noaccount, Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, R.string.creataccount, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, ChangeProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            checkUserExist();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);

    }
}