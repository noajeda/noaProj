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
   Button btnSearch, btnFilter, btnOffer, btnAnswer, btnChat, btnLogOut, btnUserList;
    private DatabaseService databaseService;
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid= mAuth.getCurrentUser().getUid();
        databaseService = DatabaseService.getInstance();

       initViews();
       initListeners();


        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            public void onCompleted(User user) {
                if(user.getIsAdmin())
                {
                    btnUserList.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "userActivity: Failed to load user", e);
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
    }
    private void initListeners() {
        btnSearch.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        btnOffer.setOnClickListener(this);
        btnAnswer.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnUserList.setOnClickListener(this);
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

    }
}