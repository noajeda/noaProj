package com.example.noaproj;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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

    private ArrayList<CheckBox> cbCities = new ArrayList<>();
    private ArrayList<CheckBox> cbTypes = new ArrayList<>();
    private ArrayList<CheckBox> cbTitles = new ArrayList<>();
    private RangeSlider sliderAge;

    String[] cities, types, titles;
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
        initData();
        setupBottomSheet();
        initSwitchListener();

    }

    private void initViews() {
        swNotification = findViewById(R.id.swNotification);
        bottomSheetDialog = new BottomSheetDialog(JobNotification.this);
        bottomSheetDialog.setContentView(R.layout.botton_sheet_filter);
    }
    private void initData(){
        prefs = getSharedPreferences("jobFilter", MODE_PRIVATE);
        boolean enabled = prefs.getBoolean("notificationsEnabled", false);  // האם המתג הופעל בעבר
        swNotification.setChecked(enabled); // השאר מופעל/כבוי
    }

    private void initSwitchListener() {
        swNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                applied = false; // איפוס לפני פתיחה

                // בדיקת האם יש הרשאה
                if (hasNotificationPermission()) {
                    bottomSheetDialog.show(); // הצג דיאלוג
                }
                else { // אין הרשאה
                    requestNotificationPermission();
                }
            }
            else {
                prefs.edit().clear().apply(); // כולל ניקוי הids של העבודות שנשלחו

                // מכבה את ההתראה
                Intent stopAlarmService = new Intent(JobNotification.this, JobAlarmService.class);
                stopAlarmService.setAction("STOP");
                startService(stopAlarmService);
            }
        });
    }


    // ---- בניית הדיאלוג ----
    private void setupBottomSheet() {
        LinearLayout layoutCities = bottomSheetDialog.findViewById(R.id.layoutCities);
        LinearLayout layoutTypes = bottomSheetDialog.findViewById(R.id.layoutTypes);
        LinearLayout layoutTitles = bottomSheetDialog.findViewById(R.id.layoutTitle);

        cities = getResources().getStringArray(R.array.arrCity); // מערך המכיל את כלל הערים
        types = getResources().getStringArray(R.array.arrType); // מערך המכיל את כלל הסוגים
        titles = getResources().getStringArray(R.array.arrTitle); // מערך המכיל את כלל התפקידים


        // ניקוי התצוגה כדי שלא יהיו כפילויות
        layoutCities.removeAllViews();
        layoutTypes.removeAllViews();
        layoutTitles.removeAllViews();
        // ניקוי הרשימות כדי שלא יכילו אובייקטים ישנים
        cbCities.clear();
        cbTypes.clear();
        cbTitles.clear();

        // ages
        sliderAge = bottomSheetDialog.findViewById(R.id.sliderAge);
        List<Float> initialValues = new ArrayList<>();
        initialValues.add(16f); // יד שמאלית
        initialValues.add(60f); // יד ימנית
        sliderAge.setValues(initialValues);

        // cities
        for(int i = 0; i < cities.length; i++){
            String city = cities[i];
            CheckBox cb = new CheckBox(this);
            cb.setText(city);
            layoutCities.addView(cb);   // הוספה לתצוגה
            cbCities.add(cb);    // שמירה ברשימה עבור בדיקה
        }

        // types
        for(int i = 0; i < types.length; i++){
            String type = types[i];
            CheckBox cb = new CheckBox(this);
            cb.setText(type);
            layoutTypes.addView(cb);  // הוספה לתצוגה
            cbTypes.add(cb);    // שמירה ברשימה עבור בדיקה
        }

        // titles
        for(int i = 0; i < titles.length; i++){
            String title = titles[i];
            CheckBox cb = new CheckBox(this);
            cb.setText(title);
            layoutTitles.addView(cb); // הוספה לתצוגה
            cbTitles.add(cb);    // שמירה ברשימה עבור בדיקה
        }

        // הגדרת כפתור האישור
        Button btnApplyFilter = bottomSheetDialog.findViewById(R.id.btnApplyFilter);
        if (btnApplyFilter != null) {
            btnApplyFilter.setOnClickListener(v -> applyFilter());
        }

        // הגדרת כפתור הניקוי
        Button btnCleanFilter = bottomSheetDialog.findViewById(R.id.btnCleanFilter);
        if (btnCleanFilter != null) {
            btnCleanFilter.setOnClickListener(v-> cleanFilter());
        }

        bottomSheetDialog.setOnDismissListener(dialog -> {
            // אם הדיאלוג נסגר ללא לחיצה על Apply
            if(!applied){
                swNotification.setChecked(false);
                prefs.edit().putBoolean("notificationsEnabled", false).apply();
            }
        });
    }

    // ---- הפעלת הסינון ----
    private void applyFilter() {
        // ---- שמירת התוצאות ברשימה ----
        ArrayList<String> selectedCities = new ArrayList<>(); // הערים שנבחרו
        for (int i = 0; i < cities.length; i++) {
            CheckBox cb = cbCities.get(i);
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

        Intent startAlarmService = new Intent(JobNotification.this, JobAlarmService.class);
        startAlarmService.setAction("START");
        startService(startAlarmService);  // הפעל התראה

        bottomSheetDialog.dismiss(); // סגור תפריט
    }

    // ---- ניקוי הסיינון ----
    private void cleanFilter(){
        for (CheckBox cb : cbCities) {
            cb.setChecked(false);
        }

        for (CheckBox cb : cbTypes) {
            cb.setChecked(false);
        }

        for (CheckBox cb : cbTitles) {
            cb.setChecked(false);
        }

        List<Float> resetValues = new ArrayList<>();
        resetValues.add(16f);
        resetValues.add(60f);
        sliderAge.setValues(resetValues);
    }

    // ---- האם יש הרשאה ----
    private boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    // ---- בקשת הרשאה ----
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_CODE
            );
        }
    }

    // ---- שליחת הודעה למשתמש בעת אישור/דחיית ההרשאה ----
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            // יש הרשאה
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "הרשאה ניתנה!", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.show();  // הצג את הדיאלוג
            }
            else { // אין הרשאה
                Toast.makeText(this, "הרשאה נדחתה!", Toast.LENGTH_SHORT).show();
                swNotification.setChecked(false);
                prefs.edit().putBoolean("notificationsEnabled", false).apply();
            }
        }
    }

}