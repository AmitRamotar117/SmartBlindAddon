package ca.t10.blinddev.it.smartblindaddon.ui.home;
//Amit Punit n01203930
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.t10.blinddev.it.smartblindaddon.BlindNotifications;
import ca.t10.blinddev.it.smartblindaddon.HomeBlinds;
import ca.t10.blinddev.it.smartblindaddon.HomeRecyclerViewAdapter;
import ca.t10.blinddev.it.smartblindaddon.R;
import ca.t10.blinddev.it.smartblindaddon.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private View root;
    private FragmentHomeBinding binding;
    RecyclerView recyclerView;

    HomeRecyclerViewAdapter homeRecyclerViewAdapter;
    ArrayList<HomeBlinds> testcase = new ArrayList<>();
    ArrayList<String> blindsowned = new ArrayList<>();
    DatabaseReference ref;



    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        applySettings();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Admin").child("Owned");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String blinds = dataSnapshot.getValue().toString();
                    testcase.add(new HomeBlinds(blinds));
                    blindsowned.add(blinds);
                    recyclerView =  root.findViewById(R.id.home_recycler_view);
                    homeRecyclerViewAdapter = new HomeRecyclerViewAdapter(testcase,getContext());
                    recyclerView.setAdapter(homeRecyclerViewAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

       SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saved",Context.MODE_PRIVATE);
        SharedPreferences.Editor data = sharedPreferences.edit();
        //data.putStringSet("blinds_owned",blindsowned);

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
        boolean d = sharedPreferences.getBoolean("dark",false);
        boolean n = sharedPreferences.getBoolean("note",false);
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