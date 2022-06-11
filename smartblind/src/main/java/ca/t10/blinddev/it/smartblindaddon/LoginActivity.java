package ca.t10.blinddev.it.smartblindaddon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        TextView username = (TextView) findViewById(R.id.username_txt);
        TextView password = (TextView) findViewById(R.id.password_txt);

        Button loginBtn = (Button) findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().equals("admin") && password.getText().toString().equals("admin"))
                {
                    Toast.makeText(LoginActivity.this, "LOGIN SUCCESSFULL", Toast.LENGTH_LONG).show();
                    startMainActivity();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "LOGIN UNSUCCESSFULL", Toast.LENGTH_LONG).show();
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

}