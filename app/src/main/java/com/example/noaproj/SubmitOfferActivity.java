package com.example.noaproj;

import android.content.Intent;
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

        initViews();
        initListeners();
        databaseService = DatabaseService.getInstance();
    }

    private void initViews() {
        etCompany = findViewById(R.id.etCompany);
        spCity = findViewById(R.id.spCity);
        spType = findViewById(R.id.spType);
        spTitle = findViewById(R.id.spTitle);
        etJobAddress = findViewById(R.id.etJobAddress);
        etJobPhone = findViewById(R.id.etJobPhone);
        etJobAge = findViewById(R.id.etJobAge);
        etJobDetails = findViewById(R.id.etJobDetails);
        btnSubmitOffer = findViewById(R.id.btnSubmitOffer);
    }

    private void initListeners() {
        btnSubmitOffer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmitOffer) { // לחיצה על כפתור הגשת עבודה
            Log.d(TAG, "onClick: submitOffer button clicked");

            // שמירת הקלט שהמשתמש הזין
            company = etCompany.getText().toString();
            jobCity=spCity.getSelectedItem().toString();
            jobType = spType.getSelectedItem().toString();
            jobTitle = spTitle.getSelectedItem().toString();
            jobAddress = etJobAddress.getText().toString();
            jobPhone = etJobPhone.getText().toString();
            jobAge = etJobAge.getText().toString();
            jobDetails = etJobDetails.getText().toString();

            Log.d(TAG, "onClick: submitting offer...");
            SubmitOffer(company, jobCity,jobType, jobTitle, jobAddress, jobPhone, jobAge, jobDetails); // הגשת ההצעה לפי הנתונים שנקלטו
        }
    }

    // ---- הגשת הצעת עבודה ----
    private void SubmitOffer(String company, String jobCity, String jobType, String jobTitle, String jobAddress, String jobPhone, String jobAge, String jobDetails) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid= mAuth.getCurrentUser().getUid();

        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            public void onCompleted(User user) {
               User currentUser=new User(user);
                String jobId=databaseService.generateJobId();
                Job job = new Job(jobAddress, jobAge, jobCity, company,jobDetails, jobId, jobPhone, jobTitle, jobType, currentUser);
                createJobInDatabase(job); // הוספת job עבור currentUser במסד הנתונים
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "SubmitOffer: Failed to load user", e);
            }
        });
    }

    // ---- הוספת העבודה החדשה למסד הנתונים ----
    private void createJobInDatabase(Job job) {
        databaseService.createNewJob(job, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "createJobInDatabase: Job created successfully");

                Intent goUserActivity = new Intent(SubmitOfferActivity.this, UserActivity.class);   // מעבר למסך המשתמש
                goUserActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // מחיקת היסטוריית המסכים הקודמים
                startActivity(goUserActivity);
                Log.d(TAG, "createJobInDatabase: Redirecting to UserActivity");
            }
            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createJobInDatabase: Failed to create job", e);
                Toast.makeText(SubmitOfferActivity.this, "Failed to create job", Toast.LENGTH_SHORT).show(); // הצגת הודעת שגיאה למשתמש
            }
        });
    }
}