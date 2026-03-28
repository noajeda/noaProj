package com.example.noaproj;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noaproj.adapters.CallAdapter;
import com.example.noaproj.adapters.OfferAdapter;
import com.example.noaproj.model.Call;
import com.example.noaproj.model.Job;
import com.example.noaproj.model.User;
import com.example.noaproj.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class MyCalls extends AppCompatActivity {
    TextView tv_MyCalloffer_count;
    RecyclerView rcCalls;
    ArrayList<Call> callArrayList;
    private DatabaseService databaseService;
    CallAdapter adapter;
    int countCalls = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_calls);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    private void initViews() {
        tv_MyCalloffer_count = findViewById(R.id.tv_MyCalloffer_count);
        databaseService = DatabaseService.getInstance();
        Log.d(TAG, "databaseService initialized");

        rcCalls = findViewById(R.id.rvMyCalls);
        Log.d(TAG, "rcCalls found: " + (rcCalls != null));

        rcCalls.setLayoutManager(new LinearLayoutManager(this));
        callArrayList = new ArrayList<>();

        adapter = new CallAdapter(callArrayList);
        rcCalls.setAdapter(adapter);
        Log.d(TAG, "initViews finished");

        readCalls();

    }

    // ---- הצגת כל השיחות במסך ----
    private void readCalls() {

        databaseService.listenToCallList(new DatabaseService.DatabaseCallback<List<Call>>() {  // מעבר על כל השיחות שבמסד הנתונים
            @Override
            public void onCompleted(List<Call> callsList) {
                if (callsList != null) {
                    //Log.d(TAG, "onCompleted: " + jobsList);
                    callArrayList.clear();
                    for (int i = 0; i < callsList.size(); i++) {
                        callArrayList.add(callsList.get(i));    // הוספת כל השיחות לרשימה
                    }
                    adapter.notifyDataSetChanged();    // עדכון הadpater שמקושר לrecyclerView
                    countCalls = callArrayList.size();
                    tv_MyCalloffer_count.setText("Total calls:" + countCalls);
                    Log.d(TAG, "tv_MyCalloffer_count found: " + (tv_MyCalloffer_count != null));
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }
}

