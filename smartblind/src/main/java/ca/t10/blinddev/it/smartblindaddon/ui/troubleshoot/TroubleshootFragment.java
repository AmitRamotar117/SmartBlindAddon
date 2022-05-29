package ca.t10.blinddev.it.smartblindaddon.ui.troubleshoot;
//Amit Punit n01203930
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ca.t10.blinddev.it.smartblindaddon.databinding.FragmentTroubleshootBinding;

public class TroubleshootFragment extends Fragment {

    private FragmentTroubleshootBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TroubleshootViewModel troubleshootViewModel =
                new ViewModelProvider(this).get(TroubleshootViewModel.class);

        binding = FragmentTroubleshootBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGallery;
        troubleshootViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}