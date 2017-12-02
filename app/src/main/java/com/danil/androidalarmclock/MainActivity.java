package com.danil.androidalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private final int firstActivityRequestCode = 1;

    private SharedPreferences settingPreferences;

    private TimePicker alarmTimePicker;
    private TextView alarmTextView;
    private Button stopAlarmClockBtn;
    private Button startAlarmClockBtn;
    private ImageButton settingsBtn;

    private int hour = -1;
    private int minute = -1;

    MainActivity activity;
    Context context;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_HOUR = "hour";
    public static final String APP_PREFERENCES_MINUTE = "minute";
    public static final String APP_PREFERENCES_TITLE = "title";
    public static final String APP_PREFERENCES_TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        this.context = this;
        alarmTextView = (TextView) findViewById(R.id.alarmText);

        final Intent alarmIntent = new Intent(this.context, AlarmReceiver.class);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        final Calendar calendar = Calendar.getInstance();

        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmTimePicker.setIs24HourView(true);

        startAlarmClockBtn = (Button) findViewById(R.id.start_alarm);
        startAlarmClockBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hour = alarmTimePicker.getHour();
                minute = alarmTimePicker.getMinute();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                alarmIntent.putExtra("extra", "yes");
                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                long time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
                if(System.currentTimeMillis()>time) {
                    time += (1000*60*60*24);
                }
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                alarmClockInstalled();
            }
        });

        stopAlarmClockBtn = (Button) findViewById(R.id.stop_alarm);
        stopAlarmClockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hour = minute = -1;
                alarmIntent.putExtra("extra", "no");
                sendBroadcast(alarmIntent);
                alarmManager.cancel(pendingIntent);
                alarmClockNotInstalled();
            }
        });

        settingsBtn = (ImageButton) findViewById(R.id.btnSettings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSettingsActivity();
            }
        });
    }

    public void gotoSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) { }

    private void setAlarmClockTextView(String alarmText) {
        alarmTextView.setText(alarmText);
    }

    private void alarmClockInstalled() {
        startAlarmClockBtn.setEnabled(false);
        stopAlarmClockBtn.setEnabled(true);
        String hourString = String.valueOf(hour);
        String minuteString = String.valueOf(minute);
        if(minute < 10) {
            minuteString = "0".concat(minuteString);
        }
        if(hour < 10) {
            hourString = "0".concat(hourString);
        }
        setAlarmClockTextView("Будильник установлен на " + hourString + ":" + minuteString);
    }

    private void alarmClockNotInstalled() {
        startAlarmClockBtn.setEnabled(true);
        stopAlarmClockBtn.setEnabled(false);
        setAlarmClockTextView("Установите время на будильнике");
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Запоминаем данные
        SharedPreferences.Editor editor = settingPreferences.edit();
        editor.putInt(APP_PREFERENCES_HOUR, hour);
        editor.putInt(APP_PREFERENCES_MINUTE, minute);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        hour = settingPreferences.getInt(APP_PREFERENCES_HOUR, -1);
        minute = settingPreferences.getInt(APP_PREFERENCES_MINUTE, -1);
        if(hour == -1 || minute == -1) {
            alarmClockNotInstalled();
        }
        else {
            alarmClockInstalled();
        }
    }
}
