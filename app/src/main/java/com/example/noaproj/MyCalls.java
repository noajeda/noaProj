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

import com.example.noaproj.adapters.CallAdapter;
import com.example.noaproj.model.Call;
import com.example.noaproj.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class MyCalls extends AppCompatActivity {
    private static final String TAG = "MyCallsActivity";
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
        initRecycler();
    }

    private void initViews() {
        tv_MyCalloffer_count = findViewById(R.id.tv_MyCalloffer_count);
        rcCalls = findViewById(R.id.rvMyCalls);
    }

    private void initRecycler() { //יצירת ה-adapter וקישורו לRecyclerView
        rcCalls.setLayoutManager(new LinearLayoutManager(this));
        callArrayList = new ArrayList<>();
        adapter = new CallAdapter(callArrayList);
        rcCalls.setAdapter(adapter);
    }

    // ---- הצגת כל השיחות במסך ----
    private void readCalls() {
        databaseService = DatabaseService.getInstance();
        databaseService.loadCallList(new DatabaseService.DatabaseCallback<List<Call>>() {  // מעבר על כל השיחות שבמסד הנתונים
            @Override
            public void onCompleted(List<Call> callsList) {
                if (callsList != null) {
                    callArrayList.clear();
                    for (int i = 0; i < callsList.size(); i++) {
                        callArrayList.add(callsList.get(i));    // הוספת כל השיחות לרשימה
                    }
                    adapter.notifyDataSetChanged();    // עדכון הadpater שמקושר לrecyclerView
                    countCalls = callArrayList.size();
                    tv_MyCalloffer_count.setText("סך כל השיחות: " + countCalls);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to read calls", e);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        readCalls();
    }
}

