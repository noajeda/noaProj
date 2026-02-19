package com.example.noaproj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noaproj.adapters.OfferAdapter;
import com.example.noaproj.adapters.UserAdapter;
import com.example.noaproj.model.Job;
import com.example.noaproj.model.User;
import com.example.noaproj.services.DatabaseService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnFilter, btnAnswer, btnChat, btnLogOut, btnUserList, btnJobList, btnMyOffersJobs;
    ImageView imgMenu, imgSearchJob;
    private boolean isMenuOpen = false;
    OfferAdapter adapter;
    RecyclerView rvApproveJobs;
    FrameLayout flMenu;
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;
    ArrayList<Job> approveArraylist = new ArrayList<Job>();

    EditText etSearchJob;
    private static final String TAG = "UserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initListeners();
        Log.d(TAG, "Views initialized");
        databaseService = DatabaseService.getInstance();
        approvejoblist();

    }



    private void initViews(){
        imgMenu = findViewById(R.id.imgMenu);
        btnFilter = findViewById(R.id.btnFilter);
        btnAnswer = findViewById(R.id.btnAnswer);
        btnChat = findViewById(R.id.btnChat);
        btnLogOut = findViewById(R.id.btnLogOut);
        btnUserList = findViewById(R.id.btnUserList);
        btnJobList = findViewById(R.id.btnJobList);
        rvApproveJobs = findViewById(R.id.rv_approve_jobs);
        flMenu = findViewById(R.id.flMenu);

        btnMyOffersJobs = findViewById(R.id.btnMyOffersJobs);
        etSearchJob = findViewById(R.id.etSearchJob);
        imgSearchJob = findViewById(R.id.imgSearchJob);


        rvApproveJobs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OfferAdapter(new OfferAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(Job job) {
                approveArraylist.clear();
                approvejoblist();
            }

            @Override
            public void onLongJobClick(Job job) {

            }
        });
        adapter.setJobList(approveArraylist);
        rvApproveJobs.setAdapter(adapter);


    }
    private void initListeners() {
        imgMenu.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        btnAnswer.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnUserList.setOnClickListener(this);
        btnJobList.setOnClickListener(this);
        imgSearchJob.setOnClickListener(this);

        btnMyOffersJobs.setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        approveArraylist.clear();
        approvejoblist();
    }

    @Override
    public void onClick(View v) {
        if(v ==imgMenu && !isMenuOpen){
            flMenu.setVisibility(View.VISIBLE);
            flMenu.setBackgroundColor(Color.parseColor("#CCFFFFFF"));
            isAdmin();
            isMenuOpen = true;
        }
        else if (v == imgMenu)
        {
            flMenu.setVisibility(View.GONE);
            btnUserList.setVisibility(View.GONE);
            btnJobList.setVisibility(View.GONE);

            flMenu.setBackground(new ColorDrawable(Color.TRANSPARENT));
            isMenuOpen = false;
        }
        if(v == btnUserList){
            Intent goUserList = new Intent(this, userList.class);
            startActivity(goUserList);

        }
        if(v == btnJobList){
            Intent goJobList = new Intent(this, offer_list.class);
            startActivity(goJobList);
        }
        if(v == btnLogOut){
            logOut();
        }
        if(v == btnMyOffersJobs){
            Intent goMyOffersJobs = new Intent(this, MyJobs.class);
            startActivity(goMyOffersJobs);
        }
        if(v == btnFilter){
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(UserActivity.this);
            bottomSheetDialog.setContentView(R.layout.botton_sheet_filter);
            bottomSheetDialog.show();
            showFilter(bottomSheetDialog);
        }
        if(v == imgSearchJob){
            searchJob();
        }

    }

    private void searchJob() {
        String findJob = etSearchJob.getText().toString();   // העבודה המבוקשת
        ArrayList<Job> searchJobList = new ArrayList<>();
        for(int i =0; i < approveArraylist.size(); i++){
            if(approveArraylist.get(i).getCompany().contains(findJob))
                searchJobList.add(approveArraylist.get(i));
        }
        adapter.setJobList(searchJobList);  // רשימה של כל העבודות של חברה מסוימת
    }

    private void showFilter(BottomSheetDialog bottomSheetDialog) {  // filter bottom sheet



        RangeSlider sliderAge = bottomSheetDialog.findViewById(R.id.sliderAge);
        List<Float> initialValues = new ArrayList<>();
        initialValues.add(16f); // יד שמאלית
        initialValues.add(60f); // יד ימנית
        sliderAge.setValues(initialValues);


        // cities
        LinearLayout layoutCities = bottomSheetDialog.findViewById(R.id.layoutCities);
        ArrayList<CheckBox> cbCites = new ArrayList<>();  // רשימת ערים עבור בדיקה
        String[] cities = getResources().getStringArray(R.array.arrCity);
        for(int i = 0; i< cities.length; i++){
            String city = cities[i];
            CheckBox cb = new CheckBox(this);
            cb.setText(city);
            layoutCities.addView(cb);   // הוספה לתצוגה
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
        // btnApply
        Button btnApplyFilter = bottomSheetDialog.findViewById(R.id.btnApplyFilter);
        if (btnApplyFilter != null) {
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

                    filterJobs(selectedCities, selectedTypes, selectedTitles, minAge, maxAge);
                    // סגירה
                    bottomSheetDialog.dismiss();
                }
            });
        }
    }
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
        adapter.setJobList(filteredJobs);
    }
    private void logOut() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(UserActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    private void isAdmin(){       // admin
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            public void onCompleted(User user) {
                Log.d(TAG, "onCompleted: id:" + uid);
                if(user!=null) {
                    if (user.getIsAdmin()) {
                        btnUserList.setVisibility(View.VISIBLE);     // show btnUserList
                        btnJobList.setVisibility(View.VISIBLE);      // show btnJobList
                    }
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to admin", e);
            }
        });
    }
    private void approvejoblist() {
        databaseService = DatabaseService.getInstance();
        databaseService.getJobList(new DatabaseService.DatabaseCallback<List<Job>>() {
                @Override
                public void onCompleted(List<Job> jobList) {
                    approveArraylist.clear();
                    for (int i= 0; i<jobList.size(); i++){
                        if(jobList.get(i).getStatus().contains("approve"))
                            approveArraylist.add(jobList.get(i));
                        Log.d(TAG, "Job ID: " + jobList.get(i).getId() + ", Status: " + jobList.get(i).getStatus());

                    }
                    adapter.setJobList(approveArraylist);
                    Log.d(TAG, "onCompleted: " + approveArraylist);
                }

                @Override
                public void onFailed(Exception e) {

                }
            });
        }
    }
