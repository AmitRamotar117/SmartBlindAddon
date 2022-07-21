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
    String blindkey;


    // this will house data on the specific blind.
    public HomeBlinds(String blindkey) {
        this.blindkey = blindkey;
        ref = FirebaseDatabase.getInstance().getReference(blindkey);
    }




    public void openBlinds(){
        ref.child("Status").setValue("open");
    }

    public void closeBlinds(){
        ref.child("Status").setValue("close");
    }


}
