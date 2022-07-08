package ca.t10.blinddev.it.smartblindaddon.ui.contact;
//Amit Punit n01203930
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;

import ca.t10.blinddev.it.smartblindaddon.BlindNotifications;
import ca.t10.blinddev.it.smartblindaddon.R;
import ca.t10.blinddev.it.smartblindaddon.databinding.FragmentContactBinding;


public class ContactFragment extends Fragment {

    private ListView listView;
    private View root;
    private FragmentContactBinding binding;
    private Button permissionBtn;
    public static final int REQUEST_CALLS = 1;
    private EditText mEditText;
    private Firebase Ref;
    private EditText feedBack,nameText,emailText,phoneText;
    private String[]emails;

    private Button submitBtn;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       // ContactViewModel contactViewModel =
               // new ViewModelProvider(this).get(ContactViewModel.class);

        binding = FragmentContactBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        mEditText = root.findViewById(R.id.contactEditText);
        feedBack = root.findViewById(R.id.contactFeedbackText);
        nameText = root.findViewById(R.id.contactNameText);
        emailText = root.findViewById(R.id.contactEmailText);
        phoneText = root.findViewById(R.id.contactPhoneText);
        submitBtn = root.findViewById(R.id.contactSubmitButton);
        Resources res = getResources();
        emails = res.getStringArray(R.array.contact_emails);




        permissionBtn = root.findViewById(R.id.contactDialerButton);
        listView = root.findViewById(R.id.contactDevsList);
        //ArrayList<String> arrayList =  new ArrayList<>();



        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),R.layout.list_view , emails);
        listView.setAdapter(arrayAdapter);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Firebase mFirebaseRef = new Firebase("https://smartblindaddon-default-rtdb.firebaseio.com/Issue/User");


                ShowDialog();
            }
        });
        permissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });

        /*final TextView textView = binding.textSlideshow;
        contactViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
*/

        applySettings();
        return root;
    }
    private void phoneNumber() {

        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(android.R.id.content), "Enter Phone Number", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
    private void denied(){
        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(android.R.id.content), "Access Denied", Snackbar.LENGTH_LONG);
        snackbar.show();

    }
    private void makePhoneCall(){
        String number = mEditText.getText().toString();
        if (number.trim().length() >0){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALLS);
              } else {
               String dial = "tel:" + number;
               startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        }else{
          phoneNumber();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
   if (requestCode == REQUEST_CALLS){
       if (grantResults.length>0&& grantResults[0]== PackageManager.PERMISSION_GRANTED){
           makePhoneCall();
       }else{
           denied();
       }
   }
    }
//creates a rating star dialog
    public void ShowDialog() {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(getActivity());

        LinearLayout linearLayout = new LinearLayout(getActivity());
        final RatingBar rating = new RatingBar(getActivity());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rating.setLayoutParams(lp);
        rating.setNumStars(5);
        rating.setStepSize(1);

        //add ratingBar to linearLayout
        linearLayout.addView(rating);


        popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("Add Rating: ");

        //add linearLayout to dialog
        popDialog.setView(linearLayout);



        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                System.out.println("Rated val:"+v);
            }
        });



        // Button OK
        popDialog.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               // textView.setText(String.valueOf(rating.getProgress()));
                                dialog.dismiss();
                            }

                        })

                // Button Cancel
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        popDialog.create();
        popDialog.show();

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
            bl.enableNotifications("this is from contact fragment");
            //this function will launch the notification.
            bl.pushNotification();
        }

        if (t.equals("large")){setTextSize(20);}
        if (t.equals("medium")){setTextSize(17);}
        if (t.equals("small")){setTextSize(13);}
    }
    public void setTextSize(int size){
        //listView.setTextSize(size);
        permissionBtn.setTextSize(size);
        submitBtn.setTextSize(size);
    }
    private void enableDarkMode() {
        root.setBackgroundColor(getResources().getColor(R.color.dark_grey));

        nameText.setHintTextColor(getResources().getColor(R.color.white));
        nameText.setTextColor(getResources().getColor(R.color.white));

        emailText.setHintTextColor(getResources().getColor(R.color.white));
        emailText.setTextColor(getResources().getColor(R.color.white));

        phoneText.setHintTextColor(getResources().getColor(R.color.white));
        phoneText.setTextColor(getResources().getColor(R.color.white));

        feedBack.setHintTextColor(getResources().getColor(R.color.white));
        feedBack.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}