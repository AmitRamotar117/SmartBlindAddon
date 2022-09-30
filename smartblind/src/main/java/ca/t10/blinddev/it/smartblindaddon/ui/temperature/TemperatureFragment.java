package ca.t10.blinddev.it.smartblindaddon.ui.temperature;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.t10.blinddev.it.smartblindaddon.R;

public class TemperatureFragment extends Fragment {

    private TemperatureViewModel mViewModel;

    public static TemperatureFragment newInstance() {
        return new TemperatureFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temperature, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TemperatureViewModel.class);
        // TODO: Use the ViewModel
    }

}