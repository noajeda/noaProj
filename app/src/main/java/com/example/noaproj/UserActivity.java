package com.example.noaproj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnSearch, btnFilter, btnOffer, btnAnswer, btnChat, btnLogOut, btnUserList, btnJobList, btnMyOffersJobs;
    ImageView imgMenu;
    private boolean isMenuOpen = false;
    OfferAdapter adapter;
    RecyclerView rvApproveJobs;
    FrameLayout flMenu;
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;
    ArrayList<Job> approveArraylist = new ArrayList<Job>();

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
        btnSearch = findViewById(R.id.btnSearch);
        btnFilter = findViewById(R.id.btnFilter);
        btnOffer = findViewById(R.id.btnOffer);
        btnAnswer = findViewById(R.id.btnAnswer);
        btnChat = findViewById(R.id.btnChat);
        btnLogOut = findViewById(R.id.btnLogOut);
        btnUserList = findViewById(R.id.btnUserList);
        btnJobList = findViewById(R.id.btnJobList);
        rvApproveJobs = findViewById(R.id.rv_approve_jobs);
        flMenu = findViewById(R.id.flMenu);

        btnMyOffersJobs = findViewById(R.id.btnMyOffersJobs);

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
        btnSearch.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        btnOffer.setOnClickListener(this);
        btnAnswer.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnUserList.setOnClickListener(this);
        btnJobList.setOnClickListener(this);

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
            btnSearch.setVisibility(View.VISIBLE);
            btnFilter.setVisibility(View.VISIBLE);
            btnOffer.setVisibility(View.VISIBLE);
            btnAnswer.setVisibility(View.VISIBLE);
            btnChat.setVisibility(View.VISIBLE);
            btnLogOut.setVisibility(View.VISIBLE);
            btnMyOffersJobs.setVisibility(View.VISIBLE);
            flMenu.setBackgroundColor(Color.parseColor("#CCFFFFFF"));
            isAdmin();
            isMenuOpen = true;
        }
        else if (v == imgMenu)
        {
            btnSearch.setVisibility(View.GONE);
            btnFilter.setVisibility(View.GONE);
            btnOffer.setVisibility(View.GONE);
            btnAnswer.setVisibility(View.GONE);
            btnChat.setVisibility(View.GONE);
            btnLogOut.setVisibility(View.GONE);
            btnUserList.setVisibility(View.GONE);
            btnJobList.setVisibility(View.GONE);
            btnMyOffersJobs.setVisibility(View.GONE);

            flMenu.setBackground(new ColorDrawable(Color.TRANSPARENT));
            isMenuOpen = false;
        }
        if(v == btnOffer){
            Intent goOffer = new Intent(this, SubmitOfferActivity.class);
            startActivity(goOffer);

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

    }
    private void logOut() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(UserActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    private void isAdmin(){
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            public void onCompleted(User user) {
                Log.d(TAG, "onCompleted: id:" + uid);
                if(user!=null) {
                    if (user.getIsAdmin()) {
                        btnUserList.setVisibility(View.VISIBLE);
                        btnJobList.setVisibility(View.VISIBLE);
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
                    for (int i= 0; i<jobList.size(); i++){
                        if(jobList.get(i).getStatus().contains("approve"))
                            approveArraylist.add(jobList.get(i));
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
