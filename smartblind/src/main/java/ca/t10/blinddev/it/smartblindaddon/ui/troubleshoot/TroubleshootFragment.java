package ca.t10.blinddev.it.smartblindaddon.ui.troubleshoot;
//Amit Punit n01203930
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ca.t10.blinddev.it.smartblindaddon.R;
import ca.t10.blinddev.it.smartblindaddon.databinding.FragmentTroubleshootBinding;

public class TroubleshootFragment extends Fragment {

    private FragmentTroubleshootBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TroubleshootViewModel troubleshootViewModel =
                new ViewModelProvider(this).get(TroubleshootViewModel.class);

        binding = FragmentTroubleshootBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView instruct = root.findViewById(R.id.troubleshoot_instruct);
        ImageView timg = root.findViewById(R.id.troubleshoot_image);
        instruct.setText("Measure height of blind in cm to calibarate the blind");
        instruct.setTextSize(15);
        timg.setImageResource(R.drawable.blinds_mount_measuring_1024x633);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}