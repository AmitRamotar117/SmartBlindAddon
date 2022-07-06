package ca.t10.blinddev.it.smartblindaddon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class NewUserActivity extends AppCompatActivity {
    View view;
    EditText name,email,password;
    Button sub;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        name = findViewById(R.id.new_user_name);
        email = findViewById(R.id.new_user_email);
        password = findViewById(R.id.new_user_password);
        sub = findViewById(R.id.new_user_submit);
        back = findViewById(R.id.new_user_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backtoapp = new Intent(NewUserActivity.this,MainActivity.class);
                startActivity(backtoapp);
                finish();
            }
        });


        applySettings();
    }

    public void applySettings(){
        SharedPreferences sharedPreferences = getSharedPreferences("saved", Context.MODE_PRIVATE);

        boolean d = sharedPreferences.getBoolean("dark",false);
        boolean n = sharedPreferences.getBoolean("note",false);
        String t = sharedPreferences.getString("size","");

        if(d){enableDarkMode();}
        if(n){
            BlindNotifications bl = new BlindNotifications(view.getContext());
            //this method will allow developer to create message for notification
            bl.enableNotifications("this is from new user fragment");
            //this function will launch the notification.
            bl.pushNotification();
        }

        if (t.equals("large")){setTextSize(20);}
        if (t.equals("medium")){setTextSize(17);}
        if (t.equals("small")){setTextSize(13);}
    }

    private void enableDarkMode() {
        view.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        name.setTextColor(getResources().getColor(R.color.white));
        name.setHintTextColor(getResources().getColor(R.color.white));

        email.setTextColor(getResources().getColor(R.color.white));
        email.setHintTextColor(getResources().getColor(R.color.white));

        password.setTextColor(getResources().getColor(R.color.white));
        password.setHintTextColor(getResources().getColor(R.color.white));


    }

    public void setTextSize(int size){
        sub.setTextSize(size);
        //TODO
        //add code for spinner when implemented
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this).setTitle(R.string.app_name)
                .setMessage(R.string.leave_app).setIcon(R.drawable.ic_exit)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setNegativeButton(R.string.no,null).show();
    }
}