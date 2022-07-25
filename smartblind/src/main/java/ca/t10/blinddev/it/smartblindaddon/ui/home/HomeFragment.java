package ca.t10.blinddev.it.smartblindaddon.ui.home;
//Amit Punit n01203930
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.util.HashSet;
import java.util.Set;

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
    Set<String> blindsowned = new HashSet<>();
    TextView error;

    //testblinds@mail.com
    //password1#



    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        applySettings();
        error = root.findViewById(R.id.homepage_error);

        //use this to check if app is connected to internet
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) root.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else{
            connected = false;
            error.setText(R.string.no_internet);
            error.setVisibility(TextView.VISIBLE);
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saved",Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("user_key","");

        if(userID.equals("")){
            error.setText(R.string.no_user);
            error.setVisibility(TextView.VISIBLE);
        }


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Owned");
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

                // this code will save the users owned blind keys so they can be used by other functions
                //like the manage blinds and schedule blinds
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saved",Context.MODE_PRIVATE);
                SharedPreferences.Editor data = sharedPreferences.edit();
                data.putStringSet("blinds_owned",blindsowned);
                data.commit();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        testcase.clear();
        blindsowned.clear();
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