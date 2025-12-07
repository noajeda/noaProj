package com.example.noaproj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
   Button btnSearch, btnFilter, btnOffer, btnAnswer, btnChat, btnLogOut;

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
        btnSearch = findViewById(R.id.btnSearch);
        btnFilter = findViewById(R.id.btnFilter);
        btnOffer = findViewById(R.id.btnOffer);
        btnAnswer = findViewById(R.id.btnAnswer);
        btnChat = findViewById(R.id.btnChat);
        btnLogOut = findViewById(R.id.btnLogOut);
        btnSearch.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        btnOffer.setOnClickListener(this);
        btnAnswer.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if(v == btnOffer){
            Intent goOffer = new Intent(this, SubmitOfferActivity.class);
            startActivity(goOffer);

        }

    }
}