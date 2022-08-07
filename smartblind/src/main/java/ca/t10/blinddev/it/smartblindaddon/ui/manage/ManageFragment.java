package ca.t10.blinddev.it.smartblindaddon.ui.manage;
//Vyacheslav Perepelytsya n01133953
//Chris Mutuc n01314607
//Amit Punit n01203930
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.Set;

import ca.t10.blinddev.it.smartblindaddon.BlindInfo;
import ca.t10.blinddev.it.smartblindaddon.BlindNotifications;
import ca.t10.blinddev.it.smartblindaddon.R;

import static android.content.ContentValues.TAG;

public class ManageFragment extends Fragment {
    private View root;
    private ManageViewModel mViewModel;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dRef,addRefToUser,addToBlind;
    BlindInfo blindInfo;
    Button delete,add,submit;
    Spinner selectBlind;
    EditText loc,bkey,height;
    TextView retrieveTV;

    public static ManageFragment newInstance() {
        return new ManageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_manage, container, false);
        delete = root.findViewById(R.id.manage_delete_btn);
        add = root.findViewById(R.id.manage_add_btn);
        submit = root.findViewById(R.id.manage_submit_btn);
        selectBlind = root.findViewById(R.id.manage_delete_select);
        loc = root.findViewById(R.id.manage_add_loc);
        bkey = root.findViewById(R.id.manage_add_bkey);
        height = root.findViewById(R.id.manage_add_height);
        retrieveTV = root.findViewById(R.id.mange_blind_loc);

        String[] blindInstance;
        loc.setVisibility(View.INVISIBLE);
        bkey.setVisibility(View.INVISIBLE);
        height.setVisibility(View.INVISIBLE);
        selectBlind.setVisibility(View.GONE);
        delete.setBackgroundColor(Color.GRAY);
        add.setBackgroundColor(Color.GRAY);

        applySettings();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saved",Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("user_key","");

        firebaseDatabase = FirebaseDatabase.getInstance();
        dRef = firebaseDatabase.getReference();

        //this is to add blinds to user profile
        // .child(blind key).setValue(blind key); use this to add blinds to user profile
        addRefToUser = firebaseDatabase.getReference("Users").child(userID).child("Owned");

        blindInfo = new BlindInfo();


        // here is how to get user owned blinds keys from shared preferences
        //data is in here
        Set<String> set = sharedPreferences.getStringSet("blinds_owned", null);
        Log.i(TAG, set.toString());
        blindInstance = set.toArray(new String[0]);

        Spinner sItems = root.findViewById(R.id.manage_delete_select);

        //Create spinner style based on settings

        if (sharedPreferences.getBoolean("dark",true)){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_style, blindInstance);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sItems.setAdapter(adapter);
        }
        else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_style_default, blindInstance);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sItems.setAdapter(adapter);
        }
        System.out.println("manage" + set.toString());

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loc.setVisibility(View.VISIBLE);
                bkey.setVisibility(View.VISIBLE);
                height.setVisibility(View.VISIBLE);
                loc.getText().clear();
                bkey.getText().clear();
                height.getText().clear();
                selectBlind.setVisibility(View.GONE);
                delete.setBackgroundColor(Color.GRAY);
                add.setBackgroundColor(Color.GREEN);
                retrieveTV.setVisibility(View.INVISIBLE);
                delete.setActivated(false);
                add.setActivated(true);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loc.setVisibility(View.INVISIBLE);
                bkey.setVisibility(View.INVISIBLE);
                height.setVisibility(View.INVISIBLE);
                selectBlind.setVisibility(View.VISIBLE);
                retrieveTV.setVisibility(View.VISIBLE);
                delete.setBackgroundColor(Color.GREEN);
                add.setBackgroundColor(Color.GRAY);
                delete.setActivated(true);
                add.setActivated(false);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delete.isActivated()){
                    //code here
                    addRefToUser.child(selectBlind.getSelectedItem().toString()).removeValue();
                    refreshFragment();
                    Toast.makeText(getActivity(), "Blind Successfully Deleted!", Toast.LENGTH_SHORT).show();
                }
                else if (add.isActivated()) {
                    //code here
                    // getting text
                    String location = loc.getText().toString();
                    String blindKey = bkey.getText().toString();
                    String blindHeight = height.getText().toString();

                    //this reference is for saving data to the blind itself
                    // .child("Height").setValue(blind height)
                    addToBlind = firebaseDatabase.getReference(blindKey);

                    if (TextUtils.isEmpty(location) || TextUtils.isEmpty(blindKey) || TextUtils.isEmpty(blindHeight)) {
                        // if no data show message to fill data
                        Toast.makeText(getActivity(), "Please fill in blind data.", Toast.LENGTH_SHORT).show();
                    } else {
                        // else call the method to add data to firebase
                        addDatatoFirebase(location, blindKey, blindHeight);
                        refreshFragment();
                        Toast.makeText(getActivity(), "Blind Successfully added!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            private void addDatatoFirebase(String location, String blindKey, String blindHeight) {
                // below 3 lines of code are used to set data in our object class.
                blindInfo.setLocation(location);
                blindInfo.setKey(blindKey);
                blindInfo.setHeight(blindHeight);

                //read values user entered
                addRefToUser.child(blindKey).setValue(blindKey);
                addToBlind.setValue(blindKey);
                addToBlind.child("Location").setValue(location);
                addToBlind.child("Height").setValue(blindHeight);

                //create random values
                // to be used from device later
                String [] arr = {"close", "open"};
                Random random = new Random();

                // randomly selects an index from the arr
                int select = random.nextInt(arr.length);

                addToBlind.child("Status").setValue(arr[select]);
                addToBlind.child("Light").setValue(String.valueOf(Math.floor(Math.random() * 100)));
                addToBlind.child("Temperature").setValue(String.valueOf(Math.floor(Math.random() * 100)));

            }
        });

        // this will display the location of the blind on the screen
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String k = sItems.getSelectedItem().toString();

                dRef.child(k).child("Location").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String lo = snapshot.getValue(String.class);
                        retrieveTV.setText("Location:" + " " + lo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return root;
    }

    private void refreshFragment(){
        //to be done, this would be to refresh fragment right away instead of on reload
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ManageViewModel.class);
        // TODO: Use the ViewModel
    }
    public void applySettings(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saved", Context.MODE_PRIVATE);

        boolean d = sharedPreferences.getBoolean("dark",false);
        boolean n = sharedPreferences.getBoolean("note",false);
        String t = sharedPreferences.getString("size","");

        if(d){enableDarkMode();}
        if(n){
            BlindNotifications bl = new BlindNotifications(root.getContext());
            //this method will allow developer to create message for notification
            bl.enableNotifications("this is from manage fragment");
            //this function will launch the notification.
            bl.pushNotification();
        }

        if (t.equals("large")){setTextSize(20);}
        if (t.equals("medium")){setTextSize(17);}
        if (t.equals("small")){setTextSize(13);}
    }
    public void setTextSize(int size){
        delete.setTextSize(size);
        add.setTextSize(size);
        submit.setTextSize(size);
        retrieveTV.setTextSize(size);
        //TODO
        //add code for spinner when implemented
    }
    private void enableDarkMode() {
        root.setBackgroundColor(getResources().getColor(R.color.dark_grey,null));
        loc.setHintTextColor(getResources().getColor(R.color.white,null));
        bkey.setHintTextColor(getResources().getColor(R.color.white,null));
        height.setHintTextColor(getResources().getColor(R.color.white,null));
        retrieveTV.setTextColor(getResources().getColor(R.color.white,null));
    }
}