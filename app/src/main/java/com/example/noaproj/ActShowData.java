package com.example.noaproj;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ActShowData extends AppCompatActivity {
    TextView tvInfo;
    String fname, lname, password, email, phone;
    Intent takeit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_act_show_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        takeit =getIntent();
        tvInfo = findViewById(R.id.tvInfo);
        fname = takeit.getStringExtra("fname");
        lname = takeit.getStringExtra("lname");
        password = takeit.getStringExtra("password");
        email = takeit.getStringExtra("email");
        phone = takeit.getStringExtra("phone");
        tvInfo.setText(fname + "\n" + lname + "\n" + password + "\n" + email + "\n" + phone);
    }
}