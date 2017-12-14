package com.catherine.webservices.toolkits;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;


import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.NotificationActivity;
import com.catherine.webservices.R;
import com.catherine.webservices.entities.NotificationInfo;

import java.io.File;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Catherine on 2017/12/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class NotificationPlugin {
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int notificationID;
    private Timer progressTimer;

    private int max;
    private int progress;
    private boolean startCounting;

    public NotificationPlugin(NotificationInfo info) {
        mNotifyManager = (NotificationManager) MyApplication.INSTANCE.getSystemService(Context.NOTIFICATION_SERVICE);

        if (info.getId() == 0)
            notificationID = (int) System.currentTimeMillis();

        mBuilder = new NotificationCompat.Builder(MyApplication.INSTANCE);
        mBuilder.setContentTitle(info.getTitle())
                .setContentText(info.getDescription())
                .setSmallIcon(R.mipmap.ic_launcher_round);

        //You can't update notification frequently.
        progressTimer = new Timer();
        progressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (startCounting) {
                    mBuilder.setContentText(String.format(Locale.ENGLISH, "%d / %d", progress, max));
                    mBuilder.setProgress(max, progress, false);
                    // Displays the progress bar for the first time.
                    mNotifyManager.notify(notificationID, mBuilder.build());
                    if (progress >= max) {
                        progressTimer.cancel();
                        progressTimer.purge();
                    }
                }
            }
        }, 0, 1000);
    }

    public void setPendingIntent(File file, String fileName) {
        Bundle b = new Bundle();
        b.putString("name", fileName);
        b.putString("path", file.getAbsolutePath());
        b.putInt("notificationID", notificationID);
        Intent intent = new Intent(MyApplication.INSTANCE, NotificationActivity.class);
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(MyApplication.INSTANCE, Constants.NOTIFICATION_CALLBACK, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
    }


    public void update(int max, int progress) {
        startCounting = true;
        this.max = max;
        this.progress = progress;
    }

    public void finish(NotificationInfo info) {
        startCounting = false;
        progressTimer.cancel();
        progressTimer.purge();
        mBuilder.setContentTitle(info.getTitle())
                .setContentText(info.getDescription())
                .setProgress(0, 0, false);
        // Displays the progress bar for the first time.
        mNotifyManager.notify(notificationID, mBuilder.build());
    }

    public void cancel() {
        startCounting = false;
        progressTimer.cancel();
        progressTimer.purge();
        mNotifyManager.cancel(notificationID);
    }

    public int getNotificationID() {
        return notificationID;
    }
}
