package ca.t10.blinddev.it.smartblindaddon.ui.troubleshoot;
//Amit Punit n01203930
//Vyacheslav Perepelytsya n01133953
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ca.t10.blinddev.it.smartblindaddon.R;
import ca.t10.blinddev.it.smartblindaddon.databinding.FragmentTroubleshootBinding;

public class TroubleshootFragment extends Fragment {
    TextView instruct;
    private FragmentTroubleshootBinding binding;
    private Button downloadBtn;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TroubleshootViewModel troubleshootViewModel =
                new ViewModelProvider(this).get(TroubleshootViewModel.class);

        binding = FragmentTroubleshootBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        instruct = root.findViewById(R.id.troubleshoot_instruct);
        ImageView timg = root.findViewById(R.id.troubleshoot_image);
        instruct.setText("Measure height of blind in cm to calibarate the blind");
        instruct.setTextSize(15);
        timg.setImageResource(R.drawable.blinds_mount_measuring_1024x633);

        //get troubleshooting file from button
        downloadBtn = root.findViewById(R.id.troubleshoot_download);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        applySettings();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        instruct.setTextSize(size);
        //TODO
        //add code for spinner when implemented
    }

}