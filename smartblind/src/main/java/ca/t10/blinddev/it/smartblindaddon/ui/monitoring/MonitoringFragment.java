package ca.t10.blinddev.it.smartblindaddon.ui.monitoring;
//Chris Mutuc n01314607
import androidx.lifecycle.ViewModelProvider;
import static android.content.Context.MODE_PRIVATE;
import static android.provider.Telephony.Mms.Part.TEXT;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

import ca.t10.blinddev.it.smartblindaddon.BlindNotifications;
import ca.t10.blinddev.it.smartblindaddon.Monitoring;
import ca.t10.blinddev.it.smartblindaddon.R;
import ca.t10.blinddev.it.smartblindaddon.Schedule;


public class MonitoringFragment extends Fragment {
    private View view;
    private MonitoringViewModel mViewModel;

    private TextView retrieveTV, currentTemp,currentLight;

    Button submitbutton, tempButton;
    Spinner blindsspinner;

    private Monitoring monitoringInfo;
    private EditText maxET, minET,lightmaxET,lightminET;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dRef;


    //    public static final String TEXT = "text";
//    public static final String SWITCH1 = "switch1";
//    public static final String PROGRESS1 = "progress1";
//    private String text;
//    private boolean switchOnOff;
//    private boolean progress1;
    private String[] locationKey;
    private static final String TAG = "MyActivity";
    public static final String SHARED_PREFS = "sharedPrefs";
    String temp,light;


    public static MonitoringFragment newInstance() {
        return new MonitoringFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_monitoring, container, false);
        // applySettings();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saved",Context.MODE_PRIVATE);
        //data is in here
        Set<String> set = sharedPreferences.getStringSet("blinds_owned",null);
        Log.i(TAG,set.toString());
        locationKey =set.toArray(new String[0]);


        monitoringInfo = new Monitoring();

        lightmaxET = view.findViewById(R.id.lightmaxET);
        lightminET = view.findViewById(R.id.lightminET);

        //temperature ET
        maxET = view.findViewById(R.id.maxET);
        minET = view.findViewById(R.id.minET);
        tempButton = view.findViewById(R.id.tempButton);



        retrieveTV = view.findViewById(R.id.retrieveLocation);

        blindsspinner = view.findViewById(R.id.blindsspinner);

        submitbutton = view.findViewById(R.id.submitbutton);
        firebaseDatabase = FirebaseDatabase.getInstance();


        if (sharedPreferences.getBoolean("dark",true)){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_style, locationKey);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            blindsspinner.setAdapter(adapter);
        }
        else{
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_style_default, locationKey);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            blindsspinner.setAdapter(adapter);
        }


        blindsspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String blindkey = String.valueOf(blindsspinner.getSelectedItem());
                dRef =firebaseDatabase.getReference(blindkey);

                dRef.child("Location").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String lo = snapshot.getValue(String.class);
                        retrieveTV.setText("Location:" + " " + lo);
                        temp = lo;


                        //Toast.makeText(getActivity(),lo+" ",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        String blindkey = String.valueOf(blindsspinner.getSelectedItem());
        dRef =firebaseDatabase.getReference(blindkey);
        DatabaseReference tempRef =  dRef.child("UTemp");

        tempRef.child("temp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String server_data_temp = snapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException(); // never ignore errors
            }
        });

        DatabaseReference lightRef =  dRef.child("ULight");
        lightRef.child("light").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String server_data_light = snapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //comparing the values from the sensors data in realtime database
                //firebase initial setup
                String blindkey = String.valueOf(blindsspinner.getSelectedItem());
                dRef =firebaseDatabase.getReference(blindkey);
                DatabaseReference tempRef =  dRef.child("UTemp");

                // creating string holders
                String maxTemp = maxET.getText().toString();
                String minTemp = minET.getText().toString();

                //adding in the database
                if (TextUtils.isEmpty(minTemp) && TextUtils.isEmpty(maxTemp)) {
                    // if the text fields are empty
                    // then show the below message.
                    Toast.makeText(getActivity(), "Please add some data.", Toast.LENGTH_SHORT).show();
                } else {
                    // else call the method to add
                    // data to our database.

                    addDatatoFirebaseTemp(maxTemp,minTemp);
                    // getDataFromFirebase();

                }


                //Chris temperature code
                tempRef.child("temp").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String server_data_temp = snapshot.getValue(String.class);
                            // currentTemp.setText("Current Temperature: \n"+server_data_temp+" degrees");
                            if(maxTemp.equals(server_data_temp)){

                               // tempRef.child("maxTemp").setValue(maxTemp);
                                Toast.makeText(getActivity(),"Blinds is set to Close",Toast.LENGTH_SHORT).show();



                            }
                            else if(minTemp.equals(server_data_temp)){

                               // tempRef.child("minTemp").setValue(minTemp);
                                Toast.makeText(getActivity(),"Blinds is set to Open",Toast.LENGTH_SHORT).show();

                            }

                            else{
                                Toast.makeText(getActivity(),"Blind is set as it is and didn't hit any of the max/min value from user",Toast.LENGTH_SHORT).show();
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException(); // never ignore errors
                    }
                });

            }
        });

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //comparing the values from the sensors data in realtime database
                //firebase initial setup
                String blindkey = String.valueOf(blindsspinner.getSelectedItem());
                dRef =firebaseDatabase.getReference(blindkey);
                DatabaseReference lightRef = dRef.child("ULight");


                String maxLight = lightmaxET.getText().toString();
                String minLight = lightminET.getText().toString();
                //adding in the database
                if (TextUtils.isEmpty(minLight) && TextUtils.isEmpty(maxLight)) {
                    // if the text fields are empty
                    // then show the below message.
                    Toast.makeText(getActivity(), "Please add some data.", Toast.LENGTH_SHORT).show();
                } else {
                    // else call the method to add
                    // data to our database.

                    addDatatoFirebaseLight(maxLight,minLight);
                    // getDataFromFirebase();

                }

                //michael light code
                lightRef.child("light").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String server_data_light = snapshot.getValue(String.class);
                            if(maxLight.equals(server_data_light)){

                                //lightRef.child("op").setValue("close");
                                Toast.makeText(getActivity(),"Blinds is set to Close",Toast.LENGTH_SHORT).show();

                            }
                            else if(minLight.equals(server_data_light)){

                                //lightRef.child("op").setValue("open");
                                Toast.makeText(getActivity(),"Blinds is set to Open",Toast.LENGTH_SHORT).show();

                            } else{
                                Toast.makeText(getActivity(),"Blind is set as it is and didn't hit any of the max/min value from user",Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException(); // never ignore errors
                    }
                });







                saveData();
            }
        });

        //loadData();
//        updateViews();

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MonitoringViewModel.class);
        // TODO: Use the ViewModel
    }


    public void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


//        editor.putString(TEXT, lighttextView.getText().toString());
//        editor.putBoolean(SWITCH1, lightswitch.isChecked());


        editor.apply();

        Toast.makeText(getActivity(), "Data is saved", Toast.LENGTH_SHORT).show();
    }

//    public void loadData() {
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        text = sharedPreferences.getString(TEXT, "");
//        switchOnOff = sharedPreferences.getBoolean(SWITCH1, false);
//    }

//    public void updateViews() {
//        lighttextView.setText(text);
//        lightswitch.setChecked(switchOnOff);
//
//    }

    private void addDatatoFirebaseTemp(String maxTemp, String minTemp) {
        monitoringInfo.setMaxTemp(maxTemp);
        monitoringInfo.setMinTemp(minTemp);

        dRef.child("UTemp").child("maxTemp").setValue(maxTemp);
        dRef.child("UTemp").child("minTemp").setValue(minTemp);


    }

    private void addDatatoFirebaseLight(String maxLight, String minLight) {
        monitoringInfo.setMaxTemp(maxLight);
        monitoringInfo.setMinTemp(minLight);

        dRef.child("ULight").child("maxLight").setValue(maxLight);
        dRef.child("ULight").child("minLight").setValue(minLight);


    }








}