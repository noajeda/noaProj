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
            startAlarm();
        } else if ("STOP".equals(action)) {
            stopAlarm();
        }

        return START_STICKY;
    }

    // ---- הפעל התראה ----
    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, JobCheckReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // הפעלה ראשונה מיד
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                pendingIntent
        );
    }


    // ---- עצור התראה ----
    private void stopAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, JobCheckReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}