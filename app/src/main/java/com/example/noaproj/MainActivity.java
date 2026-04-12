package com.example.noaproj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
        initViews();
        initListeners();

    }

    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnAboutApp = findViewById(R.id.btnAbout);
    }

    private void initListeners() {
        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        btnAboutApp.setOnClickListener(this);
    }


    // ---- יצירת מעבר ל3 מסכים ----
    @Override
    public void onClick(View v) {
        if (v == btnLogin)  // מסך התחברות
        {
            Intent goLog = new Intent(this, Login.class);
            startActivity(goLog);
        }
        if (v == btnSignup) // מסך הרשמה
        {
            Intent goReg = new Intent(this, Register.class);
            startActivity(goReg);
        }
        if (v == btnAboutApp) // מסך אודות
        {
            Intent goAb = new Intent(this, AboutApp.class);
            startActivity(goAb);
        }
    }
}