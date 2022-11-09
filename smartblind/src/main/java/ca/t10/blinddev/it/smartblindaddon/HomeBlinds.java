package ca.t10.blinddev.it.smartblindaddon;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
// recyclerview on the homepage
//Chris Mutuc n01314607
//Amit Punit n01203930
//Andrew Fraser N01309442


public class HomeBlinds {
    DatabaseReference ref;
    String blindkey;


    // this will house data on the specific blind.
    public HomeBlinds(String blindkey) {
        this.blindkey = blindkey;
        ref = FirebaseDatabase.getInstance().getReference(blindkey);
    }



    //when this method is called it will set the status node of the blind to open which is then read
    // by the device
    public void openBlinds(){
        ref.child("Status").setValue("open");
    }

    public void closeBlinds(){
        ref.child("Status").setValue("close");
    }

    public void blindsMode(String mode){
        ref.child("Mode").setValue(mode);
    }




}
