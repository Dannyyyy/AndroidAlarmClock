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
            final int hour = settingPreferences.getInt(APP_PREFERENCES_HOUR, -1);
            final int minute = settingPreferences.getInt(APP_PREFERENCES_MINUTE, -1);
            if(hour != -1 && minute != -1) {
                final AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                final Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                final Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                alarmIntent.putExtra("extra", "yes");
                final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                long time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
                if (System.currentTimeMillis() > time) {
                    time += (1000 * 60 * 60 * 24);
                }
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            }
        }
    }
}
