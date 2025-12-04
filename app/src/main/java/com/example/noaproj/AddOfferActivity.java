package com.example.noaproj;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.noaproj.model.Job;
import com.example.noaproj.model.User;
import com.example.noaproj.services.DatabaseService;

public class AddOfferActivity extends AppCompatActivity {
    private static final String TAG = "AddOfferActivity";
    private DatabaseService databaseService;
    EditText etJobCity, etJobAddress, etjobTitle, etJobPhone, etJobAge, etJobDetails;
    String jobCity, jobAddress, jobTitle, jobPhone, jobAge, jobDetails;
    Button btnSubmitOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_offer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etJobCity = findViewById(R.id.etJobCity);
        etJobAddress = findViewById(R.id.etJobAddress);
        etjobTitle = findViewById(R.id.etjobTitle);
        etJobPhone = findViewById(R.id.etJobPhone);
        etJobAge = findViewById(R.id.etJobAge);
        etJobDetails = findViewById(R.id.etJobDetails);
        btnSubmitOffer = findViewById(R.id.btnSubmitOffer);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == btnSubmitOffer.getId()) {
            Log.d(TAG, "onClick: Add Offer button clicked");

            /// get the input from the user
            jobCity = etJobCity.getText().toString();
            jobAddress = etJobAddress.getText().toString();
            jobTitle = etjobTitle.getText().toString();
            jobPhone = etJobPhone.getText().toString();
            jobAge = etJobAge.getText().toString();
            jobDetails = etJobDetails.getText().toString();

            databaseService = DatabaseService.getInstance();

            Log.d(TAG, "onClick: Adding offer...");

            /// Register user
            SubmitOffer(jobCity, jobAddress, jobTitle, jobPhone, jobAge, jobDetails);
        }
    }
    private void SubmitOffer(String jobCity, String jobAddress, String jobTitle, String jobPhone, String jobAge, String jobDetails) {
        Log.d(TAG, "registerUser: Registering user...");

        String uid = databaseService.generateUserId();

        /// create a new user object
        Job job = new User(jobAddress, jobAge, jobCity, jobDetails, uid, jobPhone, jobTitle, type);
        AddJobRequestToDatabase(job);
    }

    private void AddJobRequestToDatabase(Job job) {
    }

}