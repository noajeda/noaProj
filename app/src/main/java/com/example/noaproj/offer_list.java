package com.example.noaproj;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noaproj.adapters.UserAdapter;
import com.example.noaproj.model.Job;
import com.example.noaproj.model.User;
import com.example.noaproj.services.DatabaseService;

import java.util.ArrayList;

public class offer_list extends AppCompatActivity {
    private static final String TAG = "ReadOffers";
    DatabaseService databaseService;
    ArrayList<Job> offerList;
    RecyclerView rcOffers;
    TextView tv_offer_count;

    offerAdapter adapter;
    int totalOffers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_offer_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();

    }

    private void initViews() {
            tv_offer_count = findViewById(R.id.tv_user_count);
            databaseService = DatabaseService.getInstance();
            rcOffers = findViewById(R.id.rv_users_list);
            rcOffers.setLayoutManager(new LinearLayoutManager(this));
            offerList = new ArrayList<>();
            adapter = new UserAdapter(null);
            rcOffers.setAdapter(adapter);
        }
    }