package ca.t10.blinddev.it.smartblindaddon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUserActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    Context view;
    EditText editTextName,editTextEmail,editTextPassword,editTextConfirm;
    Button sub;
    ImageButton back;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        view = getApplication().getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        editTextName = findViewById(R.id.new_user_name);
        editTextEmail = findViewById(R.id.new_user_email);
        editTextPassword = findViewById(R.id.new_user_password);
        editTextConfirm = findViewById(R.id.new_user_confirm);
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
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    public void applySettings(){
        SharedPreferences sharedPreferences = getSharedPreferences("saved", Context.MODE_PRIVATE);

        boolean d = sharedPreferences.getBoolean("dark",false);
        boolean n = sharedPreferences.getBoolean("note",false);
        String t = sharedPreferences.getString("size","");

        if(d){enableDarkMode();}
        if(n){
            BlindNotifications bl = new BlindNotifications(getApplicationContext());
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
        editTextName.setTextColor(getResources().getColor(R.color.white));
        editTextName.setHintTextColor(getResources().getColor(R.color.white));

        editTextEmail.setTextColor(getResources().getColor(R.color.white));
        editTextEmail.setHintTextColor(getResources().getColor(R.color.white));

        editTextPassword.setTextColor(getResources().getColor(R.color.white));
        editTextPassword.setHintTextColor(getResources().getColor(R.color.white));
    }

    public void setTextSize(int size){
        sub.setTextSize(size);
    }

    private void registerUser()
    {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirm = editTextConfirm.getText().toString().trim();
        progressBar = findViewById(R.id.progressBar);

        if (name.isEmpty())
        {
            editTextName.setError(getString(R.string.name_error));
            editTextName.requestFocus();
        }
        if (email.isEmpty())
        {
            editTextEmail.setError(getString(R.string.email_error));
            editTextEmail.requestFocus();
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Please enter valid email!");
            editTextEmail.requestFocus();
        }
        if (password.isEmpty())
        {
            editTextPassword.setError(getString(R.string.password_error));
            editTextPassword.requestFocus();
        }
        if (!isValidPassword(password))
        {
            editTextPassword.setError("- at least 8 characters\n- at least 1 number\n- at least 1 special char\n- at least 1 uppercase\n- at least 1 lowercase");
            editTextPassword.requestFocus();
        }

        if (!password.equals(confirm))
        {
            editTextConfirm.setError("Password does not match");
            editTextConfirm.requestFocus();
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        User user = new User(name, email);

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(NewUserActivity.this, "user has been registered successfully!", Toast.LENGTH_LONG).show();
                                            startLoginActivity();
                                        } else {
                                            Toast.makeText(NewUserActivity.this, "Failed to register!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(NewUserActivity.this, "Failed to register!", Toast.LENGTH_LONG).show();
                    }
                }
            });
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void startLoginActivity()
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
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