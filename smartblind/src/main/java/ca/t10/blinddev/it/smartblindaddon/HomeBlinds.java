package ca.t10.blinddev.it.smartblindaddon;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
// recyclerview on the homepage

public class HomeBlinds {
    DatabaseReference ref;
    String temp,light,blindkey,location;


    // this will house data on the specific blind.
    public HomeBlinds(String test,String blindkey) {
        this.location = test;// this need to be changed
        this.blindkey = blindkey;
        ref = FirebaseDatabase.getInstance().getReference(blindkey);


    }
    // the problem is that since that the recycle viewer is created first the data will be set to null first
    //then the database is asked for the data and it stays in the
    public String getLocation() {
        ref.child("Location").addValueEventListener(new ValueEventListener() {
            String loc;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    loc = String.valueOf(snapshot.getValue());
                    location = loc;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return location;
    }
    public String getTemperature(){
        ref.child("Temperature").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            temp = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Temp error",error.toException());
            }
        });

        return temp;
    }
    public String getLight(){
        ref.child("Light").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                light = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Temp error",error.toException());
            }
        });

        return light;
    }

    public void openBlinds(){
        ref.child("Status").setValue("open");
    }

    public void closeBlinds(){
        ref.child("Status").setValue("close");
    }


}
