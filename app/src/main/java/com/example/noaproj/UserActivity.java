package com.example.noaproj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.noaproj.model.Job;
import com.example.noaproj.model.User;
import com.example.noaproj.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
   Button btnSearch, btnFilter, btnOffer, btnAnswer, btnChat, btnLogOut, btnUserList, btnJobList;
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;

    private static final String TAG = "UserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Log.d(TAG, "onCreate started");
            // כל הקוד שלך כאן...
        } catch (Exception e) {
            Log.e(TAG, "Exception in onCreate", e);
        }
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

            mAuth = FirebaseAuth.getInstance();
            String uid = mAuth.getCurrentUser().getUid();
            databaseService = DatabaseService.getInstance();

        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            public void onCompleted(User user) {
                Log.d(TAG, "onCompleted: id:" + uid);
                if (user.getIsAdmin()) {
                    btnUserList.setVisibility(View.VISIBLE);
                    btnJobList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to admin", e);
            }
        });

    }
    private void initViews(){
        btnSearch = findViewById(R.id.btnSearch);
        btnFilter = findViewById(R.id.btnFilter);
        btnOffer = findViewById(R.id.btnOffer);
        btnAnswer = findViewById(R.id.btnAnswer);
        btnChat = findViewById(R.id.btnChat);
        btnLogOut = findViewById(R.id.btnLogOut);
        btnUserList = findViewById(R.id.btnUserList);
        btnJobList = findViewById(R.id.btnJobList);
    }
    private void initListeners() {
        btnSearch.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        btnOffer.setOnClickListener(this);
        btnAnswer.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnUserList.setOnClickListener(this);
        btnJobList.setOnClickListener(this);
    }

        @Override
    public void onClick(View v) {
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


    }
}