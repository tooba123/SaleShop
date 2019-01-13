package sale.shop.app.firebasetutorials1_clientside;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

public class MyService extends Service {
    public MyService() {
    }

    private HashMap<String,String> SenderHashMap;
    private DatabaseReference mDatabase;
    public int createID(){
        return (int)((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance().getReference();

        if (SenderHashMap == null )
            SenderHashMap = new HashMap<String,String>();

        mDatabase.child("1Senders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child: children){

                    SenderHashMap.put( child.getKey().toString(),child.child("SenderName").getValue().toString());
                }

//                for (String key : SenderHashMap.keySet()) {
//                    System.out.println("***********************"+key+ " " + SenderHashMap.get(key));
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Get a firebase generated key, based on current time
         String startKey = mDatabase.child("2Messages").push().getKey();

         // 'startAt' this key, equivalent to 'start from the present second'
        mDatabase.child("2Messages").orderByKey().startAt(startKey)
        .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String sender =  SenderHashMap.get( dataSnapshot.child("SenderID").getValue().toString());
                String message =  dataSnapshot.child("Text").getValue().toString();

                // Create an explicit intent for an Activity in your app
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Add sound in notification...
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "Notification2")
                        .setContentTitle(sender)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                        .setSound(soundUri)
                        .setSmallIcon(R.drawable.ic_notify)
                        /*.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                R.mipmap.ic_launcher_one))*/
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(createID(), mBuilder.build());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                String sender =  SenderHashMap.get( dataSnapshot.child("SenderID").getValue().toString());
//                String message =  dataSnapshot.child("Text").getValue().toString();
//
//                // Create an explicit intent for an Activity in your app
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "Notification2")
//                        .setContentTitle(sender)
//                        .setContentText(message)
//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText(message))
//                        .setSmallIcon(R.drawable.ic_launcher_background)
//                        // Set the intent that will fire when the user taps the notification
//                        .setContentIntent(pendingIntent)
//                        .setAutoCancel(true);
//
//                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
//
//                // notificationId is a unique int for each notification that you must define
//                notificationManager.notify(createID(), mBuilder.build());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service1 destroyed...", Toast.LENGTH_SHORT).show();



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Service1 started...", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public boolean isMainActivityRunning(String packageName) {
        /*ActivityManager activityManager = (ActivityManager) getSystemService (Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (int i = 0; i < tasksInfo.size(); i++) {
            String a1 = tasksInfo.get(i).baseActivity.getPackageName().toString();
            boolean z = tasksInfo.get(i).baseActivity.getPackageName().toString().equals(packageName);
            if (tasksInfo.get(i).baseActivity.getPackageName().toString().equals(packageName)) {
                return true;
            }
        }*/

        return false;
    }

}
