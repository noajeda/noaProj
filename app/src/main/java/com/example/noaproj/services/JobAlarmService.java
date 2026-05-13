package com.example.noaproj.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class JobAlarmService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null || intent.getAction() == null) {
            return START_STICKY;
        }
        String action = intent.getAction();
        if ("START".equals(action)) {
            startAlarm(); // הפעל
        }
        else if ("STOP".equals(action)) {
            stopAlarm(); // עצור
        }

        return START_STICKY;
    }

    // ---- הפעל התראה ----
    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent jobCheckIntent = new Intent(this, JobCheckReceiver.class);

        PendingIntent jobCheckPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                jobCheckIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );
        // הפעלה ראשונה
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                jobCheckPendingIntent
        );
    }

    // ---- עצור התראה ----
    private void stopAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent jobCheckIntent = new Intent(this, JobCheckReceiver.class);

        PendingIntent jobCheckPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                jobCheckIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );
        alarmManager.cancel(jobCheckPendingIntent);    // עוצר את ההתראות
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}