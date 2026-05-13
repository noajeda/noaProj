package com.example.noaproj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noaproj.adapters.OfferAdapter;
import com.example.noaproj.model.Job;
import com.example.noaproj.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MyJobs extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ReadUserOffers";
    DatabaseService databaseService;
    ArrayList<Job> jobArrayList=new ArrayList<>();
    RecyclerView rvMyjobs;
    TextView tv_myJobs_count;
    ImageView imgAddOffer;
    OfferAdapter adapter;
    int totalJobs;

    String uid="";
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_jobs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth=FirebaseAuth.getInstance();
        uid=  mAuth.getUid();

        initViews();
        initRecycler();
        initListeners();
    }
    private void initViews() {
        tv_myJobs_count = findViewById(R.id.tv_MyJoboffer_count);
        imgAddOffer = findViewById(R.id.imgAddOffer);
        rvMyjobs = findViewById(R.id.rvMyjobOffer);
        Log.d(TAG, "initViews finished");
    }

    private void initRecycler(){
        rvMyjobs.setLayoutManager(new LinearLayoutManager(this));
        jobArrayList = new ArrayList<>();
        setUpJobsAdapter();
    }

    private void setUpJobsAdapter() {
        adapter = new OfferAdapter(jobArrayList, new OfferAdapter.OnJobClickListener(){
            @Override
            public void onLongJobClick(Job job) { // הסרת עבודה
                job.setStatus("delete");
                DatabaseService.getInstance().updateJob(job, new DatabaseService.DatabaseCallback<Void>() { // עדכון במסד הנתונים שהעבודה הוסרה
                    @Override
                    public void onCompleted(Void object) {
                        jobArrayList.remove(job);
                        adapter.notifyDataSetChanged(); // עדכון הadpater שמקושר לrecyclerView
                        totalJobs= jobArrayList.size();
                        tv_myJobs_count.setText("סך כל העבודות:" + totalJobs);
                        Toast.makeText(MyJobs.this, "העבודה הוסרה!", Toast.LENGTH_SHORT).show(); // הצגת הודעה למשתמש
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e(TAG, "onFailed: Failed to delete user", e);
                    }
                });
            }

            @Override
            public void onApprove(Job job) {

            }

            @Override
            public void onReject(Job job) {

            }

            @Override
            public void onPhoneClick(Job job) {

            }
        });
        rvMyjobs.setAdapter(adapter);
    }

    private void initListeners() {
        imgAddOffer.setOnClickListener(this);
    }

    // ---- הצגת כל העבודות המאושרות של המשתמש במסך ----
    private void readJobs(String uid) {
        databaseService = DatabaseService.getInstance();
        databaseService.getCompanyJobList( uid ,new DatabaseService.DatabaseCallback<List<Job>>() { // קבלת רשימת כל העבודות של המשתמש
            @Override
            public void onCompleted(List<Job> jobsList) {
                if(jobsList!=null) {
                    jobArrayList.clear();
                    for(int i=0; i< jobsList.size(); i++){
                        if(jobsList.get(i).getStatus().contains("approve"))
                            jobArrayList.add(jobsList.get(i)); // רשימה של כל העבודות המאושרות של המשתמש
                    }
                    adapter.notifyDataSetChanged(); // עדכון הadpater שמקושר לrecyclerView
                    totalJobs= jobArrayList.size();
                    tv_myJobs_count.setText("סך כל העבודות: " + totalJobs);
                    Log.d(TAG, "tv_job_count found: " + (tv_myJobs_count != null));
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to getCompanyJobList", e);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        readJobs(uid);
    }


    @Override
    public void onClick(View v) {
        if( v == imgAddOffer){   // מעבר למסך הוספת עבודה
            Intent goAddOffer = new Intent(this, SubmitOfferActivity.class);
            startActivity(goAddOffer);
        }
    }
}