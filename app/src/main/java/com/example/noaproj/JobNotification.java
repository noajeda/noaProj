package com.example.noaproj;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.noaproj.services.JobAlarmService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

public class JobNotification extends AppCompatActivity {
    private SharedPreferences prefs;
    Switch swNotification;
    BottomSheetDialog bottomSheetDialog;
    boolean applied;
    private static final int NOTIFICATION_PERMISSION_CODE = 1;


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

    private void initViews() {
        swNotification = findViewById(R.id.swNotification);
        bottomSheetDialog = new BottomSheetDialog(JobNotification.this);
        bottomSheetDialog.setContentView(R.layout.botton_sheet_filter);

        prefs = getSharedPreferences("jobFilter", MODE_PRIVATE);
        boolean enabled = prefs.getBoolean("notificationsEnabled", false);  // האם המתג הופעל בעבר
        swNotification.setChecked(enabled); // השאר מופעל/כבוי

    }

    private void initListeners() {
        swNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){ // אם המתג הופעל
                checkNotificationPermission();  // בדיקת הרשאת התראות

                // נעצר אם אין הרשאה
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                                != PackageManager.PERMISSION_GRANTED) {
                    swNotification.setChecked(false);
                    return;
                }


                bottomSheetDialog.show();  // הצג את הדיאלוג
                showFilter(bottomSheetDialog);

            }
            else{
                prefs.edit().putBoolean("notificationsEnabled", false).apply(); // מעדכן שהמתג נכבה
                // מכבה את ההתראה
                Intent intent = new Intent(JobNotification.this, JobAlarmService.class);
                intent.setAction("STOP");
                startService(intent);
            }
        });
    }

    // יצירת bottomSheetDialog
    private void showFilter(BottomSheetDialog bottomSheetDialog) {

        LinearLayout layoutCities = bottomSheetDialog.findViewById(R.id.layoutCities);
        LinearLayout layoutTypes = bottomSheetDialog.findViewById(R.id.layoutTypes);
        LinearLayout layoutTitles = bottomSheetDialog.findViewById(R.id.layoutTitle);

        //  ניקוי כל התוכן הקודם
        layoutCities.removeAllViews();
        layoutTypes.removeAllViews();
        layoutTitles.removeAllViews();

        // ages
        RangeSlider sliderAge = bottomSheetDialog.findViewById(R.id.sliderAge);
        List<Float> initialValues = new ArrayList<>();
        initialValues.add(16f); // יד שמאלית
        initialValues.add(60f); // יד ימנית
        sliderAge.setValues(initialValues);

        // cities
        ArrayList<CheckBox> cbCites = new ArrayList<>();  // רשימת ערים עבור בדיקה
        String[] cities = getResources().getStringArray(R.array.arrCity); // מערך המכיל את כלל הערים
        for(int i = 0; i< cities.length; i++){
            String city = cities[i];
            CheckBox cb = new CheckBox(this);
            cb.setText(city);
            layoutCities.addView(cb);   // הוספת הרשימה לתצוגה
            cbCites.add(cb);    // שמירה ברשימה עבור בדיקה
        }
        // types
        ArrayList<CheckBox> cbTypes = new ArrayList<>();  // רשימת סוגים עבור בדיקה
        String[] types = getResources().getStringArray(R.array.arrType); // מערך המכיל את כלל הסוגים
        for(int i = 0; i< types.length; i++){
            String type = types[i];
            CheckBox cb = new CheckBox(this);
            cb.setText(type);
            layoutTypes.addView(cb);  // הוספה לתצוגה
            cbTypes.add(cb);    // שמירה ברשימה עבור בדיקה

        }
        // titles
        ArrayList<CheckBox> cbTitles = new ArrayList<>(); // רשימת תפקידים עבור בדיקה
        String[] titles = getResources().getStringArray(R.array.arrTitle); // מערך המכיל את כלל התפקידים
        for(int i = 0; i< titles.length; i++){
            String title = titles[i];
            CheckBox cb = new CheckBox(this);
            cb.setText(title);
            layoutTitles.addView(cb); // הוספה לתצוגה
            cbTitles.add(cb);    // שמירה ברשימה עבור בדיקה
        }

        applied = false; // אתחול לפני לחיצה על אישור

            // ----שמירת תוצאות בArrayList----
            Button btnApplyFilter = bottomSheetDialog.findViewById(R.id.btnApplyFilter);
        if (btnApplyFilter != null) {    // לחיצה על אישור
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

                    // שמירת כל הפרטים בהם בחר המשתמש ב-sharedPreferences
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("cities", TextUtils.join(",", selectedCities));
                    editor.putString("types", TextUtils.join(",", selectedTypes));
                    editor.putString("titles", TextUtils.join(",", selectedTitles));
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
                // אם הדיאלוג נסגר ללא לחיצה על Apply
                if(!applied){
                    swNotification.setChecked(false);
                    prefs.edit().putBoolean("notificationsEnabled", false).apply();
                }
            });
        }
    }
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE
                );
            }
        }
    }

    // ---- שליחת הודעה למשתמש בעת אישור/דחיית ההרשאה ----
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            // אם יש תוצאה והיא מתן הרשאה
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "הרשאה ניתנה!", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(this, "הרשאה נדחתה!", Toast.LENGTH_SHORT).show();

                // אם המשתמש דחה – מכבים את המתג
                swNotification.setChecked(false);
                prefs.edit().putBoolean("notificationsEnabled", false).apply();
            }
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