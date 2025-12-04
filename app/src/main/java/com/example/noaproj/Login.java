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
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        /// get the views
            etEmail = findViewById(R.id.etLoginEmail);
            etPassword = findViewById(R.id.etLoginPassword);
            btnLogin = findViewById(R.id.btnLoginSubmit);
            tvRegister = findViewById(R.id.tvLogToReg);

        email2=sharedpreferences.getString("email","");
        pass2=sharedpreferences.getString("password","");
        etEmail.setText(email2);
        etPassword.setText(pass2);


        /// set the click listener
            btnLogin.setOnClickListener(this);
            tvRegister.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == btnLogin.getId()) {
                Log.d(TAG, "onClick: Login button clicked");

                /// get the email and password entered by the user
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                /// log the email and password
                Log.d(TAG, "onClick: Email: " + email);
                Log.d(TAG, "onClick: Password: " + password);

                Log.d(TAG, "onClick: Validating input...");
                /// Validate input
                /*/ if (!checkInput(email, password)) {
                    /// stop if input is invalid
                    return;
                } /*/

                Log.d(TAG, "onClick: Logging in user...");

                /// Login user
                loginUser(email, password);
            } else if (v.getId() == tvRegister.getId()) {
                /// Navigate to Register Activity
                Intent registerIntent = new Intent(Login.this, Register.class);
                startActivity(registerIntent);
            }
        }

        private void loginUser(String email, String password) {
            DatabaseService.LoginUser(email, password, new DatabaseService.DatabaseCallback<String>() {
                /// Callback method called when the operation is completed
                @Override
                public void onCompleted(String  uid) {

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.commit();


                    Log.d(TAG, "onCompleted: User logged in: " + uid.toString());
                    /// save the user data to shared preferences
                    /// Redirect to main activity and clear back stack to prevent user from going back to login screen
                    Intent mainIntent = new Intent(Login.this, UserActivity.class);
                    /// Clear the back stack (clear history) and start the MainActivity

                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);

                }

                @Override
                public void onFailed(Exception e) {
                    Log.e(TAG, "onFailed: Failed to retrieve user data", e);
                    /// Show error message to user
                    etPassword.setError("Invalid email or password");
                    etPassword.requestFocus();
                    /// Sign out the user if failed to retrieve user data
                    /// This is to prevent the user from being logged in again
                }
            });
        }
    }