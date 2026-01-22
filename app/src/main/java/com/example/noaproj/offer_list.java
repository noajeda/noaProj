package com.example.noaproj;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noaproj.adapters.OfferAdapter;
import com.example.noaproj.model.Job;
import com.example.noaproj.model.User;
import com.example.noaproj.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class offer_list extends AppCompatActivity {
    private static final String TAG = "ReadOffers";
    DatabaseService databaseService;
    ArrayList<Job> offerList;
    RecyclerView rcOffers;
    TextView tv_offer_count;

    OfferAdapter adapter;
    int totalOffers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_offer_list);
        Log.d(TAG, "onCreate started");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        Log.d(TAG, "initViews started");

        databaseService.getJobList(new DatabaseService.DatabaseCallback<List<Job>>() {
            @Override
            public void onCompleted(List<Job> object) {
                Log.d(TAG, "onCompleted: " + object);
                adapter.setJobList(object);
                totalOffers= object.size();
                Log.d(TAG, "tv_offer_count found: " + (tv_offer_count != null));

            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void initViews() {
            tv_offer_count = findViewById(R.id.tv_user_count);
            databaseService = DatabaseService.getInstance();
        Log.d(TAG, "databaseService initialized");

            rcOffers = findViewById(R.id.rv_users_list);
        Log.d(TAG, "rcOffers found: " + (rcOffers != null));

        rcOffers.setLayoutManager(new LinearLayoutManager(this));
        offerList = new ArrayList<>();
        adapter = new OfferAdapter(null);
        rcOffers.setAdapter(adapter);
        Log.d(TAG, "initViews finished");

    }
    }