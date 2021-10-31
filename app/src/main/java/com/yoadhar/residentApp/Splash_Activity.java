package com.yoadhar.residentApp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.yoadhar.residentApp.MainActivity;
import com.yoadhar.residentApp.R;

public class Splash_Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int SPLASH_SCREEN_TIME_OUT = 2000; //After Completion of 3000 ms the next activity will get started

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that splash activity can cover the entire screen

        setContentView(R.layout.activity_splash);
        //This will bind splash_Activity with activity_splash

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash_Activity.this, MainActivity.class);
                //Intent is used to switch from one activity to another

                startActivity(intent); // invoke MainActivity
                finish(); // the current activity will get finished

            }
        }, SPLASH_SCREEN_TIME_OUT);


    }
}