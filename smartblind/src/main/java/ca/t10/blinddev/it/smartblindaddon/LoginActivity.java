package ca.t10.blinddev.it.smartblindaddon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    ImageView google_img;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        TextView username = findViewById(R.id.username_txt);
        TextView password = findViewById(R.id.password_txt);

        google_img = (ImageView) findViewById(R.id.google_signin);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc= GoogleSignIn.getClient(this, gso);

        // if sign in with google is pressed


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
        loginBtn = findViewById(R.id.login_btn);
         //if login btn is pressed
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

    }
    public void startMainActivity()
    {
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
