package ca.t10.blinddev.it.smartblindaddon.ui.contact;
//Amit Punit n01203930
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;

import ca.t10.blinddev.it.smartblindaddon.R;
import ca.t10.blinddev.it.smartblindaddon.databinding.FragmentContactBinding;


public class ContactFragment extends Fragment {

    private ListView listView;

    private FragmentContactBinding binding;
    private Button permissionBtn;
    public static final int REQUEST_CALLS = 1;
    private EditText mEditText;
    private EditText feedBack,nameText,emailText,phoneText;

    private Button submitBtn;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ContactViewModel contactViewModel =
                new ViewModelProvider(this).get(ContactViewModel.class);

        binding = FragmentContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mEditText = root.findViewById(R.id.editText);
        feedBack = root.findViewById(R.id.feedbackText);
        nameText = root.findViewById(R.id.nameText);
        emailText = root.findViewById(R.id.emailText);
        phoneText = root.findViewById(R.id.phoneText);
        submitBtn = root.findViewById(R.id.submitButton);

        permissionBtn = root.findViewById(R.id.dialerButton);
        listView = root.findViewById(R.id.devsList);
        ArrayList<String> arrayList =  new ArrayList<>();

        arrayList.add("Slide down");
        arrayList.add("Developer's Contact");
        arrayList.add("chrisjanellemutuc@gmail.com");
        arrayList.add("amitpunit117@gmail.com");
        arrayList.add("gazaboy1001@gmail.com");
        arrayList.add("vypere1994@gmail.com");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Firebase mFirebaseRef =
                        new Firebase("https://smartblindaddon-default-rtdb.firebaseio.com/some/path");

                mFirebaseyRef.setValue(feedBack.getText().toString());*/
            }
        });







      permissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });

        final TextView textView = binding.textSlideshow;
        contactViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);



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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
   if (requestCode == REQUEST_CALLS){
       if (grantResults.length>0&& grantResults[0]== PackageManager.PERMISSION_GRANTED){
           makePhoneCall();
       }else{
           denied();
       }
   }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}