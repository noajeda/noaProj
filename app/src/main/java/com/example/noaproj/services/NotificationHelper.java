package com.example.noaproj.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import com.example.noaproj.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "job_notifications";
    private static final String CHANNEL_NAME = "Job Notifications";

    public static void sendNotification(Context context, int newJobsCount) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return; // אין הרשאה, חוזרים
            }
        }
        // יצירת ערוץ Notification (Android 8 ומעלה)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);
        }

        // בניית ההודעה
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo2)
                .setContentTitle("עבודות חדשות")
                .setContentText("יש " + newJobsCount + " עבודות חדשות שמתאימות לבקשתך!")
                .setAutoCancel(true);

        // שליחת Notification עם מזהה יחודי
        manager.notify(1, builder.build());
    }
}