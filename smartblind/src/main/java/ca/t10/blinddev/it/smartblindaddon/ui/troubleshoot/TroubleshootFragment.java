package ca.t10.blinddev.it.smartblindaddon.ui.troubleshoot;
//Amit Punit n01203930
//Vyacheslav Perepelytsya n01133953
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ca.t10.blinddev.it.smartblindaddon.BlindNotifications;
import ca.t10.blinddev.it.smartblindaddon.R;
import ca.t10.blinddev.it.smartblindaddon.databinding.FragmentTroubleshootBinding;

public class TroubleshootFragment extends Fragment {
    TextView instruct;
    private View root;
    private FragmentTroubleshootBinding binding;
    private Button downloadBtn;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TroubleshootViewModel troubleshootViewModel =
                new ViewModelProvider(this).get(TroubleshootViewModel.class);
        binding = FragmentTroubleshootBinding.inflate(inflater, container, false);
         root = binding.getRoot();

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
                downloadFile();
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

        boolean d = sharedPreferences.getBoolean("dark",false);
        boolean n = sharedPreferences.getBoolean("note",false);
        String t = sharedPreferences.getString("size","");

        if(d){enableDarkMode();}
        if(n){
            BlindNotifications bl = new BlindNotifications(root.getContext());
            //this method will allow developer to create message for notification
            bl.enableNotifications("this is from troubleshooting fragment");
            //this function will launch the notification.
            bl.pushNotification();
             }

        if (t.equals("large")){setTextSize(20);}
        if (t.equals("medium")){setTextSize(17);}
        if (t.equals("small")){setTextSize(13);}
    }

    private void enableDarkMode() {
        TextView title = root.findViewById(R.id.troubleshoot_title);
        title.setTextColor(getResources().getColor(R.color.white));
        root.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        instruct.setTextColor(getResources().getColor(R.color.white));
    }

    public void setTextSize(int size){
        instruct.setTextSize(size);
        //TODO
        //add code for spinner when implemented
    }
    public void downloadFile() {
        try {
            URL url = new URL("https://github.com/AmitPunit3930/SmartBlindAddon/blob/master/README.md");
            URLConnection conexion = url.openConnection();
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();
            InputStream is = url.openStream();
            File testDirectory = new File(Environment.getExternalStorageDirectory() + "/Download");
            if (!testDirectory.exists()) {
                testDirectory.mkdir();
            }
            FileOutputStream fos = new FileOutputStream(testDirectory + "/products.txt");
            byte[] data = new byte[1024];
            long total = 0;
            int count = 0;
            while ((count = is.read(data)) != -1) {
                total += count;
                int progress_temp = (int) total * 100 / lenghtOfFile;
        /*publishProgress("" + progress_temp); //only for asynctask
        if (progress_temp % 10 == 0 && progress != progress_temp) {
            progress = progress_temp;
        }*/
                fos.write(data, 0, count);
            }
            Toast.makeText(getActivity(), "File is Downloading", Toast. LENGTH_SHORT).show();
            is.close();
            fos.close();
        } catch (Exception e) {
            Log.e("ERROR DOWNLOADING", "Unable to download" + e.getMessage());
            Toast.makeText(getActivity(), "error " + e.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }
}