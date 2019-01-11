package com.example.farahsaeed.firebasetutorials1;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mLogoutBtn;

    Button btnAll;
    Button btnInbox;
    Button btnSent;
    Button btnDraft;
    TableLayout tblMain;
    HashMap<String, String> SenderHashMap;
    public void StartService()
    {
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
//        startForegroundService(intent);
    }


    public void StopService()
    {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }



    protected void onPause(Bundle savedInstanceState) {
        StopService();
    }


    protected void onResume(Bundle savedInstanceState) {
        StartService();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        DataLayer dl = new DataLayer();
        SenderHashMap = dl.getData();

        StartService();

        mLogoutBtn = (Button) findViewById(R.id.logout_btn);

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(AccountActivity.this, MainActivity.class));

                StopService();

            }
        });

        try {
            /*
             * Initializing Widgets
             */

            btnAll = (Button) findViewById(R.id.btnAll);
            btnAll.setOnClickListener(this);

            btnInbox = (Button) findViewById(R.id.btnInbox);
            btnInbox.setOnClickListener(this);

            btnSent = (Button) findViewById(R.id.btnSent);
            btnSent.setOnClickListener(this);

            btnDraft = (Button) findViewById(R.id.btnDraft);
            btnDraft.setOnClickListener(this);

            tblMain = (TableLayout) findViewById(R.id.tblMain);

        } catch (Exception ex) {
            Toast.makeText(this,
                    "Error in MainActivity.onCreate: " + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public void onClick(View v) {
        Uri smsUri = Uri.parse("content://sms/");

        switch (v.getId()) {
            case R.id.btnInbox:
                smsUri = Uri.parse("content://sms/inbox");
                break;
            case R.id.btnSent:
                smsUri = Uri.parse("content://sms/sent");
                break;
            case R.id.btnDraft:
                smsUri = Uri.parse("content://sms/draft");
                break;
        }

        Cursor cursor = getContentResolver().query(smsUri, null, null, null, null);

        Cursor2TableLayout(cursor, tblMain);
    }

    public void Cursor2TableLayout(Cursor cur, TableLayout tblLayout) {

        /* Clearing Table If Contains Any Rows/Headers */
        tblLayout.removeAllViews();

        /* Moving To First */
        if (!cur.moveToFirst()) { /* false = cursor is empty */
            return;
        }

        /* Column Headers */

        TableRow headersRow = new TableRow(this);

        for (int j = 0; j < cur.getColumnCount(); j++) {
            TextView textView = new TextView(this);

            textView.setGravity(Gravity.CENTER_HORIZONTAL);

            textView.setText(cur.getColumnName(j));

            textView.setPadding(0, 0, 5, 0);

            if (Build.VERSION.SDK_INT >= 11) { /* If running Android 3.0+ */
                textView.setAlpha(0.8f);
            } else {
                /* Phones with Android 2.x are a rarity now,
                 * but until recently (June, 2015) I have seen them
                 * in the store...
                 * Also, I myself have such gadget and using it sometimes... */
                AlphaAnimation animation = new AlphaAnimation(0.8f, 0.8f);
                animation.setDuration(0);
                animation.setFillAfter(true);
                textView.startAnimation(animation);
            }

            headersRow.addView(textView);
        }

        headersRow.setPadding(10, 10, 10, 10);

        tblLayout.addView(headersRow);

        /* Rows */

//        for (int i = 0; i < cur.getCount(); i++) {

        String msgSender = "";
        String msgBody = "";

        for (int i = 0; i < 20; i++) {
            TableRow tableRow = new TableRow(this);

            for (int j = 0; j < cur.getColumnCount(); j++) {
                TextView textView = new TextView(this);

                textView.setGravity(Gravity.CENTER_HORIZONTAL);

                String content = cur.getString(j);

                if (j==2)
                {
                    msgSender = content;
                }
                if (j==13)
                {
                    msgBody = content;
                }
                textView.setText(content);

                textView.setPadding(0, 0, 5, 0);

                tableRow.addView(textView);
            }
            System.out.println(cur.getString(2));

            /////////////////// setting button
            Button btn=new Button(this);
            btn.setText("SEND");
            String[] msgdetails = {msgSender, msgBody};
            btn.setTag(msgdetails);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                   String[] msgdetails_arr = (String[])v.getTag();
                   System.out.println(msgdetails_arr[0]+" "+ msgdetails_arr[1]);

                   String phone = msgdetails_arr[0];
                   String message = msgdetails_arr[1];

                    if (SenderHashMap.containsKey(phone.toLowerCase())) {

                        SimpleDateFormat dateobj = new SimpleDateFormat("ddMMyyyyhhmmss");
                        String currentDateTime = dateobj.format(new Date());

                        HashMap<String, String> datamap = new HashMap<>();
                        datamap.put("SenderID", SenderHashMap.get(phone.toLowerCase()));
                        datamap.put("Text", message);
                        datamap.put("DateTime", currentDateTime);
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("2Messages").push().setValue(datamap);

                    }
                }





            });







            tableRow.addView(btn);

            tableRow.setPadding(10, 10, 10, 10);

            tblLayout.addView(tableRow);
            cur.moveToNext();
        }

        cur.close();
    }


}
