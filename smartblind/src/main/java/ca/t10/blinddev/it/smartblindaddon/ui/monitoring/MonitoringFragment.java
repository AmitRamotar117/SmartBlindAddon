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
    //michael
    private TextView lighttextView, retrieveTV;
    private ProgressBar lightprogressBar;
    private SeekBar lightseekBar;
    Switch lightswitch;
    Button submitbutton;
    Spinner blindsspinner;
    private Monitoring monitoringInfo;
    private EditText maxET, minET;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dRef;


    public static final String TEXT = "text";
    public static final String SWITCH1 = "switch1";
    public static final String PROGRESS1 = "progress1";
    private String text;
    private boolean switchOnOff;
    private boolean progress1;
    private String[] locationKey;
    private static final String TAG = "MyActivity";
    public static final String SHARED_PREFS = "sharedPrefs";
    String temp;


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
        maxET = view.findViewById(R.id.maxET);
        minET = view.findViewById(R.id.minET);

        retrieveTV = view.findViewById(R.id.retrieveLocation);

        blindsspinner = view.findViewById(R.id.blindsspinner);
        lightswitch = view.findViewById(R.id.opencloseswitch);


        submitbutton = view.findViewById(R.id.submitbutton);
        firebaseDatabase = FirebaseDatabase.getInstance();

        lighttextView =  view.findViewById(R.id.progresstextView);
        lightprogressBar = view.findViewById(R.id.lightprogressBar);
        lightseekBar = view.findViewById(R.id.lightseekBar);
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










        lightswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // do something when check is selected

                    Toast.makeText(getActivity(),"Blinds is set to Open",Toast.LENGTH_SHORT).show();

                } else {
                    //do something when unchecked
                    Toast.makeText(getActivity(),"Blinds is set to Close",Toast.LENGTH_SHORT).show();
                }
            }
        });
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

                        Toast.makeText(getActivity(),lo+" ",Toast.LENGTH_SHORT).show();
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


        lightseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {



                lightprogressBar.setProgress(progress);

                lighttextView.setText("" + progress + "%");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        submitbutton.setOnClickListener(new View.OnClickListener() {
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
                //
                tempRef.child("temp").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String server_data_temp = snapshot.getValue(String.class);
                            if(maxTemp.equals(server_data_temp)){

                                tempRef.child("op").setValue("close");
                                Toast.makeText(getActivity(),"Blinds is set to Close",Toast.LENGTH_SHORT).show();



                            }
                            else if(minTemp.equals(server_data_temp)){

                                tempRef.child("op").setValue("open");
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

                saveData();
            }
        });

        //loadData();
        updateViews();

        return view;
    }
    /*public void applySettings(){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saved", Context.MODE_PRIVATE);

        boolean d = sharedPreferences.getBoolean("dark",false);
        boolean n = sharedPreferences.getBoolean("note",false);
        String t = sharedPreferences.getString("size","");

        if(d){enableDarkMode();}
        if(n){
            BlindNotifications bl = new BlindNotifications(view.getContext());
            //this method will allow developer to create message for notification
            bl.enableNotifications("this is from schedule fragment");
            //this function will launch the notification.
            bl.pushNotification();
        }

        if (t.equals("large")){setTextSize(20);}
        if (t.equals("medium")){setTextSize(17);}
        if (t.equals("small")){setTextSize(13);}
    }
    public void setTextSize(int size){
        /*object needs to be put in and replace the existing
       /* opt.setTextSize(size);
        submit.setTextSize(size);
        date.setTextSize(size);
        time.setTextSize(size);
        retrieveTV.setTextSize(size);
    }
   /* private void enableDarkMode() {
        /*object needs to be put in and replace the existing
        view.setBackgroundColor(getResources().getColor(R.color.dark_grey,null));
       opt.setTextColor(getResources().getColor(R.color.white,null));
        title.setTextColor(getResources().getColor(R.color.white,null));
        retrieveTV.setTextColor(getResources().getColor(R.color.white,null));
        intime.setHintTextColor(getResources().getColor(R.color.white,null));
        indate.setHintTextColor(getResources().getColor(R.color.white,null));
    }*/





    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MonitoringViewModel.class);
        // TODO: Use the ViewModel
    }


    public void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        editor.putString(TEXT, lighttextView.getText().toString());
        editor.putBoolean(SWITCH1, lightswitch.isChecked());


        editor.apply();

        Toast.makeText(getActivity(), "Data is saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT, "");
        switchOnOff = sharedPreferences.getBoolean(SWITCH1, false);
    }

    public void updateViews() {
        lighttextView.setText(text);
        lightswitch.setChecked(switchOnOff);

    }







}