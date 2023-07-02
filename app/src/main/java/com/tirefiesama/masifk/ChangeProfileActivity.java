package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.media.MediaBrowserServiceCompat.RESULT_OK;

public class ChangeProfileActivity extends AppCompatActivity {


    private CardView imagecard,buttoncard;
    private CircleImageView circleImageView;
    private EditText name,email,phone;
    private DatabaseReference UserRef;
    private String UserID,imageUri,userkey,Stphone;
    private FirebaseAuth mAuth;
    final static int Gallary_pic = 1;
    HashMap userMap = new HashMap();
    private StorageReference UserProfileImageRef;
    private Boolean uploadImage=false,oldImage=false;
    private View itemsview;
    private Toolbar mtoolbar;
    Uri ImageData;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);



        mtoolbar = findViewById(R.id.toolbar_tool);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(getString(R.string.changeprofile));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagecard =findViewById(R.id.imagwcard);
        buttoncard = findViewById(R.id.buttonwcard);
        circleImageView = findViewById(R.id.head_circle);
        name = findViewById(R.id.username);
        email = findViewById(R.id.linktextView);
        phone = findViewById(R.id.cvtextView);

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getUid().toString();

        SharedPreferences preferences = getSharedPreferences("sharedFile",MODE_PRIVATE);
        Stphone = preferences.getString("phone","000000000");
        userkey =preferences.getString("userkey","000000");

        UserRef = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.child("name").exists())
                {
                    String nameval = snapshot.child("name").getValue().toString();
                    name.setText(nameval);
                }

                if(snapshot.child("phone").exists())
                {
                    String phoneVal = snapshot.child("phone").getValue().toString();
                    phone.setText(phoneVal);
                }
                else { phone.setText(Stphone); }

                if(snapshot.child("email").exists())
                {
                    String emailVal = snapshot.child("email").getValue().toString();
                    email.setText(emailVal);
                }

                if(snapshot.child("image").exists())
                {
                    String imageVal = snapshot.child("image").getValue().toString();
                    Picasso.get().load(imageVal).into(circleImageView);
                    oldImage=true;

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


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


        buttoncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (uploadImage)
                {
                    progressDialog = ProgressDialog.show(ChangeProfileActivity.this,
                            getString(R.string.wait),getString(R.string.uploading),false,false);

                    saveImage();

                }
                if(uploadImage==false)
                {
                    if(oldImage)
                    {
                        progressDialog = ProgressDialog.show(ChangeProfileActivity.this,
                                getString(R.string.wait),getString(R.string.uploading),false,false);

                        String phoneVal = phone.getText().toString();
                        String nameVal = name.getText().toString();
                        String emailVal = email.getText().toString();

                        userMap.put("name", nameVal);
                        userMap.put("email", emailVal);
                        userMap.put("phone", phoneVal);



                        UserRef = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
                        UserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task)
                            {
                                Toast.makeText(ChangeProfileActivity.this, getResources().getString(R.string.Uploaded), Toast.LENGTH_SHORT).show();
                                SendNotificationEmail();

                            }
                        });

                    }
                    else {
                        Toast.makeText(ChangeProfileActivity.this, R.string.Selectimage, Toast.LENGTH_SHORT).show();
                    }
                }






            }
        });




    }




    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== Gallary_pic)
        {

            if (resultCode==RESULT_OK){

                ImageData = data.getData();
                Picasso.get().load(ImageData).into(circleImageView);
                uploadImage=true;
            }

        }
        else
        {
            Toast.makeText(ChangeProfileActivity.this, R.string.Selectimage, Toast.LENGTH_SHORT).show();
        }
    }


    private void saveImage()
    {

        if (uploadImage)
        {
            String nameVal = name.getText().toString();
            if(TextUtils.isEmpty(nameVal))
            {
                Toast.makeText(ChangeProfileActivity.this,R.string.usernamefirst,Toast.LENGTH_SHORT).show();
            }
            else {
                String emailVal = email.getText().toString();
                if (TextUtils.isEmpty(emailVal))
                {
                    Toast.makeText(ChangeProfileActivity.this, R.string.emailfrist, Toast.LENGTH_SHORT).show();
                } else
                    {

                        FirebaseUser mFirebaseUser;
                        mFirebaseUser = mAuth.getCurrentUser();
                        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("users").child(UserID).child("image");
                        UserProfileImageRef.putFile(ImageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                 UserProfileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageUri = String.valueOf(uri);
                                        Picasso.get().load(uri).into(circleImageView);


                                        String phoneVal = phone.getText().toString();

                                        userMap.put("name", nameVal);
                                        userMap.put("email", emailVal);
                                        userMap.put("phone", phoneVal);
                                        userMap.put("image", imageUri);


                                        UserRef = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
                                        UserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task)
                                            {
                                                Toast.makeText(ChangeProfileActivity.this, getResources().getString(R.string.Uploaded), Toast.LENGTH_SHORT).show();
                                                SendNotificationEmail();

                                            }
                                        });


                                    }
                                });
                            }
                        });

                }
            }
        }
        else{
            Toast.makeText(ChangeProfileActivity.this, R.string.imagefirst , Toast.LENGTH_SHORT).show();
        }






    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    private void SendNotificationEmail()
    {
        String mail = email.getText().toString().trim();
        String message = getResources().getString(R.string.edtionMessage);
        String subject = getResources().getString(R.string.ProfileeditionSubject);

        JavaMailAPI javaMailAPI = new JavaMailAPI(ChangeProfileActivity.this,mail,subject,message);
        javaMailAPI.execute();


        Intent intent = new Intent(ChangeProfileActivity.this,MainActivity.class);
        progressDialog.dismiss();
        startActivity(intent);
    }


}