package ca.t10.blinddev.it.smartblindaddon.ui.schedule;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import ca.t10.blinddev.it.smartblindaddon.R;

public class ScheduleFragment extends Fragment {

    private ScheduleViewModel mViewModel;
    View view;
    Spinner blist;
    Button submit,date,time;
    Switch opt;
    EditText indate,intime;

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule, container, false);

        opt = view.findViewById(R.id.schedule_op);
        indate = view.findViewById(R.id.in_date);
        intime = view.findViewById(R.id.in_time);
        blist = view.findViewById(R.id.schedule_blinds);
        submit = view.findViewById(R.id.schedule_submit);
        date = view.findViewById(R.id.btn_date);
        time = view.findViewById(R.id.btn_time);

        //https://www.journaldev.com/9976/android-date-time-picker-dialog
        //this is how the user will set the time and date
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
        if(n){//function for notification
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

}