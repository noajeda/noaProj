package com.example.noaproj;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.example.noaproj.model.Job;
import com.example.noaproj.model.User;
import com.example.noaproj.services.DatabaseService;

public class SubmitOfferActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddOfferActivity";
    private DatabaseService databaseService;
    EditText etJobCity, etJobAddress, etJobTitle, etJobPhone, etJobAge, etJobDetails, etJobType;
    String jobCity, jobAddress, jobTitle, jobPhone, jobAge, jobDetails, jobType;
    Button btnSubmitOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_offer_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        etJobCity = findViewById(R.id.etJobCity);
        etJobAddress = findViewById(R.id.etJobAddress);
        etJobTitle = findViewById(R.id.etjobTitle);
        etJobPhone = findViewById(R.id.etJobPhone);
        etJobAge = findViewById(R.id.etJobAge);
        etJobDetails = findViewById(R.id.etJobDetails);
        btnSubmitOffer = findViewById(R.id.btnSubmitOffer);
        etJobType = findViewById(R.id.etJobType);

        btnSubmitOffer.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnSubmitOffer.getId()) {
            Log.d(TAG, "onClick: Add Offer button clicked");

            /// get the input from the user
            jobCity = etJobCity.getText().toString();
            jobAddress = etJobAddress.getText().toString();
            jobTitle = etJobTitle.getText().toString();
            jobPhone = etJobPhone.getText().toString();
            jobAge = etJobAge.getText().toString();
            jobDetails = etJobDetails.getText().toString();
            jobType = etJobType.getText().toString();

            databaseService = DatabaseService.getInstance();

            Log.d(TAG, "onClick: submitting offer...");

            /// Register user
            SubmitOffer(jobCity, jobAddress, jobTitle, jobPhone, jobAge, jobDetails, jobType);
        }
    }

    private void SubmitOffer(String jobCity, String jobAddress, String jobTitle, String jobPhone, String jobAge, String jobDetails, String jobType) {
        Log.d(TAG, "registerUser: Registering user...");
        String uid = databaseService.generateUserId();

        DatabaseService.getInstance().getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            public void onCompleted(User user) {

                Job job = new Job(jobAddress, jobAge, jobCity, jobDetails, uid, jobPhone, jobTitle, jobType, user);
                createJobInDatabase(job);

            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "SubmitOffer: Failed to load user", e);
            }
        });
    }



    private void createJobInDatabase(Job job) {
        databaseService.createNewJob(job, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "createJobInDatabase: Job created successfully");
                /// save the user to shared preferences

                Log.d(TAG, "createJobInDatabase: Redirecting to MainActivity");
                /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen



                Intent mainIntent = new Intent(SubmitOfferActivity.this, UserActivity.class);
                /// clear the back stack (clear history) and start the MainActivity
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }
            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                /// show error message to user
                Toast.makeText(SubmitOfferActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                /// sign out the user if failed to register

            }
        });
    }
}