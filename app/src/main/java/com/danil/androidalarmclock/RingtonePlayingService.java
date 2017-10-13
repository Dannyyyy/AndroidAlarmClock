package com.danil.androidalarmclock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Random;

public class RingtonePlayingService extends Service {

    private boolean isInstalled;
    MediaPlayer mediaPlayer;
    private int startId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        Notification notification  = new Notification.Builder(this)
                .setContentTitle("Подъем!!!")
                .setContentText("Пора учиться,...!!!")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();
        notification.ledARGB = Color.BLUE;
        notification.ledOffMS = 1000;
        notification.ledOnMS = 1000;
        notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;

        String state = intent.getStringExtra("extra");

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

        if(!this.isInstalled && startId == 1)
        {
            Random random = new Random();
            int number = random.nextInt(5) + 1;
            switch(number)
            {
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
        else
        {
            if (!this.isInstalled && startId == 0)
            {
                this.isInstalled = false;
                this.startId = 0;
            }
            else
            {
                if (this.isInstalled && startId == 1)
                {
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
