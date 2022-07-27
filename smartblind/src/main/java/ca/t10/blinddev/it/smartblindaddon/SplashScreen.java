package ca.t10.blinddev.it.smartblindaddon;
//Chris Mutuc n01314607

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //Splash Screen
        //splash screen will display for three seconds and move to login screen


        new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}