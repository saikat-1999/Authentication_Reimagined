package com.applex.authenticationreimagined.modules.splash.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.applex.authenticationreimagined.R;
import com.applex.authenticationreimagined.modules.login.ui.ActivityZip;
import com.applex.authenticationreimagined.modules.main.ui.MainActivity;
import com.applex.authenticationreimagined.utilities.preferenceManager.PreferenceManager;


public class ActivitySplash extends AppCompatActivity {

    private static final long Splash_time_out = 1500;

    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);

        preferenceManager = new PreferenceManager(ActivitySplash.this);

        if(preferenceManager.isFirstTimeLaunch()){
            preferenceManager.setIsFirstTimeLaunch(false);
            new Handler().postDelayed(() -> {
                startActivity(new Intent(ActivitySplash.this, ActivityZip.class));
                finish();
            },Splash_time_out);
        }
        else{
            if(preferenceManager.getCurrentUser()!=null){
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(ActivitySplash.this, MainActivity.class));
                    finish();
                },Splash_time_out);
            }
            else{
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(ActivitySplash.this, ActivityZip.class));
                    finish();
                },Splash_time_out);
            }

        }


    }
}