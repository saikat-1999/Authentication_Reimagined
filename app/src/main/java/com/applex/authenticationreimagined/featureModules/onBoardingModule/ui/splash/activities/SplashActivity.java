package com.applex.authenticationreimagined.featureModules.onBoardingModule.ui.splash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.applex.authenticationreimagined.R;
import com.applex.authenticationreimagined.featureModules.onBoardingModule.ui.login.activities.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long Splash_time_out = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        },Splash_time_out);
    }
}