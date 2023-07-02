package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;


public class RegisterActivity extends AppCompatActivity {

    private CardView imagecard,updatebuttoncard;
    private CircleImageView circleImageView;
    private EditText name,email,phone;
    private DatabaseReference UserRef;
    private String imageUri,verificationid;
    public FirebaseAuth mAuth;
    final static int Gallary_pic = 1;
    HashMap userMap = new HashMap();
    private StorageReference UserProfileImageRef;
    FirebaseUser mFirebaseUser;
    private Boolean uploadImage=false,selectImage=false,previousimage=false,phoneMatched=false;

    private String userId;
    PhoneAuthCredential credential;
    Uri ImageData;
    private ProgressDialog loadingBar;


    private String Stphone,OlduserKey;
    private Spinner spinner;
    String number = "";
    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        SharedPreferences preferences = getSharedPreferences("sharedFile",MODE_PRIVATE);
        Stphone = preferences.getString("phone","000000000");
        userId =preferences.getString("userkey","000000");


        mtoolbar = findViewById(R.id.toolbar_tool);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(getString(R.string.creataccount));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imagecard = findViewById(R.id.imagwcard);
        updatebuttoncard = findViewById(R.id.buttonwcard);
        circleImageView = findViewById(R.id.head_circle);
        name = findViewById(R.id.username);
        email = findViewById(R.id.emailtextView);
        phone = findViewById(R.id.phonetextView);
        spinner = findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));



        phone.setText(Stphone);

        if(Stphone.equals("000000000")){ phone.setEnabled(true); phone.setText("");}

        loadingBar = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();



        UserRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (snapshot.child("name").exists()) {
                    String nameval = snapshot.child("name").getValue().toString();
                    name.setText(nameval);
                }

                if (snapshot.child("phone").exists()) {
                    String phoneVal = snapshot.child("phone").getValue().toString();
                    phone.setText(phoneVal);
                }

                if (snapshot.child("email").exists()) {
                    String emailVal = snapshot.child("email").getValue().toString();
                    email.setText(emailVal);
                }

                if (snapshot.child("image").exists())
                {

                    uploadImage=true;
                    selectImage=true;
                    Toast.makeText(RegisterActivity.this, "selectImage=true;", Toast.LENGTH_SHORT).show();
                    previousimage=true;
                    String imageVal = snapshot.child("image").getValue().toString();
                    Picasso.get().load(imageVal).into(circleImageView);


                }
                else{ Toast.makeText(RegisterActivity.this, "selectImage=false", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        /*
        face_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,FaceAuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

         */

        imagecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent gallaryIntint = new Intent();
                gallaryIntint.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntint.setType("image/*");
                startActivityForResult(gallaryIntint, Gallary_pic);


            }
        });


        updatebuttoncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(RegisterActivity.this, selectImage.toString(), Toast.LENGTH_SHORT).show();

             /*   if(selectImage)
                {
                    Toast.makeText(RegisterActivity.this, "selectImage=true;", Toast.LENGTH_SHORT).show();
                    //   searchPhoneInUsers();
                    saveImage();
                    selectImage=false;
                }
                else{
                    Toast.makeText(RegisterActivity.this, R.string.imagefirst, Toast.LENGTH_SHORT).show();
                }

              */



            }
        });



    }

    private void searchPhoneInUsers()
    {
        DatabaseReference usersRef= FirebaseDatabase.getInstance().getReference().child("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String othersPhone=null,userphone=null;
                long usercount = snapshot.getChildrenCount();
                for(DataSnapshot users : snapshot.getChildren())
                {
                    String userkey = users.getKey().toString();

                    usercount--;

                    if(snapshot.child(userkey).child("phone").exists())
                    {
                     othersPhone = snapshot.child(userkey).child("phone").getValue().toString();
                     userphone = phone.getText().toString().trim();
                    if(othersPhone.equals(userphone))
                      {
                        showMovingMessage(userkey);
                          phoneMatched=true;

                      }
                    else
                    {
                        if(usercount==0)
                        {
                             if (! phoneMatched)
                            {
                                if (!othersPhone.equals(userphone)) {
                                    showEditingMessage();
                                }
                            }
                        }
                    }


                    }else
                    {
                        if(usercount==0)
                        {
                             if (! phoneMatched)
                            {
                                if (!othersPhone.equals(userphone)) {

                                    showEditingMessage();
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

    private void showEditingMessage()
    {
        String othersPhone = phone.getText().toString().trim();
        final String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];

        if (othersPhone.startsWith("0")){ number=othersPhone.substring(1); }
        number = code +number;

        if
        (number.isEmpty() || number.length() < 9)
        {
            phone.setError("Valid number is required");
            phone.requestFocus();
            return;
        }
         sendVerificationCode(number);
        final android.app.AlertDialog alertadd = new android.app.AlertDialog.Builder(RegisterActivity.this).create();
        final LayoutInflater factory = LayoutInflater.from(RegisterActivity.this);
        View dialogView = factory.inflate(R.layout.sample_dialog_otp, null);

        EditText otp = dialogView.findViewById(R.id.otp);


        Button submit = dialogView.findViewById(R.id.submitBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Stotp = otp.getText().toString();
                if (!TextUtils.isEmpty(Stotp)) {
                    verifyCode(Stotp);
                }

            }
        });
        alertadd.setView(dialogView);
        alertadd.show();
    }

    private void showMovingMessage(String userkey)
    {

            OlduserKey = userkey;
            String othersPhone = phone.getText().toString().trim();
            AlertDialog alert=     new AlertDialog.Builder(RegisterActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.phoneRegestedbefore))
                    .setMessage(getString(R.string.useAnotherNo))
                    .setPositiveButton(getString(R.string.ok), null)
                    /*    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
                                if (othersPhone.startsWith("0")){number=othersPhone.substring(1);}
                                number = code +number;
                                if (number.isEmpty() || number.length() < 9)
                                {
                                    phone.setError("Valid number is required");
                                    phone.requestFocus();
                                    return;
                                }
                                sendVerificationCode(number);
                                final android.app.AlertDialog alertadd = new android.app.AlertDialog.Builder(RegisterActivity.this).create();
                                final LayoutInflater factory = LayoutInflater.from(RegisterActivity.this);
                                View dialogView = factory.inflate(R.layout.sample_dialog_otp, null);
                                EditText otp = dialogView.findViewById(R.id.otp);
                                Button submit = dialogView.findViewById(R.id.submitBtn);
                                submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String Stotp = otp.getText().toString();
                                        if (!TextUtils.isEmpty(Stotp))
                                        {
                                            mAuth.signOut();
                                            LoginManager.getInstance().logOut();
                                            verifyCode(Stotp);
                                        }
                                    }
                                });
                                alertadd.setView(dialogView);
                                alertadd.show();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                   */
                    .show();
    }

    private void SendintoMainactivity()
    {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== Gallary_pic)
        {

            if (resultCode==RESULT_OK){

                ImageData = data.getData();
                Picasso.get().load(ImageData).into(circleImageView);
                selectImage=true;
                Toast.makeText(RegisterActivity.this, "selectImage=true;", Toast.LENGTH_SHORT).show();

            }

        }
        else
        {
            Toast.makeText(RegisterActivity.this, R.string.Selectimage, Toast.LENGTH_SHORT).show();
        }
    }


    private void saveImage()
    {

        mFirebaseUser = mAuth.getCurrentUser();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("users").child(userId).child("image");
        UserProfileImageRef.putFile(ImageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(RegisterActivity.this,R.string.Uploading,Toast.LENGTH_SHORT).show();
                UserProfileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUri = String.valueOf(uri);
                        Picasso.get().load(uri).into(circleImageView);
                        Toast.makeText(RegisterActivity.this,R.string.Uploaded,Toast.LENGTH_SHORT).show();
                        uploadImage=true;
                        if (uploadImage)
                        {
                            saveData();

                        }
                        else{
                            Toast.makeText(RegisterActivity.this, R.string.imagefirst , Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });



    }

    private void saveData()
    {
        String nameVal = name.getText().toString();
        if(TextUtils.isEmpty(nameVal))
        {
            Toast.makeText(RegisterActivity.this,R.string.usernamefirst,Toast.LENGTH_SHORT).show();
        }
        else {
            String emailVal = email.getText().toString();
            if (TextUtils.isEmpty(emailVal)) {
                Toast.makeText(RegisterActivity.this, R.string.emailfrist, Toast.LENGTH_SHORT).show();
            } else {

                String phoneVal = phone.getText().toString();

                userMap.put("name", nameVal);
                userMap.put("email", emailVal);
                userMap.put("phone", phoneVal);
               if(!previousimage){ userMap.put("image", imageUri);}


                UserRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                UserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.Uploaded), Toast.LENGTH_SHORT).show();


                    }
                });
            }
        }
    }


    private void sendWellcomingEmail()
    {
        String mail = email.getText().toString().trim();
        String message = getResources().getString(R.string.wellcomMessage);
        String subject = getResources().getString(R.string.wellcomSubject);

        JavaMailAPI javaMailAPI = new JavaMailAPI(RegisterActivity.this,mail,subject,message);
        javaMailAPI.execute();
    }

    @Override
    public void onBackPressed() {
        mAuth.signOut();

    }



    private void sendVerificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                RegisterActivity.this,
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
            if (code != null)
            {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(RegisterActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();

        }
    };




    private void verifyCode(String code)
    {
         credential = PhoneAuthProvider.getCredential(verificationid, code);
        signInWithCredential( credential);
       // editeData();
    }

    private void editeData()
    {
        DatabaseReference FaceUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        DatabaseReference OldUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(OlduserKey);

        FaceUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
             if(snapshot.child("faceID").exists())
             {
                 String faceID= snapshot.child("faceID").getValue().toString();
                 OldUserRef.child("faceID").setValue(faceID);
             }


                String nameVal = name.getText().toString();
                if(TextUtils.isEmpty(nameVal))
                {
                    Toast.makeText(RegisterActivity.this,R.string.usernamefirst,Toast.LENGTH_SHORT).show();
                }
                else {
                    String emailVal = email.getText().toString();
                    if (TextUtils.isEmpty(emailVal)) {
                        Toast.makeText(RegisterActivity.this, R.string.emailfrist, Toast.LENGTH_SHORT).show();
                    } else {

                        String phoneVal = phone.getText().toString();

                        userMap.put("name", nameVal);
                        userMap.put("email", emailVal);
                        userMap.put("phone", phoneVal);
                        if(!previousimage){ userMap.put("image", imageUri);}



                        OldUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                            SendintoMainactivity();
                           // sendEditionEmail();
                            Toast.makeText(RegisterActivity.this, getString(R.string.done), Toast.LENGTH_LONG).show();

                        }
                        else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }

    private void sendEditionEmail()
    {
        String mail = email.getText().toString().trim();
        String message = getResources().getString(R.string.edtionMessage);
        String subject = getResources().getString(R.string.ProfileeditionSubject);

        JavaMailAPI javaMailAPI = new JavaMailAPI(RegisterActivity.this,mail,subject,message);
        javaMailAPI.execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}