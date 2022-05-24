package ca.t10.blinddev.it.smartblindaddon.ui.troubleshoot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TroubleshootViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TroubleshootViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is troubleshooting fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}