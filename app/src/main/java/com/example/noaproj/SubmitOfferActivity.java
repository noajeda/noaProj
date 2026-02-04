package com.example.noaproj;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.noaproj.model.Job;
import com.example.noaproj.model.User;
import com.example.noaproj.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

public class SubmitOfferActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddOfferActivity";
    private DatabaseService databaseService;
    EditText etCompany, etJobAddress, etJobPhone, etJobAge, etJobDetails, etJobType;
    String jobCity, jobAddress, jobTitle, jobPhone, jobAge, jobDetails, jobType, company;
    Button btnSubmitOffer;

    Spinner spCity,spTitle, spType;

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

        etCompany = findViewById(R.id.etCompany);
        spCity = findViewById(R.id.spCity);
        spType = findViewById(R.id.spType);
        spTitle = findViewById(R.id.spTitle);
        etJobAddress = findViewById(R.id.etJobAddress);
        etJobPhone = findViewById(R.id.etJobPhone);
        etJobAge = findViewById(R.id.etJobAge);
        etJobDetails = findViewById(R.id.etJobDetails);
        btnSubmitOffer = findViewById(R.id.btnSubmitOffer);

        btnSubmitOffer.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnSubmitOffer.getId()) {
            Log.d(TAG, "onClick: Add Offer button clicked");

            company = etCompany.getText().toString();
            jobCity=spCity.getSelectedItem().toString();
            jobType = spType.getSelectedItem().toString();
            jobTitle = spTitle.getSelectedItem().toString();
            jobAddress = etJobAddress.getText().toString();
            jobPhone = etJobPhone.getText().toString();
            jobAge = etJobAge.getText().toString();
            jobDetails = etJobDetails.getText().toString();


            databaseService = DatabaseService.getInstance();

            Log.d(TAG, "onClick: submitting offer...");

            /// Register user
            SubmitOffer(company, jobCity,jobType, jobTitle, jobAddress, jobPhone, jobAge, jobDetails);
        }
    }

    private void SubmitOffer(String company, String jobCity, String jobType, String jobTitle, String jobAddress, String jobPhone, String jobAge, String jobDetails) {
        Log.d(TAG, "registerUser: Registering user...");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid= mAuth.getCurrentUser().getUid();

        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            public void onCompleted(User user) {

                user=new User(user);

                String jobId=databaseService.generateJobId();
                Job job = new Job(jobAddress, jobAge, jobCity, company,jobDetails, jobId, jobPhone, jobTitle, jobType, user);
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