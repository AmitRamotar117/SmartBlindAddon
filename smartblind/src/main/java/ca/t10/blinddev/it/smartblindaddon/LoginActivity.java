package ca.t10.blinddev.it.smartblindaddon;
//Chris Mutuc N01314607
//Amit Punit n01203930
//Andrew Fraser N01309442
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

   private Button loginBtn, registerBtn;
   private ImageView google_img;
   private TextView name,mail;
   private GoogleSignInOptions gso;
   private GoogleSignInClient gsc;
   private EditText editTextEmail, editTextPassword;
   private ProgressBar progressBar;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        name = findViewById(R.id.displayName);
        mail = findViewById(R.id.displayMail);
        google_img = (ImageView) findViewById(R.id.google_signin);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        // if sign in with google is pressed

        google_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();

            }
        });


        loginBtn = findViewById(R.id.login_btn);
        //if login btn is pressed
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();

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

        /*GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        mAuth.createUserWithEmailAndPassword(account.getEmail(), account.getDisplayName()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            String googleName = account.getDisplayName();
            String googleEmail = account.getEmail();
            User user = new User(googleName, googleEmail);
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "user has been registered successfully!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Failed to register!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "User is already registered", Toast.LENGTH_LONG).show();
                }
            }
        });*/

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
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String ukey = firebaseUser.getUid();
        String uemail = firebaseUser.getEmail();
        SharedPreferences sharedPreferences = getSharedPreferences("saved", Context.MODE_PRIVATE);
        SharedPreferences.Editor d = sharedPreferences.edit();
        d.putString("user_key",ukey);
        d.putString("user_email",uemail);
        d.commit();

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

    public void userLogin()
    {
        progressBar = findViewById(R.id.progressBar);
        editTextEmail = findViewById(R.id.email_txt);
        editTextPassword = findViewById(R.id.password_txt);

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty())
        {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty())
        {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Please enter valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if (!isValidPassword(password))
        {
            editTextPassword.setError("- at least 8 characters\n- at least 1 number\n- at least 1 special char\n- at least 1 uppercase\n- at least 1 lowercase");
            editTextPassword.requestFocus();
            return;
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                        startMainActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                    }

                }
            });
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\\\\\/%§\"&“|`´}{°><:.;#')(@_$\"!?*=^-]).{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();


    }
}
