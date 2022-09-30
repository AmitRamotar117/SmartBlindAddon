package ca.t10.blinddev.it.smartblindaddon.ui.monitoring;
//Chris Mutuc n01314607
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


import ca.t10.blinddev.it.smartblindaddon.BlindNotifications;
import ca.t10.blinddev.it.smartblindaddon.R;



public class MonitoringFragment extends Fragment {
    private View view;
    private MonitoringViewModel mViewModel;

    public static MonitoringFragment newInstance() {
        return new MonitoringFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_monitoring, container, false);
        applySettings();
        return view;
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
       /* opt.setTextSize(size);
        submit.setTextSize(size);
        date.setTextSize(size);
        time.setTextSize(size);
        retrieveTV.setTextSize(size);*/
    }
    private void enableDarkMode() {
        view.setBackgroundColor(getResources().getColor(R.color.dark_grey,null));
      /*  opt.setTextColor(getResources().getColor(R.color.white,null));
        title.setTextColor(getResources().getColor(R.color.white,null));
        retrieveTV.setTextColor(getResources().getColor(R.color.white,null));
        intime.setHintTextColor(getResources().getColor(R.color.white,null));
        indate.setHintTextColor(getResources().getColor(R.color.white,null));*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MonitoringViewModel.class);
        // TODO: Use the ViewModel
    }

}