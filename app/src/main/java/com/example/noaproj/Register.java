package com.example.noaproj;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import com.example.noaproj.model.User;
import com.example.noaproj.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";
    private DatabaseService databaseService;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;



    Button btnSubmit;
    EditText etFname, etLname, etPassword, etEmail, etPhone, etAge;
    String fname, lname, password, email, phone, city, gender, age;
    Spinner spUserGender, spUserCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        initListeners();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        databaseService = DatabaseService.getInstance();
    }

    private void initViews() {
        btnSubmit = findViewById(R.id.btnSubmit);
        etFname = findViewById(R.id.etFname);
        etLname = findViewById(R.id.etLname);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        spUserCity = findViewById(R.id.spUserCity);
        spUserGender = findViewById(R.id.spUserGender);
        etAge = findViewById(R.id.etAge);
    }

    private void initListeners() {
        btnSubmit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == btnSubmit) { // לחיצה על שליחה
            Log.d(TAG, "onClick: Register button clicked");

            // שמירת הקלט שהמשתמש הזין
            fname = etFname.getText().toString();
            lname = etLname.getText().toString();
            password = etPassword.getText().toString();
            email = etEmail.getText().toString();
            phone = etPhone.getText().toString();
            city = spUserCity.getSelectedItem().toString();
            age = etAge.getText().toString();
            gender = spUserGender.getSelectedItem().toString();

            Log.d(TAG, "onClick: Registering user...");
            registerUser(fname, lname, phone, email, password, age,gender, city);  // רישום המשתמש לפי הנתונים שנקלטו
        }
    }

        // ---- רישום המשתמש ----
    private void registerUser(String fname, String lname, String phone, String email, String password, String age, String gender, String city) {
        Log.d(TAG, "register: Registering user...");

        User user = new User("oo",fname, lname, phone, email, password, age, gender, city); // יצירת אובייקט User
            createUserInDatabase(user);
        }

        // ---- הוספת המשתמש למסד הנתונים ----
    private void createUserInDatabase(User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                user.setId(uid);
                Log.d(TAG, "createUserInDatabase: User created successfully");

                SharedPreferences.Editor editor = sharedpreferences.edit(); // שמירת נתוני המשתמש ב-SharedPreferences
                editor.putString("email", email);
                editor.putString("password", password);
                editor.commit();

                Intent goUserActivity = new Intent(Register.this, UserActivity.class); // מעבר למסך המשתמש
                goUserActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // מחיקת היסטוריית המסכים הקודמים
                startActivity(goUserActivity);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                Toast.makeText(Register.this, "Failed to register user", Toast.LENGTH_SHORT).show(); // הצגת הודעת שגיאה למשתמש
                FirebaseAuth.getInstance().signOut(); // נטרול המשתמש אם הרישום למסד הנתונים נכשל
            }
        });
    }
}
