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


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn, registerBtn;
    ImageView google_img;
    TextView name,mail;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        TextView username = findViewById(R.id.username_txt);
        TextView password = findViewById(R.id.password_txt);
        name = findViewById(R.id.displayName);
        mail = findViewById(R.id.displayMail);
        google_img = (ImageView) findViewById(R.id.google_signin);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        // if sign in with google is pressed

        google_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });



        /*
        googleSignInOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //https://firebase.google.com/docs/auth/android/google-signin
                //use this to implement the authentication
                Toast.makeText(LoginActivity.this, "LOGIN google SUCCESSFUL", Toast.LENGTH_LONG).show();
            }
        });
         */
        loginBtn = findViewById(R.id.login_btn);
        //if login btn is pressed
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {
                    Toast.makeText(LoginActivity.this, "LOGIN SUCCESSFUL", Toast.LENGTH_LONG).show();
                    startMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "LOGIN UNSUCCESSFUL", Toast.LENGTH_LONG).show();
                }
            }

        });

        registerBtn = findViewById(R.id.register_btn);
        //if register btn is pressed
        registerBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view) {
                startRegisterActivity();
            }
        });
    }


    public void SignIn() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==100){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                startMainActivity();
            } catch (ApiException e) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void startRegisterActivity()
    {
        Intent intent = new Intent(getApplicationContext(), NewUserActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();


    }
}
