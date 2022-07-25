package ca.t10.blinddev.it.smartblindaddon.ui.contact;
//Amit Punit n01203930
//Chris Mutuc N01314607
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.t10.blinddev.it.smartblindaddon.BlindNotifications;
import ca.t10.blinddev.it.smartblindaddon.Contact;
import ca.t10.blinddev.it.smartblindaddon.R;
import ca.t10.blinddev.it.smartblindaddon.databinding.FragmentContactBinding;


public class ContactFragment extends Fragment {

    private ListView listView;
    private View root;
    private FragmentContactBinding binding;

    public static final int REQUEST_CALLS = 1;
    private EditText mEditText;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dRef;
    private EditText feedBack,nameText,emailText,phoneText;
    private String[]emails;
    private Contact contactInfo;
    private FloatingActionButton fab;
    private Button submitBtn;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentContactBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        mEditText = root.findViewById(R.id.contactEditText);
        feedBack = root.findViewById(R.id.contactFeedbackText);
        nameText = root.findViewById(R.id.contactNameText);
        emailText = root.findViewById(R.id.contactEmailText);
        phoneText = root.findViewById(R.id.contactPhoneText);
        submitBtn = root.findViewById(R.id.contactSubmitButton);
        fab = root.findViewById(R.id.fab);
        Resources res = getResources();
        emails = res.getStringArray(R.array.contact_emails);
        firebaseDatabase = FirebaseDatabase.getInstance();
        dRef =firebaseDatabase.getReferenceFromUrl("https://smartblindaddon-default-rtdb.firebaseio.com/Issue/User");

        contactInfo = new Contact();




        listView = root.findViewById(R.id.contactDevsList);
        //ArrayList<String> arrayList =  new ArrayList<>();



        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),R.layout.list_view , emails);
        listView.setAdapter(arrayAdapter);



        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             String comment = feedBack.getText().toString();
             String name = nameText.getText().toString();
             String email = emailText.getText().toString();
             String phone = phoneText.getText().toString();

                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(phone) && TextUtils.isEmpty(comment)&& TextUtils.isEmpty(email)) {
                    // if the text fields are empty
                    // then show the below message.
                    Toast.makeText(getActivity(), "Please add some data.", Toast.LENGTH_SHORT).show();
                } else {
                    // else call the method to add
                    // data to our database.
                    addDatatoFirebase(name, phone, email, comment);
                }

               ShowDialog();
            }
        });
    fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            makePhoneCall();
        }
    });



        applySettings();
        return root;
    }
    private void addDatatoFirebase(String name, String phone, String email, String comment) {

      contactInfo.setEmail(email);
      contactInfo.setComment(comment);
      contactInfo.setName(name);
      contactInfo.setPhone(phone);


        // we are use add value event listener method
        // which is called with database reference.
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
               dRef.setValue(contactInfo);

                // after adding this data we are showing toast message.
                Toast.makeText(getActivity(), "data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(getActivity(), "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
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