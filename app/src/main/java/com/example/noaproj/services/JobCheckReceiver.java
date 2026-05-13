package com.example.noaproj.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.example.noaproj.model.Job;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class JobCheckReceiver extends BroadcastReceiver {
    SharedPreferences prefs;

    @Override
    public void onReceive(Context context, Intent intent) {

        final PendingResult pendingResult = goAsync();

       // שליפת כל הנתונים מהסינון שביצע המשתמש והפיכת המחרוזות לרשימות
        prefs = context.getSharedPreferences("jobFilter", Context.MODE_PRIVATE);
        String citiesStr = prefs.getString("cities", "");
        List<String> cities;
        if (citiesStr.isEmpty()) {
            cities = new ArrayList<String>();
        } else {
            cities = new ArrayList<> (Arrays.asList(citiesStr.split(",")));  // רשימת ערים
        }

        String typesStr = prefs.getString("types", "");
        List<String> types;
        if (typesStr.isEmpty()) {
            types = new ArrayList<String>();
        } else {
            types =new ArrayList<> (Arrays.asList(typesStr.split(","))); // רשימת סוגים
        }

        String titlesStr = prefs.getString("titles", "");
        List<String> titles;
        if (titlesStr.isEmpty()) {
            titles = new ArrayList<String>();
        } else {
            titles =new ArrayList<> (Arrays.asList(titlesStr.split(","))); // רשימת תפקידים
        }
        int minAge = prefs.getInt("minAge",16);
        int maxAge = prefs.getInt("maxAge",60);


        DatabaseService databaseService = DatabaseService.getInstance();
        databaseService.getJobList(new DatabaseService.DatabaseCallback<List<Job>>() {
            @Override
            public void onCompleted(List<Job> jobList) {
                try {
                    // יצירת רשימה של כל העבודות המאושרות
                    ArrayList<Job> approveJobs = new ArrayList<>();
                    for (Job job : jobList) {
                        if ("approve".equals(job.getStatus()))
                            approveJobs.add(job);
                    }

                    // יצירת הרשימה המסוננת
                    ArrayList<Job> filtered = filterJobs(approveJobs, cities, types, titles, minAge, maxAge);

                    // הוספה לרשימה עבודות שכבר בוצעה בעקבותיהם התראה
                    String sentIdsStr = prefs.getString("sentJobIds", ""); // מכילה את כל הIds של העבודות שנשלחו עבורן התראה
                    List<String> sentIds = new ArrayList<>();
                    if (!sentIdsStr.isEmpty()) {
                        sentIds.addAll(Arrays.asList(sentIdsStr.split(",")));
                    }

                    // יצירת רשימה newJobs של העבודות שלא נשלחו עבורן התראה, והוספת הIds שלהן לרשימה sentIds
                    ArrayList<Job> newJobs = new ArrayList<>();
                    for (int i = 0; i < filtered.size(); i++) {
                        Job job = filtered.get(i);
                        if (!sentIds.contains(job.getId())) {
                            newJobs.add(job); // רשימת העבודות שכעת נשלחת עבורן התראה
                            sentIds.add(job.getId()); // רשימת ה-Ids של כל העבודות שנשלחו עבורתן התראה
                        }
                    }

                    Log.d("JobCheck", "Filtered jobs: " + filtered.size());
                    Log.d("JobCheck", "New jobs: " + newJobs.size());

                    // שליחת Notification רק אם יש עבודות חדשות
                    if (newJobs.size() > 0) {
                        NotificationHelper.sendNotification(context, newJobs.size());
                        prefs.edit().putString("sentJobIds", TextUtils.join(",", sentIds)).apply();  // שמירת ה-IDs שנשלחו
                    }
                    scheduleNext(context);   // הפעל התראה הבאה
                }
                catch(Exception e) {
                    Log.e("JobCheckReceiver", "Error in onCompleted: " + e.getMessage());
                } finally {
                    pendingResult.finish();
                }
            }
            @Override
            public void onFailed(Exception e) {
                Log.e("JobCheckReceiver", "Database failed: " + e.getMessage());
                pendingResult.finish();
            }
        });
    }
    private void scheduleNext(Context context) {
        if (!prefs.getBoolean("notificationsEnabled", false))
            return; // המשתמש כיבה, לא מתזמנים שוב

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent scheduleIntent = new Intent(context, JobCheckReceiver.class);

        PendingIntent schedulePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                scheduleIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // הפעל את ההתראה בעוד 20 שניות
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 1000 * 60 * 15,
                schedulePendingIntent
        );
    }

    // ---- פעולה המחזירה את רשימת העבודות המתאימות על פי סינון המשתמש  ----
    private ArrayList<Job> filterJobs(ArrayList<Job> jobs, List<String> cities, List<String> types,
                                      List<String> titles, int minAge, int maxAge) {
        ArrayList<Job> filteredJobs = new ArrayList<>();
        for(Job job : jobs){
            boolean match = true;

            if (!cities.isEmpty()) {
                boolean cityMatch = cities.contains(job.getCity());
                match = match && cityMatch;
            }

            if (!types.isEmpty()) {
                boolean typeMatch = types.contains(job.getType());
                match = match && typeMatch;
            }

            if (!titles.isEmpty()) {
                boolean titleMatch = titles.contains(job.getTitle());
                match = match && titleMatch;
            }

                int age = Integer.parseInt(job.getAge());
                boolean ageMatch = (age >= minAge && age <= maxAge);
                match = match && ageMatch;

            if(match){
                filteredJobs.add(job);
            }
        }
        return filteredJobs;
    }
}