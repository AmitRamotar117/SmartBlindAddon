package ca.t10.blinddev.it.smartblindaddon;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
        Button applyBtn = (Button) findViewById(R.id.apply_settings_button);
    applyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startMainActivity();
            }
        });
    }
    public void startMainActivity()
    {
        Intent intent = new Intent (SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
