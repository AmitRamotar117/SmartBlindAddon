package ca.t10.blinddev.it.smartblindaddon.ui.monitoring;
//Chris Mutuc n01314607
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.t10.blinddev.it.smartblindaddon.R;

public class MonitoringFragment extends Fragment {

    private MonitoringViewModel mViewModel;

    public static MonitoringFragment newInstance() {
        return new MonitoringFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_monitoring, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MonitoringViewModel.class);
        // TODO: Use the ViewModel
    }

}