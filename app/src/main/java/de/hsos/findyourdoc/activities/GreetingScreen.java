package de.hsos.findyourdoc.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import de.hsos.findyourdoc.R;
import de.hsos.findyourdoc.storage.SharedPreferencesEnum;

public class GreetingScreen extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText cityEditText;
    private CheckBox defaultSettingsCheckBox;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting_screen);
        Objects.requireNonNull(getSupportActionBar()).setTitle("FindYourDoc");

        this.usernameEditText = findViewById(R.id.usernameTextInputFieldEditText);
        this.cityEditText = findViewById(R.id.cityTextInputFieldEditText);
        this.defaultSettingsCheckBox = findViewById(R.id.checkboxDefaultSettings);
        this.sharedPreferences = getSharedPreferences(String.valueOf(SharedPreferencesEnum.SHARED_PREFERENCES_ID), MODE_PRIVATE);

        Button saveUserDataButton = findViewById(R.id.saveUserDataButton);
        saveUserDataButton.setOnClickListener(view -> {
            if (usernameEditText.getText().toString().length() > 30 || cityEditText.getText().toString().length() > 50) {
                Toast.makeText(this, getString(R.string.numberOfCharsExceeded), Toast.LENGTH_LONG).show();
                return;
            }

            if (!usernameEditText.getText().toString().isEmpty() && !usernameEditText.getText().toString().isBlank()
                    && !cityEditText.getText().toString().isEmpty() && !cityEditText.getText().toString().isBlank()) {
                saveUserData();
                Intent intent = new Intent(GreetingScreen.this, MainMenu.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.incomplete_form), Toast.LENGTH_LONG).show();
            }
        });

        welcomeAlert();
    }

    private void welcomeAlert() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.peace_icon);
        builder.setTitle(getString(R.string.welcome_to_findyourdoc))
                .setMessage(getString(R.string.welcome_alert_text))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> dialogInterface.cancel()).show();
    }

    private void saveUserData() {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(String.valueOf(SharedPreferencesEnum.DEFAULT_DOCTORS_ASKED), true);
        editor.putString(String.valueOf(SharedPreferencesEnum.USERNAME), this.usernameEditText.getText().toString());
        editor.putString(String.valueOf(SharedPreferencesEnum.CITY), this.cityEditText.getText().toString());
        editor.putBoolean(String.valueOf(SharedPreferencesEnum.SET_DEFAULT_DOCTORS), this.defaultSettingsCheckBox.isChecked());
        editor.apply();
    }
}