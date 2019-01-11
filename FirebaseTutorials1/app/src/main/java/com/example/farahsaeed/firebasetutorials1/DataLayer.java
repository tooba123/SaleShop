package com.example.farahsaeed.firebasetutorials1;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by farah.saeed on 1/4/2019.
 */

public class DataLayer {


    private DatabaseReference mDatabase;

    private HashMap<String,String> SenderHashMap;


    public HashMap<String,String>  getData() {

        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance().getReference();

        if (SenderHashMap == null)
            SenderHashMap = new HashMap<String, String>();


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

        return SenderHashMap;
    }

    }
