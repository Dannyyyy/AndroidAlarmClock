package com.danil.androidalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static com.danil.androidalarmclock.MainActivity.APP_PREFERENCES;
import static com.danil.androidalarmclock.MainActivity.APP_PREFERENCES_HOUR;
import static com.danil.androidalarmclock.MainActivity.APP_PREFERENCES_MINUTE;

public class RestartAlarmsReceiver extends BroadcastReceiver {

    private SharedPreferences settingPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            settingPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            int hour = settingPreferences.getInt(APP_PREFERENCES_HOUR, -1);
            int minute = settingPreferences.getInt(APP_PREFERENCES_MINUTE, -1);
            if(hour != -1 && minute != -1) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                Intent myIntent = new Intent(context, AlarmReceiver.class);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                myIntent.putExtra("extra", "yes");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                long time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
                if (System.currentTimeMillis() > time) {
                    time += (1000 * 60 * 60 * 24);
                }
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            }
        }
    }
}
