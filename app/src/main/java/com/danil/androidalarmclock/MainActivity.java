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

    AlarmManager firstAlarmManager;
    private PendingIntent firstAlarmPendingIntent;

    AlarmManager secondAlarmManager;
    private PendingIntent secondAlarmPendingIntent;

    private final int firstActivityRequestCode = 1;

    private SharedPreferences settingPreferences;

    private TimePicker alarmTimePicker;
    private TextView firstAlarmTextView;
    private Button stopFirstAlarmClockBtn;
    private Button startFirstAlarmClockBtn;
    private ImageButton settingsBtn;
    private TextView secondAlarmTextView;
    private Button stopSecondAlarmClockBtn;
    private Button startSecondAlarmClockBtn;

    private int firstAlarmHour = -1;
    private int firstAlarmMinute = -1;

    private int secondAlarmHour = -1;
    private int secondAlarmMinute = -1;

    MainActivity activity;
    Context context;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_FIRST_ALARM_HOUR = "firstAlarmHour";
    public static final String APP_PREFERENCES_FIRST_ALARM_MINUTE = "firstAlarmMinute";
    public static final String APP_PREFERENCES_SECOND_ALARM_HOUR = "secondAlarmHour";
    public static final String APP_PREFERENCES_SECOND_ALARM_MINUTE = "secondAlarmMinute";
    public static final String APP_PREFERENCES_TITLE = "title";
    public static final String APP_PREFERENCES_TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        this.context = this;
        firstAlarmTextView = (TextView) findViewById(R.id.firstAlarmText);

        secondAlarmTextView = (TextView) findViewById(R.id.secondAlarmText);

        final Intent firstAlarmIntent = new Intent(this.context, AlarmReceiver.class);
        firstAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        final Intent secondAlarmIntent = new Intent(this.context, AlarmReceiver.class);
        secondAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        final Calendar calendar = Calendar.getInstance();

        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmTimePicker.setIs24HourView(true);

        startFirstAlarmClockBtn = (Button) findViewById(R.id.start_first_alarm);
        startFirstAlarmClockBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                firstAlarmHour = alarmTimePicker.getHour();
                firstAlarmMinute = alarmTimePicker.getMinute();
                calendar.set(Calendar.HOUR_OF_DAY, firstAlarmHour);
                calendar.set(Calendar.MINUTE, firstAlarmMinute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                firstAlarmIntent.putExtra("extra", "yes");
                secondAlarmIntent.putExtra("number", 0);
                firstAlarmPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, firstAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                long time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
                if(System.currentTimeMillis()>time) {
                    time += (1000*60*60*24);
                }
                firstAlarmManager.set(AlarmManager.RTC_WAKEUP, time, firstAlarmPendingIntent);
                firstAlarmClockInstalled();
            }
        });

        startSecondAlarmClockBtn = (Button) findViewById(R.id.start_second_alarm);
        startSecondAlarmClockBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                secondAlarmHour = alarmTimePicker.getHour();
                secondAlarmMinute = alarmTimePicker.getMinute();
                calendar.set(Calendar.HOUR_OF_DAY, secondAlarmHour);
                calendar.set(Calendar.MINUTE, secondAlarmMinute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                secondAlarmIntent.putExtra("extra", "yes");
                secondAlarmIntent.putExtra("number", 1);
                secondAlarmPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1, secondAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                long time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
                if(System.currentTimeMillis()>time) {
                    time += (1000*60*60*24);
                }
                secondAlarmManager.set(AlarmManager.RTC_WAKEUP, time, secondAlarmPendingIntent);
                secondAlarmClockInstalled();
            }
        });

        stopFirstAlarmClockBtn = (Button) findViewById(R.id.stop_first_alarm);
        stopFirstAlarmClockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstAlarmHour = firstAlarmMinute = -1;
                firstAlarmIntent.putExtra("extra", "no");
                secondAlarmIntent.putExtra("number", 0);
                sendBroadcast(firstAlarmIntent);
                firstAlarmManager.cancel(firstAlarmPendingIntent);
                firstAlarmClockNotInstalled();
            }
        });

        stopSecondAlarmClockBtn = (Button) findViewById(R.id.stop_second_alarm);
        stopSecondAlarmClockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondAlarmHour = secondAlarmMinute = -1;
                secondAlarmIntent.putExtra("extra", "no");
                secondAlarmIntent.putExtra("number", 1);
                sendBroadcast(secondAlarmIntent);
                secondAlarmManager.cancel(secondAlarmPendingIntent);
                secondAlarmClockNotInstalled();
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

    private void setFirstAlarmClockTextView(String alarmText) {
        firstAlarmTextView.setText(alarmText);
    }

    private void setSecondAlarmClockTextView(String alarmText) {
        secondAlarmTextView.setText(alarmText);
    }

    private void firstAlarmClockInstalled() {
        startFirstAlarmClockBtn.setEnabled(false);
        stopFirstAlarmClockBtn.setEnabled(true);
        String hourString = String.valueOf(firstAlarmHour);
        String minuteString = String.valueOf(firstAlarmMinute);
        if(firstAlarmMinute < 10) {
            minuteString = "0".concat(minuteString);
        }
        if(firstAlarmHour < 10) {
            hourString = "0".concat(hourString);
        }
        setFirstAlarmClockTextView("Будильник установлен на " + hourString + ":" + minuteString);
    }

    private void firstAlarmClockNotInstalled() {
        startFirstAlarmClockBtn.setEnabled(true);
        stopFirstAlarmClockBtn.setEnabled(false);
        setFirstAlarmClockTextView("Установите время на будильнике");
    }

    private void secondAlarmClockInstalled() {
        startSecondAlarmClockBtn.setEnabled(false);
        stopSecondAlarmClockBtn.setEnabled(true);
        String hourString = String.valueOf(secondAlarmHour);
        String minuteString = String.valueOf(secondAlarmMinute);
        if(secondAlarmMinute < 10) {
            minuteString = "0".concat(minuteString);
        }
        if(secondAlarmHour < 10) {
            hourString = "0".concat(hourString);
        }
        setSecondAlarmClockTextView("Будильник установлен на " + hourString + ":" + minuteString);
    }

    private void secondAlarmClockNotInstalled() {
        startSecondAlarmClockBtn.setEnabled(true);
        stopSecondAlarmClockBtn.setEnabled(false);
        setSecondAlarmClockTextView("Установите время на будильнике");
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
        editor.putInt(APP_PREFERENCES_FIRST_ALARM_HOUR, firstAlarmHour);
        editor.putInt(APP_PREFERENCES_FIRST_ALARM_MINUTE, firstAlarmMinute);
        editor.putInt(APP_PREFERENCES_SECOND_ALARM_HOUR, secondAlarmHour);
        editor.putInt(APP_PREFERENCES_SECOND_ALARM_MINUTE, secondAlarmMinute);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        firstAlarmHour = settingPreferences.getInt(APP_PREFERENCES_FIRST_ALARM_HOUR, -1);
        firstAlarmMinute = settingPreferences.getInt(APP_PREFERENCES_FIRST_ALARM_MINUTE, -1);
        if(firstAlarmHour == -1 || firstAlarmMinute == -1) {
            firstAlarmClockNotInstalled();
        }
        else {
            firstAlarmClockInstalled();
        }

        secondAlarmHour = settingPreferences.getInt(APP_PREFERENCES_SECOND_ALARM_HOUR, -1);
        secondAlarmMinute = settingPreferences.getInt(APP_PREFERENCES_SECOND_ALARM_MINUTE, -1);
        if(secondAlarmHour == -1 || secondAlarmMinute == -1) {
            secondAlarmClockNotInstalled();
        }
        else {
            secondAlarmClockInstalled();
        }
    }
}
