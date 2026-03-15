package com.example.noaproj;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.noaproj.adapters.OfferAdapter;
import com.example.noaproj.model.Job;
import com.example.noaproj.services.AlarmReceiver;
import com.example.noaproj.services.DatabaseService;
import com.example.noaproj.services.JobAlarmService;
import com.example.noaproj.services.JobCheckReceiver;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

public class JobNotification extends AppCompatActivity {
    ArrayList<Job> approveArraylist = new ArrayList<Job>();
    private DatabaseService databaseService;
    private SharedPreferences prefs;
    Switch swNotification;
    BottomSheetDialog bottomSheetDialog;
    boolean applied;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_job_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        initListeners();
    }

    private void initListeners() {
        swNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {

            prefs = getSharedPreferences("jobFilter", MODE_PRIVATE);
           // prefs.edit().putBoolean("notificationsEnabled", isChecked).apply();   // הפעל/כבה מתג

            if(isChecked){
                bottomSheetDialog.show();
                showFilter(bottomSheetDialog);
            }else{
                Intent intent = new Intent(JobNotification.this, JobAlarmService.class);                intent.setAction("STOP");
                intent.setAction("STOP");
                startService(intent);
            }

        });

    }

    private void initViews() {
        databaseService = DatabaseService.getInstance();

        swNotification = findViewById(R.id.swNotification);
        SharedPreferences prefs = getSharedPreferences("jobFilter", MODE_PRIVATE);
        boolean enabled = prefs.getBoolean("notificationsEnabled", false);  // האם המתג הופעל בעבר
        swNotification.setChecked(enabled); // השאר מופעל/כבוי

        bottomSheetDialog = new BottomSheetDialog(JobNotification.this);
        bottomSheetDialog.setContentView(R.layout.botton_sheet_filter);

    }


        private void showFilter(BottomSheetDialog bottomSheetDialog) {
        // יצירת bottomSheetDialog

        // ages
        RangeSlider sliderAge = bottomSheetDialog.findViewById(R.id.sliderAge);
        List<Float> initialValues = new ArrayList<>();
        initialValues.add(16f); // יד שמאלית
        initialValues.add(60f); // יד ימנית
        sliderAge.setValues(initialValues);


        // cities
        LinearLayout layoutCities = bottomSheetDialog.findViewById(R.id.layoutCities);
        ArrayList<CheckBox> cbCites = new ArrayList<>();  // רשימת ערים עבור בדיקה
        String[] cities = getResources().getStringArray(R.array.arrCity); // אופציות מהמאגר
        for(int i = 0; i< cities.length; i++){
            String city = cities[i];
            CheckBox cb = new CheckBox(this);
            cb.setText(city);
            layoutCities.addView(cb);   // הוספת הרשימה לתצוגה
            cbCites.add(cb);    // שמירה ברשימה עבור בדיקה
        }
        // types
        LinearLayout layoutTypes = bottomSheetDialog.findViewById(R.id.layoutTypes);
        ArrayList<CheckBox> cbTypes = new ArrayList<>();  // רשימת סוגים עבור בדיקה
        String[] types = getResources().getStringArray(R.array.arrType);
        for(int i = 0; i< types.length; i++){
            String type = types[i];
            CheckBox cb = new CheckBox(this);
            cb.setText(type);
            layoutTypes.addView(cb);  // הוספה לתצוגה
            cbTypes.add(cb);    // שמירה ברשימה עבור בדיקה

        }
        // titles
        LinearLayout layoutTitles = bottomSheetDialog.findViewById(R.id.layoutTitle);
        ArrayList<CheckBox> cbTitles = new ArrayList<>(); // רשימת תפקידים עבור בדיקה
        String[] titles = getResources().getStringArray(R.array.arrTitle);
        for(int i = 0; i< titles.length; i++){
            String title = titles[i];
            CheckBox cb = new CheckBox(this);
            cb.setText(title);
            layoutTitles.addView(cb);
            cbTitles.add(cb);    // שמירה ברשימה עבור בדיקה
        }

             applied = false; // אתחול לפני לחיצה על אישור

            // ----שמירת תוצאות בArrayList----
            Button btnApplyFilter = bottomSheetDialog.findViewById(R.id.btnApplyFilter);
        if (btnApplyFilter != null) {    // אישור נלחץ
            btnApplyFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> selectedCities = new ArrayList<>(); // הערים שנבחרו
                    for (int i = 0; i < cities.length; i++) {
                        CheckBox cb = cbCites.get(i);
                        if (cb.isChecked()) {
                            selectedCities.add(cb.getText().toString());  // שמירה
                        }
                    }
                    ArrayList<String> selectedTypes = new ArrayList<>(); // הסוגים שנבחרו
                    for (int i = 0; i < types.length; i++) {
                        CheckBox cb = cbTypes.get(i);
                        if (cb.isChecked()) {
                            selectedTypes.add(cb.getText().toString());  // שמירה
                        }
                    }
                    ArrayList<String> selectedTitles = new ArrayList<>(); // התפקידים שנבחרו
                    for (int i = 0; i < titles.length; i++) {
                        CheckBox cb = cbTitles.get(i);
                        if (cb.isChecked()) {
                            selectedTitles.add(cb.getText().toString());  // שמירה
                        }
                    }


                    // טווח גילים
                    List<Float> values = sliderAge.getValues();
                    int minAge = Math.round(values.get(0)); // היד השמאלית
                    int maxAge = Math.round(values.get(1)); // היד הימנית

                 //   filterJobs(selectedCities, selectedTypes, selectedTitles, minAge, maxAge);

                    SharedPreferences.Editor editor = prefs.edit();

                    // ----שמירת הנתונים בSharedPreferences----
                        // שמירת ערים

                    /*/
                      String citiesString = "";
                    for(int i = 0; i < selectedCities.size(); i++){
                        if(i > 0) citiesString += ",";
                        citiesString += selectedCities.get(i);
                    }
                    editor.putString("cities", citiesString);

                    // שמירת סוגים
                    String typesString = "";
                    for(int i = 0; i < selectedTypes.size(); i++){
                        if(i > 0) typesString += ",";
                        typesString += selectedTypes.get(i);
                    }
                    editor.putString("types", typesString);

                    // שמירת תפקידים
                    String titlesString = "";
                    for(int i = 0; i < selectedTitles.size(); i++){
                        if(i > 0) titlesString += ",";
                        titlesString += selectedTitles.get(i);
                    }
                    editor.putString("titles", titlesString);

                     */

                    editor.putString("cities", TextUtils.join(",", selectedCities));
                    editor.putString("types", TextUtils.join(",", selectedTypes));
                    editor.putString("titles", TextUtils.join(",", selectedTitles));
                    // שמירת טווח גיל
                    editor.putInt("minAge", minAge);
                    editor.putInt("maxAge", maxAge);
                    editor.putBoolean("notificationsEnabled", true);


                    editor.apply();
                    applied = true; // משתמש לחץ Apply

                    Intent intent = new Intent(JobNotification.this, JobAlarmService.class);                    intent.setAction("START");
                    startService(intent);  // הפעל התראה
                    bottomSheetDialog.dismiss(); // סגור תפריט
                }
            });
            bottomSheetDialog.setOnDismissListener(dialog -> {
                // אם הדיאלוג נסגר בלי Apply
                if(!applied){
                    swNotification.setChecked(false);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("notificationsEnabled", true);// סוגר את הסוויץ
                }
            });
        }
    }

    /*
       private void setAlarm() {  // מפעיל התראה
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, JobCheckReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        // הפעלה כל 10 שניות
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                1000 * 10,
                pendingIntent
        );
    }
    private void cancelAlarm() { // מפסיק התראות
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
     */


    /*/
    private void filterJobs(ArrayList<String> selectedCities, ArrayList<String> selectedTypes, ArrayList<String> selectedTitles, int minAge, int maxAge){
        ArrayList<Job> filteredJobs = new ArrayList<>();
        for(int i=0; i<approveArraylist.size(); i++) {
            Job job = approveArraylist.get(i);
            boolean match = true;
            if (!selectedCities.isEmpty()) {
                match = match && selectedCities.contains(job.getCity());
            }

            if (!selectedTypes.isEmpty()) {
                match = match && selectedTypes.contains(job.getType());
            }

            if (!selectedTitles.isEmpty()) {
                match = match && selectedTitles.contains(job.getTitle());
            }
            if(Integer.parseInt(job.getAge()) < minAge || Integer.parseInt(job.getAge()) > maxAge)
                match= false;

            if (match) {     // בדיקה האם ישנה עבודה התואמת לדרישות
                filteredJobs.add(job);
            }
        }
    }
     */
}