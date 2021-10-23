package com.applex.authenticationreimagined.featureModules.onBoardingModule.ui.login.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.applex.authenticationreimagined.R;
import com.applex.authenticationreimagined.featureModules.onBoardingModule.ui.login.fragments.AadharNumberFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, new AadharNumberFragment());
        fragmentTransaction.commit();
    }
}