package com.example.noaproj;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.noaproj.model.User;
import com.example.noaproj.services.DatabaseService;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";
    private DatabaseService databaseService;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;



    Button btnSubmit;
    EditText etFname, etLname, etPassword, etEmail, etPhone, etCity, etGender, etAge;
    String fname, lname, password, email, phone, city, gender, age;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        btnSubmit = findViewById(R.id.btnSubmit);
        etFname = findViewById(R.id.etFname);
        etLname = findViewById(R.id.etLname);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etCity = findViewById(R.id.etCity);
        etGender = findViewById(R.id.etGender);
        etAge = findViewById(R.id.etAge);
        btnSubmit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == btnSubmit.getId()) {
            Log.d(TAG, "onClick: Register button clicked");

            /// get the input from the user
            fname = etFname.getText().toString();
            lname = etLname.getText().toString();
            password = etPassword.getText().toString();
            email = etEmail.getText().toString();
            phone = etPhone.getText().toString();
            city = etCity.getText().toString();
            age = etAge.getText().toString();
            gender = etGender.getText().toString();

            databaseService = DatabaseService.getInstance();

            Log.d(TAG, "onClick: Registering user...");

            /// Register user
            registerUser(fname, lname, phone, email, password, age,gender, city);
        }
    }


    /// Register the user
    private void registerUser(String fname, String lname, String phone, String email, String password, String age, String gender, String city) {
        Log.d(TAG, "registerUser: Registering user...");

        String uid = databaseService.generateUserId();

        /// create a new user object
        User user = new User(age, city, email, fname, gender, uid, lname, password, phone);
            createUserInDatabase(user);
        }

    private void createUserInDatabase(User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "createUserInDatabase: User created successfully");
                /// save the user to shared preferences

                Log.d(TAG, "createUserInDatabase: Redirecting to MainActivity");
                /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.commit();


                Intent mainIntent = new Intent(Register.this, UserActivity.class);
                /// clear the back stack (clear history) and start the MainActivity
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                /// show error message to user
                Toast.makeText(Register.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                /// sign out the user if failed to register

            }
        });
    }
}
