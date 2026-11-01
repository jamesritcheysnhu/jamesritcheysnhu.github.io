package com.zybooks.eventtracker;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class EventNotificationWorker extends Worker {

    public static final String KEY_TITLE = "title";
    private final static int NOTIFICATION_ID = 0;
    private final static String CHANNEL_ID = "channel_timer";
    private final static String CHANNEL_TITLE = "timed Event notification";
    private final NotificationManager mNotificationManager;



    public EventNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        createTimerNotificationChannel();
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString(KEY_TITLE);

        // perform the notification action
        createTimerNotification(title);

        return Result.success();
    }

    private void createTimerNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID, CHANNEL_TITLE, NotificationManager.IMPORTANCE_HIGH);

        // register channel with system
        mNotificationManager.createNotificationChannel(channel);
    }

    private void createTimerNotification(String title) {
        Context context = getApplicationContext();

        // generate the notification structure
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(getApplicationContext().getString(R.string.notification_event_title))
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build();

        // display the notification
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}