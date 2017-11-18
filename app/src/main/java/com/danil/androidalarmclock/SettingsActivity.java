package com.danil.androidalarmclock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.danil.androidalarmclock.MainActivity.APP_PREFERENCES;
import static com.danil.androidalarmclock.MainActivity.APP_PREFERENCES_TEXT;
import static com.danil.androidalarmclock.MainActivity.APP_PREFERENCES_TITLE;

public class SettingsActivity extends AppCompatActivity {

    private EditText editTitle;
    private EditText editText;
    private Button saveSettingsBtn;
    private Button cancelSettingsBtn;

    private SharedPreferences settingPreferences;

    private final String defaultTitle = "Будильник";
    private final String defaultText = "Пора просыпаться!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        editTitle = (EditText) findViewById(R.id.editTextTitle);
        final String title = settingPreferences.getString(APP_PREFERENCES_TITLE, defaultTitle);
        editTitle.setText(title);

        editText = (EditText) findViewById(R.id.editTextText);
        final String text = settingPreferences.getString(APP_PREFERENCES_TEXT, defaultText);
        editText.setText(text);

        saveSettingsBtn = (Button) findViewById(R.id.saveSettings);
        saveSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = editTitle.getText().toString();
                final String text = editText.getText().toString();
                // Запоминаем данные
                SharedPreferences.Editor editor = settingPreferences.edit();
                if (title.isEmpty()){
                    editor.putString(APP_PREFERENCES_TITLE, defaultTitle);
                }
                else {
                    editor.putString(APP_PREFERENCES_TITLE, title);
                }
                if (text.isEmpty()){
                    editor.putString(APP_PREFERENCES_TEXT, defaultText);
                }
                else{
                    editor.putString(APP_PREFERENCES_TEXT, text);
                }
                editor.apply();
                gotoMainActivity();
            }
        });

        cancelSettingsBtn = (Button) findViewById(R.id.cancelSettings);
        cancelSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMainActivity();
            }
        });
    }

    public void gotoMainActivity(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        gotoMainActivity();

        super.onBackPressed();
    }
}
