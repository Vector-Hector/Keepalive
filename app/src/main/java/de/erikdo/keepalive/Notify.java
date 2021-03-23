package de.erikdo.keepalive;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;

import java.util.Random;

import static android.app.PendingIntent.getActivity;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Notify {
    public static void clearNotifications(Context parent) {
        NotificationManager notificationManager = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void showNotification(Context parent, String title, String body) {
        Intent intent = new Intent(parent, de.erikdo.keepalive.MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK);

        String NotificationChannelId = "de.erikdo.keepalive.channel";

        NotificationManager notificationManager = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);

        long[] vibratePattern = new long[] { 1000, 1000 };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NotificationChannelId, "Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(vibratePattern);
            notificationChannel.setDescription("Test description");
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(parent, NotificationChannelId);
        PendingIntent pendingIntent = getActivity(parent, 0, intent, 0);

        notificationBuilder.setAutoCancel(false)
                .setVibrate(vibratePattern)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("info")
                .setOnlyAlertOnce(false)
                .setLights(Color.RED, 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        Notification notification = notificationBuilder.build();

        notificationManager.notify(new Random().nextInt(), notification);
    }
}
