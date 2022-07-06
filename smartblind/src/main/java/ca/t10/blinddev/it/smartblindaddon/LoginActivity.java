package ca.t10.blinddev.it.smartblindaddon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        sharedPreferences = getSharedPreferences("saved",MODE_PRIVATE);
        SharedPreferences.Editor data = sharedPreferences.edit();

        TextView username = findViewById(R.id.username_txt);
        TextView password = findViewById(R.id.password_txt);
        //View googleSignInOptions = findViewById(R.id.google_signin);
        Button newuser = findViewById(R.id.new_user_btn);
        ImageButton back = findViewById(R.id.new_user_back);
        Button loginBtn = findViewById(R.id.login_btn);
        /*
        googleSignInOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //https://firebase.google.com/docs/auth/android/google-signin
                //use this to implement the authentication
                Toast.makeText(LoginActivity.this, "LOGIN google SUCCESSFULL", Toast.LENGTH_LONG).show();
            }
        });
         */

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().equals("admin") && password.getText().toString().equals("admin"))
                {
                    Toast.makeText(LoginActivity.this, "LOGIN SUCCESSFUL", Toast.LENGTH_LONG).show();
                    startMainActivity();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "LOGIN UNSUCCESSFUL", Toast.LENGTH_LONG).show();
                }
            }
        });

        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (LoginActivity.this, NewUserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backtoapp = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(backtoapp);
                finish();
            }
        });



    }
    public void startMainActivity() {
        Intent intent = new Intent (LoginActivity.this, MainActivity.class);
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
