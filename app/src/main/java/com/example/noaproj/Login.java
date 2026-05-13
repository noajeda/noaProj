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
import android.widget.Toast;

import com.example.noaproj.services.DatabaseService;


public class Login extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    String email1, password1;

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
        initData();
        initListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLoginSubmit);
        tvRegister = findViewById(R.id.tvLogToReg);
    }
    private void initData(){
        // כתיבה אוטומטית של פריטh ההתחברות הקודמים, במידה וקיימים
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        email1=sharedpreferences.getString("email","");
        password1=sharedpreferences.getString("password","");
        etEmail.setText(email1);
        etPassword.setText(password1);
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
            if(email.isEmpty() || password.isEmpty())
                Toast.makeText(Login.this,"יש למלא את שני השדות", Toast.LENGTH_SHORT).show();
            else
                loginUser(email, password); // חיבור המשתמש
        }
        if (v == tvRegister) { // לחיצה על הרשמה
            Intent goRegister = new Intent(Login.this, Register.class);
            startActivity(goRegister); // מעבר למסך הרשמה
        }
    }

    // ---- חיבור המשתמש ----
    private void loginUser(String email, String password) {
        DatabaseService.LoginUser(email, password, new DatabaseService.DatabaseCallback<String>() { // חיבור המשתמש
            @Override
            public void onCompleted(String  uid) {

                // שמירת האימייל והסיסמה של המשתמש המחובר ב-sharedpreferences
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();

                Intent goMain = new Intent(Login.this, UserActivity.class); // מעבר למסך המשתמש
                goMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // מחיקת היסטוריית המסכים הקודמים
                startActivity(goMain);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to retrieve user data", e);
                etPassword.setError("הסיסמה או האימייל אינם נכונים"); // הצגת הודעת שגיאה למשתמש
                etPassword.requestFocus();
            }
        });
    }
}