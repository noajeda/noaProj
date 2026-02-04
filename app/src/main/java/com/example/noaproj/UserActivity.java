package com.example.noaproj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
   ImageView imgMenu;
    private boolean isMenuOpen = false;

    private DatabaseService databaseService;
    private FirebaseAuth mAuth;


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
}
