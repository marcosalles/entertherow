package com.marcosalles.entertherow.managers;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.marcosalles.entertherow.R;

import java.util.UUID;

public class CustomNotificationManager {
    public static final String CHANNEL_ID = UUID.randomUUID().toString();
    public static final int FOREGROUND_ID = 1;
    public static final int NOTIFICATION_ID = 777;

    private IntentService context;

    public CustomNotificationManager(IntentService context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Enter The Row TransitionService Channel", NotificationManager.IMPORTANCE_DEFAULT);
            context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("")
                .setContentText("")
                .build();

        context.startForeground(FOREGROUND_ID, notification);
    }

    public void send(String title, String content) {
        Notification notification = new NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build();

        this.notify(notification);
    }

    private void notify(Notification notification) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.cancel(NOTIFICATION_ID);
        manager.notify(NOTIFICATION_ID, notification);
    }

    public Context getContext() {
        return context;
    }
}
