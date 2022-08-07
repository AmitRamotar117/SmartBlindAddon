package ca.t10.blinddev.it.smartblindaddon.ui.troubleshoot;
//Amit Punit n01203930
//Vyacheslav Perepelytsya n01133953
//Chris Mutuc n01314607
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
    TextView title;
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
        title = root.findViewById(R.id.troubleshoot_title);
        instruct = root.findViewById(R.id.troubleshoot_instruct);
        instruct.setMovementMethod(new ScrollingMovementMethod());
        ImageView timg = root.findViewById(R.id.troubleshoot_image);

        applySettings();

        //Set initial instruction text
        instruct.setText("Please use the spinner or download the full troubleshooting document to resolve your issue");
        instruct.setTextSize(20);
        timg.setImageResource(R.drawable.blinds_mount_measuring_1024x633);

        //Spinner initialization code

        String[] arraySpinner = new String[] { "Please select your issue...",
                "Blinds are not visible/manageable/saved", "Login/Logout not working", "Application Crashes",
                "Blinds do not open/close to the full extent", "Blinds do not move at all"
        };
        Spinner troubleshootSpinner = root.findViewById(R.id.troubleshoot_options);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saved", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark",true)){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                    R.layout.spinner_style, arraySpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            troubleshootSpinner.setAdapter(adapter);
        }
        else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_spinner_item, arraySpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            troubleshootSpinner.setAdapter(adapter);
        }

        //Spinner selection code

        troubleshootSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                String selItem = troubleshootSpinner.getItemAtPosition(arg2).toString();

                if(selItem.equals("Please select your issue..."))
                {
                    instruct.setText("Please use the spinner or download the full troubleshooting document to find and resolve your issue");
                }

                if(selItem.equals("Blinds are not visible/manageable/saved"))
                {
                    instruct.setText("1. Please ensure you are connected to the internet\n" +
                            "2. If issue persists you can contact support in our contact us page");
                }
                else if(selItem.equals("Login/Logout not working"))
                {
                    instruct.setText("1. Please ensure you are a registered user\n" +
                            "2. Make sure you are connected to the internet");
                }
                else if(selItem.equals("Application Crashes"))
                {
                    instruct.setText("1. Please ensure that you are using a compatible version of Android and update your version if it is outdated\n" +
                            "2. Try restarting the phone and running the app again\n" +
                            "3. Check and your phone for malware and/or any intrusive or overarching apps, close them and clean your phone of malware before starting the application\n" +
                            "4. If your phone is configured in a language other than English or French try running from an English or French Android configuration.\n");
                }
                else if(selItem.equals("Blinds do not open/close to the full extent"))
                {
                    instruct.setText("1. Measure the height of the blind in cm and enter it to calibrate the blind\n" +
                            "2. Try deleting and adding the blind in question again");
                }
                else if(selItem.equals("Blinds do not move at all"))
                {
                    instruct.setText("1. Please ensure the blind is connected to the motor\n" +
                            "2. Make sure you are connected to the internet when sending operations\n" +
                            "3. Measure the height of the blind in cm and enter it to calibrate the blind\n" +
                            "4. Try deleting and adding the blind in question again");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                instruct.setText("Please use the spinner or download the full troubleshooting document to find and resolve your issue");
            }
        });

        //Get troubleshooting file from button
        downloadBtn = root.findViewById(R.id.troubleshoot_download);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (downloadedFile)
                {
                    Toast.makeText(getActivity(), "File Downloaded - Please open downloads/troubleshoot.txt to view it", Toast.LENGTH_SHORT).show();
                }
                else {
                    downloadedFile = true;
                    downloadFile.start();
                    Toast.makeText(getActivity(), "File Downloading to downloads/troubleshoot.txt", Toast.LENGTH_SHORT).show();

                }
                showFile();
            }
        });

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
        root.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        instruct.setTextColor(getResources().getColor(R.color.white));
        title.setTextColor(getResources().getColor(R.color.white));
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
    // Open the File after Download

    public void showFile() {
        try {
            File file = new File(Environment.getDataDirectory()
                    + "/Download" + "/Troubleshoot.txt");
            if (!file.isDirectory())
                file.mkdir();
            /*

            // This would be the more specific function to map to the file type
            // A mime type of all types is used rn for maximizing applications for the user to choose
            
            MimeTypeMap map = MimeTypeMap.getSingleton();
            String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
            String type = map.getMimeTypeFromExtension(ext);
            if (type == null)
               type = "*COMMENTED OUT/*";
            */

            Uri data = Uri.fromFile(file);

            // Open file with user selected app
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setData(data);
            intent.setType("*/*");
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}