package ca.t10.blinddev.it.smartblindaddon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    Switch portrait, notification,dark;
    RadioGroup textsize;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
        sharedPreferences = getSharedPreferences("saved",MODE_PRIVATE);
        SharedPreferences.Editor data = sharedPreferences.edit();

        Button applyBtn = findViewById(R.id.apply_settings_button);
        textsize = findViewById(R.id.text_size);
        portrait = findViewById(R.id.settings_portrait_mode);
        notification = findViewById(R.id.settings_notification_mode);
        dark = findViewById(R.id.settings_dark_mode);

        //sets current setting form user
        currentSettings();

        //this is the radiobutton group to choose the size of the text in the app
        textsize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selected = textsize.getCheckedRadioButtonId();
                switch (selected){
                    case R.id.small_text:
                        //
                        data.putString("size","small");
                        Toast.makeText(getApplicationContext(),R.string.small,Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.medium_text:
                        //
                        data.putString("size","medium");
                        Toast.makeText(getApplicationContext(),R.string.medium,Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.large_text:
                        //
                        data.putString("size","large");
                        Toast.makeText(getApplicationContext(),R.string.large,Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });

        //if enable the app will be locked in portrait mode
        portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(portrait.isChecked()){
                    data.putBoolean("portrait",true);
                    Toast.makeText(getApplicationContext(), R.string.portriat_enable,Toast.LENGTH_SHORT).show();
                }else {
                    data.putBoolean("portrait",false);
                    Toast.makeText(getApplicationContext(), R.string.portrait_disable,Toast.LENGTH_SHORT).show();
                }
                //data.commit();
            }
        });

        //if check then the app will display notifications
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notification.isChecked()){
                    data.putBoolean("note",true);
                    Toast.makeText(getApplicationContext(), R.string.notif_enable,Toast.LENGTH_SHORT).show();
                }else{
                    data.putBoolean("note",false);
                    Toast.makeText(getApplicationContext(), R.string.notif_disable,Toast.LENGTH_SHORT).show();
                }
            }
        });

        //if checked then the color for layout will be dark.
        dark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dark.isChecked()){
                    data.putBoolean("dark",true);
                    Toast.makeText(getApplicationContext(), R.string.dark_mode_enabled,Toast.LENGTH_SHORT).show();
                }else{
                    data.putBoolean("dark",false);
                    Toast.makeText(getApplicationContext(), R.string.dark_mode_disabled,Toast.LENGTH_SHORT).show();
                }
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                data.commit();
                startMainActivity();
            }
        });
    }

    private void currentSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("saved", Context.MODE_PRIVATE);
        RadioButton l,m,s;
        l = findViewById(R.id.large_text);
        m = findViewById(R.id.medium_text);
        s = findViewById(R.id.small_text);
        boolean p = sharedPreferences.getBoolean("portrait",false);
        boolean d = sharedPreferences.getBoolean("dark",false);
        boolean n = sharedPreferences.getBoolean("note",false);
        String t = sharedPreferences.getString("size","");

        if(p){portrait.setChecked(true);}
        if(d){dark.setChecked(true);}
        if(n){notification.setChecked(true);}

        //this will select the appropriate radio the user saves
        if (t.equals("large")){l.setChecked(true);}
        if (t.equals("medium")){m.setChecked(true);}
        if (t.equals("small")){s.setChecked(true);}



    }

    public void startMainActivity() {
        Intent intent = new Intent (SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this).setTitle(R.string.app_name)
                .setMessage(R.string.leave_app).setIcon(R.drawable.ic_exit)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> finish()).setNegativeButton(R.string.no,null).show();
    }
}
