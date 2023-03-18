package de.hsos.findyourdoc.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import de.hsos.findyourdoc.R;
import de.hsos.findyourdoc.storage.SharedPreferencesEnum;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        this.sharedPreferences = getSharedPreferences(String.valueOf(SharedPreferencesEnum.SHARED_PREFERENCES_ID), MODE_PRIVATE);

        // Comment for simulate opening the app for the first time
        this.sharedPreferences.edit().clear().apply();

        // If asked for Metadata, propose to MainMenu, else open GreetingScreen
        if (loadInitialSetting()) {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashScreen.this, MainMenu.class));
                finish();
            }, 4000);
        } else {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashScreen.this, GreetingScreen.class));
                finish();
            }, 4000);
        }
    }

    private boolean loadInitialSetting() {
        return this.sharedPreferences.getBoolean(String.valueOf(SharedPreferencesEnum.DEFAULT_DOCTORS_ASKED), false);
    }
}