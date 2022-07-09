package ca.t10.blinddev.it.smartblindaddon.ui.troubleshoot;
//Amit Punit n01203930
//Vyacheslav Perepelytsya n01133953
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import static android.content.ContentValues.TAG;

public class TroubleshootFragment extends Fragment {
    TextView instruct;
    private View root;
    private FragmentTroubleshootBinding binding;
    private Button downloadBtn;
    Boolean downloadedFile = false;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TroubleshootViewModel troubleshootViewModel =
                new ViewModelProvider(this).get(TroubleshootViewModel.class);
        binding = FragmentTroubleshootBinding.inflate(inflater, container, false);
         root = binding.getRoot();

        instruct = root.findViewById(R.id.troubleshoot_instruct);
        ImageView timg = root.findViewById(R.id.troubleshoot_image);

        //Set initial instruction text
        instruct.setText("Please use the spinner or download the full troubleshooting document to resolve your issue");
        instruct.setTextSize(15);
        timg.setImageResource(R.drawable.blinds_mount_measuring_1024x633);

        //Spinner initialization code
        String[] arraySpinner = new String[] {
                "Blinds are not visible/manageable/saved", "Login/Logout not working", "Application Crashes",
                "Blinds do not open/close to the full extent", "Blinds do not move at all"
        };
        Spinner troubleshootSpinner = root.findViewById(R.id.troubleshoot_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        troubleshootSpinner.setAdapter(adapter);

        //get troubleshooting file from button
        downloadBtn = root.findViewById(R.id.troubleshoot_download);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (downloadedFile == true)
                {
                    Toast.makeText(getActivity(), "File Already Downloaded - Please go to downloads/troubleshoot.txt to view it", Toast.LENGTH_SHORT).show();
                }
                else {
                    downloadedFile = true;
                    downloadFile.start();
                    Toast.makeText(getActivity(), "File Downloading to downloads/troubleshoot.txt", Toast.LENGTH_SHORT).show();
                }
                //showFile();
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
    Thread downloadFile = new Thread(new Runnable(){
        @Override
        public void run() {
            try {
                    URL url = new URL("https://drive.google.com/uc?export=download&id=1pUpw6CKkKtC94LFZ4QjKqs_6bgdb63lW");
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    int lenghtOfFile = conexion.getContentLength();
                    InputStream is = url.openStream();
                    File testDirectory = new File(Environment.getExternalStorageDirectory() + "/Download");
                    if (!testDirectory.exists()) {
                        testDirectory.mkdir();
                        Log.e(TAG, "Directory Created.");
                    }
                    FileOutputStream fos = new FileOutputStream(testDirectory + "/Troubleshoot.txt");
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
                        Log.e(TAG, "File Written");
                    }
                    is.close();
                    fos.close();
                } catch(
                Exception e)

                {
                    Log.e("ERROR DOWNLOADING", "Unable to download" + e.getMessage());
                    /*
                    Toast.makeText(getActivity(), "error " + e.toString(), Toast.LENGTH_LONG)
                            .show();
                     */
                }
        }
    });
    // Open the File after Download Pseudocode
    /*
    public void showFile() {
        try {
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/Download/" + "Troubleshoot.txt");
            if (!file.isDirectory())
                file.mkdir();
            MimeTypeMap map = MimeTypeMap.getSingleton();
            String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
            String type = map.getMimeTypeFromExtension(ext);
            if (type == null)
               type = "*COMMENTED OUT/*";

            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.fromFile(file);

            intent.setDataAndType(data, type);

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     */
}