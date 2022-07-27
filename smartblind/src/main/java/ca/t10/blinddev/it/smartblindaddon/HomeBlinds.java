package ca.t10.blinddev.it.smartblindaddon;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
// recyclerview on the homepage
//Chris Mutuc n01314607


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
