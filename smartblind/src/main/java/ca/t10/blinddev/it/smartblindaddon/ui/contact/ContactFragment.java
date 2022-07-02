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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import ca.t10.blinddev.it.smartblindaddon.R;
import ca.t10.blinddev.it.smartblindaddon.databinding.FragmentContactBinding;


public class ContactFragment extends Fragment {

    private ListView listView;

    private FragmentContactBinding binding;
    private Button permissionBtn;
    public static final int PERMISSIONS_REQUEST_READ_STORAGE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ContactViewModel contactViewModel =
                new ViewModelProvider(this).get(ContactViewModel.class);

        binding = FragmentContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        permissionBtn = root.findViewById(R.id.dialerButton);
        listView = root.findViewById(R.id.devsList);
        ArrayList<String> arrayList =  new ArrayList<>();

        arrayList.add("Developer's Contact");
        arrayList.add("chrisjanellemutuc@gmail.com");
        arrayList.add("amitpunit117@gmail.com");
        arrayList.add("gazaboy1001@gmail.com");
        arrayList.add("vypere1994@gmail.com");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);


      /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(this,"email clicked"+i+" "+arrayList.get(i).toString(),Toast.LENGTH_LONG).show();
            }
        });*/




      permissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
            }
        });

        final TextView textView = binding.textSlideshow;
        contactViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);





        return root;
    }
    private void getAccess() {

        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(android.R.id.content), "Access Granted", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void denied(){
        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(android.R.id.content), "Denied", Snackbar.LENGTH_LONG);
        snackbar.show();

    }

    //request permission to open contacts
    public void requestStoragePermission() {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.permission_storage_title);
                    builder.setPositiveButton(R.string.enable, null);
                    builder.setMessage(R.string.prompt1);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_EXTERNAL_STORAGE}
                                    , PERMISSIONS_REQUEST_READ_STORAGE);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_READ_STORAGE);
                }
            } else {
                getAccess();
            }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAccess();
                } else {
                          denied();
                }
                return;
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}