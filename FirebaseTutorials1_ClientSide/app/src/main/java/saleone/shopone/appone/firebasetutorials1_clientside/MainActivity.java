package saleone.shopone.appone.firebasetutorials1_clientside;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import saleone.shopone.appone.firebasetutorials1_clientside.Model.SaleMessage;

import saleone.shopone.appone.firebasetutorials1_clientside.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private HashMap<String,String> SenderHashMap;
//    private ArrayList<String> senders;
//    private ArrayList<String> sendersMessages;


    private  ArrayList<SaleMessage> saleMessageList;

    ListView lst;

    /**
     * Method to start a back ground service
     * Back ground service will look for new record added in database
     * On new record addition it will send a notification
     */
    public void StartService()
    {
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    /**
     * Method to stop the background service
     */
    public void StopService()
    {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        StartService();
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance().getReference();


        SenderHashMap = new HashMap<String,String>();
//        senders = new ArrayList<>();
//        sendersMessages = new ArrayList<>();
            saleMessageList = new ArrayList<SaleMessage>();

        //Retrieve Data from Firebase Database


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

        mDatabase.child("2Messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                String senderChild =  SenderHashMap.get(dataSnapshot.child("SenderID").getValue().toString());
                String messageChild = dataSnapshot.child("Text").getValue().toString();
                String datetimeChild = "01012017000000";

                if (dataSnapshot.child("DateTime").getValue() != null){
                    datetimeChild = dataSnapshot.child("DateTime").getValue().toString();

                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                Date dateChild = null;

                try {
                    dateChild = dateFormat.parse(datetimeChild);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                saleMessageList.add(new SaleMessage(messageChild, senderChild, dateChild));

//                senders.add( SenderHashMap.get( dataSnapshot.child("SenderID").getValue().toString()));
//                sendersMessages.add( dataSnapshot.child("Text").getValue().toString());
//                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
//                System.out.println("***********************"+ " " + SenderHashMap.keySet().size());
//                for (String key : SenderHashMap.keySet()) {
//                    System.out.println("***********************"+key+ " " + SenderHashMap.get(key));
//                }
//                for (DataSnapshot child: children){
//
//                    senders.add( SenderHashMap.get( child.child("SenderID").getValue().toString()));
//                    sendersMessages.add( child.child("Text").getValue().toString());
////                    System.out.println("-------------------------------"+ " " + child.child("SenderID").getValue().toString());
//                }
//
//                for (String key : SenderHashMap.keySet()) {
//                    System.out.println("***********************"+key+ " " + SenderHashMap.get(key));
//                }

//                for (String key : senders) {
//                    System.out.println("-------------------------------"+key+ " " + key);
//                }
                new MyTask().execute();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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


        lst = (ListView) findViewById(R.id.listvw);;
        CustomAdapter customAdapter = new CustomAdapter(this,R.layout.customlayout,new ArrayList<SaleMessage>()  );

        lst.setAdapter(customAdapter);

        /// Adding ads Test App Id
        MobileAds.initialize(this,"ca-app-pub-3940256099942544~3347511713");

        try {
            //MobileAds.initialize(this, "ca-app-pub-5777265259393911~6452701866");
            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
//        AdRequest adRequest =  new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            mAdView.loadAd(adRequest);
        }
        catch (Exception exceptio){
            System.out.print("abc");
        }

        //runOnBackgroundThread();

    }


    class MyTask extends AsyncTask<Void, Integer, String>
    {
        ArrayAdapter cuAdapater;
        @Override
        protected void onPreExecute() {
            cuAdapater = (ArrayAdapter) lst.getAdapter();
//            cuAdapater.clear();
        }

        @Override
        protected String doInBackground(Void... voids) {

//            for(int i = 0; i < sendersMessages.size(); i++)
//            {
                publishProgress(saleMessageList.size()-1);
//            }

            return "All messages fetched successfully";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            cuAdapater.add(values[0] );
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }


    class CustomAdapter extends ArrayAdapter
    {
        ArrayList<SaleMessage> lists;

        public CustomAdapter(@NonNull Context context, int resource, @NonNull ArrayList<SaleMessage> objects) {
            super(context, resource, objects);
            lists = objects;

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.customlayout, null);

            TextView textView_name = (TextView) convertView.findViewById(R.id.textView_name);
            final TextView textView_description = (TextView) convertView.findViewById(R.id.textView_desciption);
            TextView textView_date = (TextView) convertView.findViewById(R.id.textView_date);
            final TextView textView_detail_description = (TextView) convertView.findViewById(R.id.textView_detail_desciption);
            //view_outer_message
            final View viewOuterMessage = (View) convertView.findViewById(R.id.view_outer_message);



            final View viewExpandable = (View)convertView.findViewById(R.id.view_epandable);
            viewOuterMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(textView_description.getVisibility() == View.VISIBLE){
                        textView_description.setVisibility(View.GONE);
                        textView_detail_description.setVisibility(View.VISIBLE);
                        viewExpandable.setRotation(180);
                        viewOuterMessage.setBackgroundColor(getResources().getColor(R.color.colorDarkGray));
                    } else {
                        textView_detail_description.setVisibility(View.GONE);
                        textView_description.setVisibility(View.VISIBLE);
                        viewExpandable.setRotation(360);
                        viewOuterMessage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                }
            });

            SaleMessage currentSaleMessage = saleMessageList.get(lists.size()-1-position);

//
//            String sendername = senders.get(lists.size()-1-position);
//            sendername = sendername.substring(0, 1).toUpperCase() + sendername.substring(1);

            textView_name.setText(currentSaleMessage.getSender());
            textView_description.setText(currentSaleMessage.getMessage());
            textView_detail_description.setText(currentSaleMessage.getMessage());

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
            String date = sdf.format(currentSaleMessage.getDate());

            textView_date.setText(date);

            return convertView;
        }

    }

    SearchView mSearchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        mSearchView = searchView;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            mSearchView.setIconifiedByDefault(true);
            mSearchView.setFocusable(true);
            mSearchView.setIconified(false);
            mSearchView.requestFocusFromTouch();
        }

        return super.onOptionsItemSelected(item);
    }


    public void runOnBackgroundThread(){
        final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(getIdThread()){
                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MobileAds.initialize(MainActivity.this, "ca-app-pub-5777265259393911~6452701866");
                            AdView mAdView = (AdView) findViewById(R.id.adView);
                            AdRequest adRequest = new AdRequest.Builder().build();
//        AdRequest adRequest =  new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                            mAdView.loadAd(adRequest);
                        }
                    });
                }
            }
        });
    }

    public boolean getIdThread() {

        AdvertisingIdClient.Info adInfo = null;
        try {
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(MainActivity.this);

        } catch (IOException e) {
            return false;
            // Unrecoverable error connecting to Google Play services (e.g.,
            // the old version of the service doesn't support getting AdvertisingId).

        }  catch (GooglePlayServicesNotAvailableException e) {
            return false;
            // Google Play services is not available entirely.
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            return false;
        }
        final String id = adInfo.getId();
        final boolean isLAT = adInfo.isLimitAdTrackingEnabled();
        return true;
    }
}
