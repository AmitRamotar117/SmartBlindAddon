package ca.t10.blinddev.it.smartblindaddon.ui.manage;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import java.util.Set;

import ca.t10.blinddev.it.smartblindaddon.BlindNotifications;
import ca.t10.blinddev.it.smartblindaddon.R;

public class ManageFragment extends Fragment {
    private View root;
    private ManageViewModel mViewModel;
    Button delete,add,submit;
    Spinner selectblind;
    EditText loc,bkey,height;

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
        selectblind = root.findViewById(R.id.manage_delete_select);
        loc = root.findViewById(R.id.manage_add_loc);
        bkey = root.findViewById(R.id.manage_add_bkey);
        height = root.findViewById(R.id.manage_add_height);
        loc.setVisibility(View.INVISIBLE);
        bkey.setVisibility(View.INVISIBLE);
        height.setVisibility(View.INVISIBLE);
        selectblind.setVisibility(View.GONE);
        delete.setBackgroundColor(Color.GRAY);
        add.setBackgroundColor(Color.GRAY);
        applySettings();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loc.setVisibility(View.VISIBLE);
                bkey.setVisibility(View.VISIBLE);
                height.setVisibility(View.VISIBLE);
                loc.getText().clear();
                bkey.getText().clear();
                height.getText().clear();
                selectblind.setVisibility(View.GONE);
                delete.setBackgroundColor(Color.GRAY);
                add.setBackgroundColor(Color.WHITE);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loc.setVisibility(View.INVISIBLE);
                bkey.setVisibility(View.INVISIBLE);
                height.setVisibility(View.INVISIBLE);
                selectblind.setVisibility(View.VISIBLE);
                delete.setBackgroundColor(Color.WHITE);
                add.setBackgroundColor(Color.GRAY);
            }
        });
        // here is how to get user owned blinds keys from shared preferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saved",Context.MODE_PRIVATE);
        //data is in here
        Set<String> set = sharedPreferences.getStringSet("blinds_owned",null);
        System.out.println("manage"+ set.toString());

        return root;
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
        //TODO
        //add code for spinner when implemented
    }
    private void enableDarkMode() {
        root.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        loc.setHintTextColor(getResources().getColor(R.color.white));
        bkey.setHintTextColor(getResources().getColor(R.color.white));
        height.setHintTextColor(getResources().getColor(R.color.white));
    }

}