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

import com.example.noaproj.model.User;
import com.example.noaproj.adapters.UserAdapter;
import com.example.noaproj.services.DatabaseService;

import java.util.List;

public class UserList extends AppCompatActivity {
    private static final String TAG = "ReadUsers";
    DatabaseService databaseService;
    RecyclerView rcUsers;
    TextView tv_user_count;

    UserAdapter adapter;
    int totalUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        loadUserList();
    }

    private void initViews() {
        tv_user_count = findViewById(R.id.tv_user_count);
        rcUsers = findViewById(R.id.rv_users_list);
    }
    private void setupRecyclerView() {   // יצירת ה-adapter וקישורו ל-recyclerView
        rcUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter();
        rcUsers.setAdapter(adapter);
    }

     // ---- שליפת המשתמשים ממסד הנתונים והצגתם במסך המשתמש ----
    private void loadUserList() {
        databaseService = DatabaseService.getInstance();
        databaseService.getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> usersList) {
                if (usersList != null) {
                    Log.d(TAG, "onCompleted: " + usersList);
                    adapter.setUserList(usersList);
                    totalUsers = usersList.size();
                    tv_user_count.setText("סך כל המשתמשים: " + totalUsers);
                }
            }
            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to get userlist", e);
            }
        });
    }
}