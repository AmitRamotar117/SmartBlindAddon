package ca.t10.blinddev.it.smartblindaddon.ui.manage;

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
import android.widget.Spinner;

import ca.t10.blinddev.it.smartblindaddon.R;

public class ManageFragment extends Fragment {

    private ManageViewModel mViewModel;
    Button delete,add,submit;
    Spinner selectblind;

    public static ManageFragment newInstance() {
        return new ManageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_manage, container, false);
        delete = root.findViewById(R.id.manage_delete_btn);
        add = root.findViewById(R.id.manage_add_btn);
        submit = root.findViewById(R.id.manage_submit_btn);
        selectblind = root.findViewById(R.id.manage_delete_select);

        applySettings();
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

        Boolean d = sharedPreferences.getBoolean("dark",false);
        Boolean n = sharedPreferences.getBoolean("note",false);
        String t = sharedPreferences.getString("size","");

        if(d == true){//function for dark mode
        }
        if(n == true){//function for notification
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

}