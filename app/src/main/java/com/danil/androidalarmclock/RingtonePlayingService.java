package com.danil.androidalarmclock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Random;

import static com.danil.androidalarmclock.MainActivity.APP_PREFERENCES;
import static com.danil.androidalarmclock.MainActivity.APP_PREFERENCES_TEXT;
import static com.danil.androidalarmclock.MainActivity.APP_PREFERENCES_TITLE;

public class RingtonePlayingService extends Service {

    private boolean isInstalled;
    MediaPlayer mediaPlayer;
    private int startId;

    private SharedPreferences settingPreferences;

    private final String defaultTitle = "Будильник";
    private final String defaultText = "Пора просыпаться!";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        settingPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        final String title = settingPreferences.getString(APP_PREFERENCES_TITLE, defaultTitle);
        final String text = settingPreferences.getString(APP_PREFERENCES_TEXT, defaultText);

        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final Intent activityIntent = new Intent(this.getApplicationContext(), MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
        final Notification notification  = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                .build();
        notification.ledARGB = Color.BLUE;
        notification.ledOffMS = 1000;
        notification.ledOnMS = 1000;
        notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;


        final String state = intent.getStringExtra("extra");

        assert state != null;
        switch (state) {
            case "no":
                startId = 0;
                break;
            case "yes":
                startId = 1;
                break;
            default:
                startId = 0;
        }

        if(!this.isInstalled && startId == 1) {
            Random random = new Random();
            int number = random.nextInt(5) + 1;
            switch(number) {
                case 1: mediaPlayer = MediaPlayer.create(this, R.raw.track_1);
                    break;
                case 2: mediaPlayer = MediaPlayer.create(this, R.raw.track_2);
                    break;
                case 3: mediaPlayer = MediaPlayer.create(this, R.raw.track_3);
                    break;
                case 4: mediaPlayer = MediaPlayer.create(this, R.raw.track_4);
                    break;
                case 5: mediaPlayer = MediaPlayer.create(this, R.raw.track_5);
                    break;
                default: mediaPlayer = MediaPlayer.create(this, R.raw.track_1);
            }
            mediaPlayer.start();

            notificationManager.notify(0, notification);

            this.isInstalled = true;
            this.startId = 0;
        }
        else {
            if (!this.isInstalled && startId == 0) {
                this.isInstalled = false;
                this.startId = 0;
            }
            else {
                if (this.isInstalled && startId == 1) {
                    this.isInstalled = true;
                    this.startId = 0;
                } else {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    this.isInstalled = false;
                    this.startId = 0;
                }
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isInstalled = false;
    }
}
