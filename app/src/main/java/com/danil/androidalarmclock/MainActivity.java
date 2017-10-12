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
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    private PendingIntent pending_intent;

    private SharedPreferences mSettings;

    private TimePicker alarmTimePicker;
    private TextView alarmTextView;
    private Button stop_alarm;

    private int hour = -1;
    private int minute = -1;

    MainActivity activity;
    Context context;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_HOUR = "hour";
    public static final String APP_PREFERENCES_MINUTE = "minute";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        this.context = this;
        alarmTextView = (TextView) findViewById(R.id.alarmText);

        final Intent myIntent = new Intent(this.context, AlarmReceiver.class);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        final Calendar calendar = Calendar.getInstance();

        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmTimePicker.setIs24HourView(true);

        Button start_alarm= (Button) findViewById(R.id.start_alarm);
        start_alarm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hour = alarmTimePicker.getHour();
                minute = alarmTimePicker.getMinute();
                stop_alarm.setEnabled(true);
                String hourString = String.valueOf(hour);
                String minuteString = String.valueOf(minute);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                myIntent.putExtra("extra", "yes");
                pending_intent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if(minute < 10)
                {
                    minuteString = "0".concat(minuteString);
                }
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);
                setAlarmClockTextView("Будильник установлен на " + hourString + ":" + minuteString);
            }
        });

        stop_alarm = (Button) findViewById(R.id.stop_alarm);
        stop_alarm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hour = -1;
                minute = -1;
                myIntent.putExtra("extra", "no");
                sendBroadcast(myIntent);
                alarmManager.cancel(pending_intent);
                setAlarmClockTextView("Будильник выключен");
            }
        });

    }

    public void setAlarmClockTextView(String alarmText) {
        alarmTextView.setText(alarmText);
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
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_HOUR, hour);
        editor.putInt(APP_PREFERENCES_MINUTE, minute);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        hour = mSettings.getInt(APP_PREFERENCES_HOUR, -1);
        minute = mSettings.getInt(APP_PREFERENCES_MINUTE, -1);
        if(hour == -1 || minute == -1)
        {
            stop_alarm.setEnabled(false);
            setAlarmClockTextView("Установите время на будильнике");
        }
        else
        {
            String hourString = String.valueOf(hour);
            String minuteString = String.valueOf(minute);
            if(minute < 10)
            {
                minuteString = "0".concat(minuteString);
            }
            setAlarmClockTextView("Будильник установлен на " + hourString + ":" + minuteString);
        }
    }
}
