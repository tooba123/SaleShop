package com.example.farahsaeed.firebasetutorials1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by farah.saeed on 3/29/2018.
 */

public class MyService extends Service {

    private DatabaseReference mDatabase;

    private SmsReceiver mSMSreceiver;
    private IntentFilter mIntentFilter;
    private ActionReceiver mActionreceiver;
    private IntentFilter mActionIntentFilter;
    private HashMap<String,String> SenderHashMap;



    private class ActionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle intentExtras = intent.getExtras();

            String phone = intentExtras.get("phone").toString();
            String message = intentExtras.get("message").toString();
            int notification_id = intentExtras.getInt("notification_id");


            SimpleDateFormat dateobj = new SimpleDateFormat("ddMMyyyyhhmmss");
            String currentDateTime = dateobj.format(new Date());

            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("SenderID", SenderHashMap.get(phone.toLowerCase()));
            datamap.put("Text", message);
            datamap.put("DateTime", currentDateTime);
            mDatabase.child("2Messages").push().setValue(datamap);

            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notification_id);

        }
    }

        private class SmsReceiver extends BroadcastReceiver {

        public int createID(){
           return (int)((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle intentExtras = intent.getExtras();

            String phone  = "";
            String message  = "";
            if (intentExtras != null) {
            /* Get Messages */
                Object[] sms = (Object[]) intentExtras.get("pdus");

                for (int i = 0; i < sms.length; ++i) {
                /* Parse Each Message */
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                     phone = smsMessage.getOriginatingAddress();
                     message = message + smsMessage.getMessageBody().toString();

                }

                phone = phone.trim().toLowerCase();
                    Toast.makeText(context, phone + ": " + message, Toast.LENGTH_SHORT).show();




//                  Add Data into Firebase Database

                    if (SenderHashMap.containsKey(phone.toLowerCase())) {
                        int notificationId = createID();
                        Intent snoozeIntent = new Intent(
//                                context, ActionReceiver.class
                        );
                        snoozeIntent.setAction("ACTION_ADD_RECORD");
                        snoozeIntent.putExtra("phone", phone);
                        snoozeIntent.putExtra("message", message);
                        snoozeIntent.putExtra("notification_id", notificationId);
                        PendingIntent snoozePendingIntent =
                                PendingIntent.getBroadcast(context, 0, snoozeIntent,   PendingIntent.FLAG_UPDATE_CURRENT);

                        // Build the notification and add the action.
                        Intent intent1 = new Intent(context, MainActivity.class);
                        PendingIntent pendingIntent= PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                        Notification newMessageNotification = new NotificationCompat.Builder(context, "Notification_channel_id_1")
                                .setContentTitle("Broadcast SMS")
                                .setContentText("From: "+phone)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Message: "+message+" ---From: "+phone))
                                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                .setContentIntent(pendingIntent)
                                .addAction(2, "SEND",
                                        snoozePendingIntent)
                                .setAutoCancel(true)
                                .build();
                        // Issue the notification.
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        notificationManager.notify(/*createID()*/notificationId, newMessageNotification);



                    }

            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance().getReference();

        if (SenderHashMap == null)
            SenderHashMap = new HashMap<String, String>();
        //SMS event receiver
        mSMSreceiver = new SmsReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSMSreceiver, mIntentFilter);


        mActionreceiver = new ActionReceiver();
        mActionIntentFilter = new IntentFilter();
        mActionIntentFilter.addAction("ACTION_ADD_RECORD");
        registerReceiver(mActionreceiver, mActionIntentFilter);


        //Retrieve Data from Firebase Database

        mDatabase.child("1Senders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child: children){

                    SenderHashMap.put(child.child("SenderName").getValue().toString(), child.getKey());
                }

                for (String key : SenderHashMap.keySet()) {
                    System.out.println("***********************"+key+ " " + SenderHashMap.get(key));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service destroyed...", Toast.LENGTH_SHORT).show();

        // Unregister the SMS receiver
        unregisterReceiver(mSMSreceiver);
        unregisterReceiver(mActionreceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service started...", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }






}
