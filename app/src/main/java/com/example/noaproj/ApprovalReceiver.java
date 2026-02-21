package com.example.noaproj;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class ApprovalReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String jobTitle = intent.getStringExtra("job_title");

        String channelId = "approval_channel";
        String channelName = "Job Approval";

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // יצירת ערוץ התראות רק אם גרסת האנדרואיד >= O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Job Approved")
                .setContentText("The job '" + jobTitle + "' has been approved!")
                .setAutoCancel(true);

        // בדיקה לפני השימוש כדי למנוע NullPointerException
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}