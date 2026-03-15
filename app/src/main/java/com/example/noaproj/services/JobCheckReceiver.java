package com.example.noaproj.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.noaproj.model.Job;
import com.example.noaproj.services.DatabaseService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class JobCheckReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {



        SharedPreferences prefs = context.getSharedPreferences("jobFilter", Context.MODE_PRIVATE);
        String citiesStr = prefs.getString("cities", "");
        List<String> cities;
        if (citiesStr.isEmpty()) {
            cities = new ArrayList<String>();
        } else {
            cities = Arrays.asList(citiesStr.split(","));
        }

        String typesStr = prefs.getString("types", "");
        List<String> types;
        if (typesStr.isEmpty()) {
            types = new ArrayList<String>();
        } else {
            types = Arrays.asList(typesStr.split(","));
        }

        String titlesStr = prefs.getString("titles", "");
        List<String> titles;
        if (titlesStr.isEmpty()) {
            titles = new ArrayList<String>();
        } else {
            titles = Arrays.asList(titlesStr.split(","));
        }
        int minAge = prefs.getInt("minAge",16);
        int maxAge = prefs.getInt("maxAge",60);


        DatabaseService databaseService = DatabaseService.getInstance();
        databaseService.getJobList(new DatabaseService.DatabaseCallback<List<Job>>() {
            @Override
            public void onCompleted(List<Job> jobList) {
                // קריאה של הסינון שהמשתמש בחר והפיכת המחרוזת לרשימה

                // רשימה רק של עבודות מאושרות
                ArrayList<Job> approveJobs = new ArrayList<>();
                for(Job job : jobList){
                    if("approve".equals(job.getStatus()))
                        approveJobs.add(job);
                }

                // יצירת הרשימה המסוננת
                ArrayList<Job> filtered = filterJobs(approveJobs, cities, types, titles, minAge, maxAge);

                // הוספה לרשימה עבודות שכבר בוצעה בעקבותיהם התראה
                String sentIdsStr = prefs.getString("sentJobIds", "");
                List<String> sentIds = new ArrayList<>();
                if(!sentIdsStr.isEmpty()){
                    sentIds.addAll(Arrays.asList(sentIdsStr.split(",")));
                }

                // יצירת רשימה של העבודות שלא נשלחו עבורן התראה
                ArrayList<Job> newJobs = new ArrayList<>();
                for(int i = 0; i < filtered.size(); i++){
                    Job job = filtered.get(i);
                    if(!sentIds.contains(job.getId())){
                        newJobs.add(job);
                        sentIds.add(job.getId()); // מוסיפים כדי לא לשלוח שוב
                    }
                }

                // שליחת Notification רק אם יש עבודות חדשות
                if(newJobs.size() > 0){
                    NotificationHelper.sendNotification(context, newJobs.size());
                    // שמירת ה-IDs שנשלחו
                    prefs.edit().putString("sentJobIds", String.join(",", sentIds)).apply();
                }
        }
            @Override
            public void onFailed(Exception e) {}
        });
    }
    private ArrayList<Job> filterJobs(ArrayList<Job> jobs, List<String> cities, List<String> types,
                                      List<String> titles, int minAge, int maxAge) {
        ArrayList<Job> filteredJobs = new ArrayList<>();
        for(Job job : jobs){
            boolean match = true;

            Log.d("FILTER_DEBUG", "Checking job: " + job.getTitle() + " / " + job.getType() + " / " + job.getCity() + " / age " + job.getAge());

            if (!cities.isEmpty()) {
                boolean cityMatch = cities.contains(job.getCity());
                Log.d("FILTER_DEBUG", "City match? " + cityMatch);
                match = match && cityMatch;
            }

            if (!types.isEmpty()) {
                boolean typeMatch = types.contains(job.getType());
                Log.d("FILTER_DEBUG", "Type match? " + typeMatch);
                match = match && typeMatch;
            }

            if (!titles.isEmpty()) {
                boolean titleMatch = titles.contains(job.getTitle());
                Log.d("FILTER_DEBUG", "Title match? " + titleMatch);
                match = match && titleMatch;
            }

            int age = Integer.parseInt(job.getAge());
            boolean ageMatch = (age >= minAge && age <= maxAge);
            Log.d("FILTER_DEBUG", "Age match? " + ageMatch);
            match = match && ageMatch;

            Log.d("FILTER_DEBUG", "Final match? " + match);

            if(match){
                filteredJobs.add(job);
            }
        }
        return filteredJobs;
    }
}