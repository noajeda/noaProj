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


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnLogin, btnSignup, btnAboutApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnAboutApp = findViewById(R.id.btnAbout);
        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        btnAboutApp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin)
        {
            Intent goLog = new Intent(this, Login.class);
            startActivity(goLog);
        }
        if (v == btnSignup)
        {
            Intent goReg = new Intent(this, Register.class);
            startActivity(goReg);
        }
        if (v == btnAboutApp)
        {
            Intent goAb = new Intent(this, AboutApp.class);
            startActivity(goAb);
        }
    }
}