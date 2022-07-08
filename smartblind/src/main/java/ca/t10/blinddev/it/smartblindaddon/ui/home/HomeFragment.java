package ca.t10.blinddev.it.smartblindaddon.ui.home;
//Amit Punit n01203930
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import ca.t10.blinddev.it.smartblindaddon.BlindNotifications;
import ca.t10.blinddev.it.smartblindaddon.HomeBlinds;
import ca.t10.blinddev.it.smartblindaddon.HomeRecyclerViewAdapter;
import ca.t10.blinddev.it.smartblindaddon.MainActivity;
import ca.t10.blinddev.it.smartblindaddon.R;
import ca.t10.blinddev.it.smartblindaddon.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private View root;
    private FragmentHomeBinding binding;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    HomeRecyclerViewAdapter homeRecyclerViewAdapter;
    ArrayList<HomeBlinds> testcase = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        // these are test cased for the blinds that will be appear in the homepage
        testcase.add(new HomeBlinds("Amit"));
        testcase.add(new HomeBlinds("punit"));


        // this is code that is used to populate the recyclerview for the blinds
        recyclerView = (RecyclerView) root.findViewById(R.id.home_recycler_view);
        homeRecyclerViewAdapter = new HomeRecyclerViewAdapter(testcase,getContext());
        recyclerView.setAdapter(homeRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        applySettings();
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        testcase.clear();
        binding = null;
    }
    public void applySettings(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saved", Context.MODE_PRIVATE);
        Boolean d = sharedPreferences.getBoolean("dark",false);
        Boolean n = sharedPreferences.getBoolean("note",false);
        if(d){root.setBackgroundColor(getResources().getColor(R.color.dark_grey));}
        if(n){
            //create class that has functions that make the blind
            BlindNotifications bl = new BlindNotifications(root.getContext());
            //this method will allow developer to create message for notification
            bl.enableNotifications("this is from home fragment");
            //this function will launch the notification.
            bl.pushNotification();
             }
    }

}