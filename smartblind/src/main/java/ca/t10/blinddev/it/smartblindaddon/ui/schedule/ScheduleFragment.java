package ca.t10.blinddev.it.smartblindaddon.ui.schedule;
//Chris Mutuc N01314607
//Amit Punit n01203930
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Set;

import ca.t10.blinddev.it.smartblindaddon.BlindNotifications;
import ca.t10.blinddev.it.smartblindaddon.R;
import ca.t10.blinddev.it.smartblindaddon.Schedule;

public class ScheduleFragment extends Fragment {

    private ScheduleViewModel mViewModel;
    private View view;

    Button submit,date,time;
    Switch opt;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dRef;
    private Schedule scheduleInfo;
    EditText indate,intime;
    TextView retrieveTV;

    private String[] locationKey;
    String temp;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private static final String TAG = "MyActivity";
    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // here is how to get user owned blinds keys from shared preferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saved",Context.MODE_PRIVATE);
        //data is in here
       Set<String> set = sharedPreferences.getStringSet("blinds_owned",null);
        Log.i(TAG,set.toString());
        locationKey =set.toArray(new String[0]);


        opt = view.findViewById(R.id.schedule_op);
        indate = view.findViewById(R.id.in_date);
        intime = view.findViewById(R.id.in_time);

        submit = view.findViewById(R.id.schedule_submit);
        date = view.findViewById(R.id.btn_date);
        time = view.findViewById(R.id.btn_time);
        retrieveTV = view.findViewById(R.id.schedule_location_tv);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, locationKey);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = view.findViewById(R.id.schedule_blinds);
        sItems.setAdapter(adapter);

        firebaseDatabase = FirebaseDatabase.getInstance();


        scheduleInfo = new Schedule();
        date.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Get Current Date
                                        final Calendar c = Calendar.getInstance();
                                        mYear = c.get(Calendar.YEAR);
                                        mMonth = c.get(Calendar.MONTH);
                                        mDay = c.get(Calendar.DAY_OF_MONTH);


                                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                                                new DatePickerDialog.OnDateSetListener() {

                                                    @Override
                                                    public void onDateSet(DatePicker view, int year,
                                                                          int monthOfYear, int dayOfMonth) {

                                                        indate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                                    }
                                                }, mYear, mMonth, mDay);
                                        datePickerDialog.show();
                                    }
                                });
                time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get Current Time
                        final Calendar c = Calendar.getInstance();
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinute = c.get(Calendar.MINUTE);

                        // Launch Time Picker Dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {

                                        intime.setText(hourOfDay + ":" + minute);
                                    }
                                }, mHour, mMinute, true);
                        timePickerDialog.show();
                    }
                });
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String time = intime.getText().toString();
                        String date = indate.getText().toString();
                        String operation;
                        if(opt.isChecked()){
                            operation = "Close";
                        }else{
                            operation = "Open";
                        }


                      sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String blindkey = String.valueOf(sItems.getSelectedItem());
                                dRef =firebaseDatabase.getReference(blindkey);






                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        String blindkey = String.valueOf(sItems.getSelectedItem());
                        dRef =firebaseDatabase.getReference(blindkey);
                        dRef.child("Location").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String lo = snapshot.getValue(String.class);
                                retrieveTV.setText("Location:" + " " + lo);
                                temp = lo;

                                Toast.makeText(getActivity(),lo+"test",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        if (TextUtils.isEmpty(time) && TextUtils.isEmpty(date) && TextUtils.isEmpty(operation)) {
                            // if the text fields are empty
                            // then show the below message.
                            Toast.makeText(getActivity(), "Please add some data.", Toast.LENGTH_SHORT).show();
                        } else {
                            // else call the method to add
                            // data to our database.

                            addDatatoFirebase(operation, time, date,blindkey);
                          // getDataFromFirebase();

                        }
                    }
                });

        applySettings();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        // TODO: Use the ViewModel
    }
    public void applySettings(){
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
    opt.setTextSize(size);
    submit.setTextSize(size);
    date.setTextSize(size);
    time.setTextSize(size);
    }
    private void enableDarkMode() {
        view.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        opt.setTextColor(getResources().getColor(R.color.white));
        intime.setHintTextColor(getResources().getColor(R.color.white));
        indate.setHintTextColor(getResources().getColor(R.color.white));
    }

   private void addDatatoFirebase(String type, String time, String date, String blindkey ) {

       scheduleInfo.setDate(date);
       scheduleInfo.setOperation(type);
       scheduleInfo.setTime(time);


       dRef.child("Schedule").child("date").setValue(date);
       dRef.child("Schedule").child("time").setValue(time);
       dRef.child("Schedule").child("operation").setValue(type);




   }






}