package ca.t10.blinddev.it.smartblindaddon.ui.distance;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.t10.blinddev.it.smartblindaddon.R;


public class DistanceFragment extends Fragment {
    private View view;
    private TextView currentDistance;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_distance, container, false);
        currentDistance = view.findViewById(R.id.distancereadings);
        myRef = database.getReference("currentdistance");

      myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String server_data_distance = String.valueOf(snapshot.getValue());

                currentDistance.setText("Current Distance: "+server_data_distance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

        return view;
    }






}