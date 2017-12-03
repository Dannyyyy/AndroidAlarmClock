package com.danil.androidalarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String state = intent.getStringExtra("extra");
        final int number = intent.getIntExtra("number", 0);
        final Intent serviceIntent = new Intent(context, RingtonePlayingService.class);
        serviceIntent.putExtra("extra", state);
        serviceIntent.putExtra("number", number);
        context.startService(serviceIntent);
    }
}
