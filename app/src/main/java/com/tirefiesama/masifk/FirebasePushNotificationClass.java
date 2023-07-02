package com.tirefiesama.masifk;



import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebasePushNotificationClass extends FirebaseMessagingService {

    public FirebasePushNotificationClass() {
        super();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
       // super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        if (data.containsKey("click_action")) {
            String value = data.get("click_action");
            Toast.makeText(this, value, Toast.LENGTH_SHORT).show();

            Class cls;
            try {
                cls = Class.forName(value);
                Intent intent = new Intent(this,cls);
                startActivity(intent);
              //  ClickActionHelper.startActivity(data.get("click_action"), null, this);
            }catch(ClassNotFoundException e){
                //means you made a wrong input in firebase console
            }

        }
    }


}