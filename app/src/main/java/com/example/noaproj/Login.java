package com.example.noaproj;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import com.example.noaproj.services.DatabaseService;


public class Login extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    String email2, pass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        initListeners();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        email2=sharedpreferences.getString("email","");
        pass2=sharedpreferences.getString("password","");
        etEmail.setText(email2);
        etPassword.setText(pass2);
    }

    private void initViews() {
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLoginSubmit);
        tvRegister = findViewById(R.id.tvLogToReg);
    }

    private void initListeners() {
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin) { // לחיצה על התחברות
            Log.d(TAG, "onClick: Login button clicked");

            // קבלת המייל והסיסמה שהמשתמש הזין
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            loginUser(email, password); // חיבור המשתמש
        }
        if (v == tvRegister) { // לחיצה על הרשמה
            Intent goRegister = new Intent(Login.this, Register.class);
            startActivity(goRegister); // מעבר למסך הרשמה
        }
    }

    private void loginUser(String email, String password) {
        DatabaseService.LoginUser(email, password, new DatabaseService.DatabaseCallback<String>() { // חיבור המשתמש
            @Override
            public void onCompleted(String  uid) {

                // שמירת האימייל והסיסמה של המשתמש המחובר ב-sharedpreferences
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.commit();

                Intent mainIntent = new Intent(Login.this, UserActivity.class); // מעבר למסך המשתמש
                mainIntent.putExtra("uid", uid);  // מעבירים את המזהה היחודי של המשתמש
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // מחיקת היסטוריית המסכים הקודמים
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to retrieve user data", e);
                etPassword.setError("Invalid email or password"); // הצגת הודעת שגיאה למשתמש
                etPassword.requestFocus();
            }
        });
    }
}