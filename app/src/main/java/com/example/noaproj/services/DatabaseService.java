package com.example.noaproj.services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.noaproj.model.Call;
import com.example.noaproj.model.Job;
import com.example.noaproj.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    private static final String TAG = "DatabaseService";
    private static final String USERS_PATH = "users",

    COMPANY_JOBS_PATH = "company_jobs",
            JOBS_PATH = "jobs",
            CALLS_PATH = "calls";

    public interface DatabaseCallback<T> {
        public void onCompleted(T object);
        public void onFailed(Exception e);
    }

    private static DatabaseService instance;
    private final DatabaseReference databaseReference;


    private DatabaseService() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    // ---- כתיבת נתונים בנתיב מסוים ----
    private void writeData(@NotNull final String path, @NotNull final Object data, final @Nullable DatabaseCallback<Void> callback) {
        readData(path).setValue(data, (error, ref) -> {   // נכנסים לנתיב ושומרים שם את האובייקט
            if (error != null) {
                if (callback == null) return;
                callback.onFailed(error.toException());
            } else {
                if (callback == null) return;
                callback.onCompleted(null); //
            }
        });
    }

    // ---- מחיקת נתונים בנתיב מסוים ----
    private void deleteData(@NotNull final String path, @Nullable final DatabaseCallback<Void> callback) {
        readData(path).removeValue((error, ref) -> {     // נכנסים לנתיב ומוחקים את מה שבתוכו
            if (error != null) {
                if (callback == null) return;
                callback.onFailed(error.toException());
            } else {
                if (callback == null) return;
                callback.onCompleted(null);
            }
        });
    }

    private DatabaseReference readData(@NotNull final String path) {
        return databaseReference.child(path);     // קבלת reference לנתיב
    }

    // שליפת אובייקט
    private <T> void getData(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<T> callback) {
        readData(path).get().addOnCompleteListener(task -> { // קריאת הנתונים של הנתיב
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            T data = task.getResult().getValue(clazz); // האובייקט שהתקבל בנתיב
            callback.onCompleted(data);
        });
    }

    // שליפת רשימת אובייקטים
    private <T> void getDataList(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<List<T>> callback) {
        readData(path).get().addOnCompleteListener(task -> { // קריאת הנתונים של הנתיב
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<T> tList = new ArrayList<>();
            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) { //  עבור כל אחד מהילדים בנתיב
                T t = dataSnapshot.getValue(clazz);
                tList.add(t); // להוסיף לרשימה
            }
            callback.onCompleted(tList);
        });
    }

    // ייצור מזהה יחודי חדש
    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }

    // ---- יצירת משתמש חדש
    public void createNewUser(@NotNull final User user, @Nullable final DatabaseCallback<String> callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())  // יצירת משתמש ב-Firebase Auth
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "createUserWithEmail:success");
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        user.setId(uid);
                        // שמירת המשתמש במסד הנתונים
                        writeData(USERS_PATH + "/" + uid, user, new DatabaseCallback<Void>() {
                            @Override
                            public void onCompleted(Void v) {
                                if (callback != null) callback.onCompleted(uid);
                            }

                            @Override
                            public void onFailed(Exception e) {
                                if (callback != null) callback.onFailed(e);
                            }
                        });
                    } else {
                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        if (callback != null)
                            callback.onFailed(task.getException());
                    }
                });
    }

    // ---- חיבור המשתמש באמצעות אימייל וסיסמה ----
    public static void LoginUser(@NotNull final String email, final String password,
                                 @Nullable final DatabaseCallback<String> callback) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)   // התחברות ל-Firebase Auth
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "createUserWithEmail:success");
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        callback.onCompleted(uid);
                    } else {
                        Log.w("TAG", "createUserWithEmail:failure", task.getException());

                        if (callback != null)
                            callback.onFailed(task.getException());
                    }
                });
    }

    // שליפת משתמש מסויים באמצעות מזהה יחודי
    public void getUser(@NotNull final String uid, @NotNull final DatabaseCallback<User> callback) {
        getData(USERS_PATH + "/" + uid, User.class, callback);
    }

    // ---- שליפת רשימת כל המשתמשים ----
    public void getUserList(@NotNull final DatabaseCallback<List<User>> callback) {
        getDataList(USERS_PATH, User.class, callback);
    }

    // ---- יצירת עבודה חדשה ב2 נתיבים ----
    public void createNewJob(@NotNull final Job job, @Nullable final DatabaseCallback<Void> callback) {
        writeData(JOBS_PATH + "/" + job.getId(), job, callback);
        writeData(COMPANY_JOBS_PATH + "/" + job.getUser().getId() + "/" + job.getId(), job, callback);

    }

    // ---- עדכון פרטי עבודה ----
    public void updateJob(@NotNull Job job, @Nullable DatabaseCallback<Void> callback) {
        writeData(JOBS_PATH + "/" + job.getId(), job, callback);
        writeData(COMPANY_JOBS_PATH + "/" + job.getUser().getId() + "/" + job.getId(), job, null);
    }

    // ---- שליפת רשימת כל העבודות ----
    public void getJobList(@NotNull final DatabaseCallback<List<Job>> callback) {
        getDataList(JOBS_PATH, Job.class, callback);
    }

    // ---- שליפת רשימת כל העבודות של משתמש מסויים ----
    public void getCompanyJobList(@NotNull final String companyId, @NotNull final DatabaseCallback<List<Job>> callback) {
        getDataList(COMPANY_JOBS_PATH + "/" + companyId + "/", Job.class, callback);
    }

    // ---- יצירת מזהה יחודי לעבודה ----
    public String generateJobId() {
        return generateNewId(JOBS_PATH);
    }

    // ---- יצירת שיחה חדשה ----
    public void createNewCall(@NotNull final Call call, @Nullable final DatabaseCallback<Void> callback) {
        // יוצאת – אצל מי שהתקשר
        Call callUser = new Call(call.getId(), call.getJob(), call.getTime());
        writeData(CALLS_PATH + "/" + call.getUser().getId() + "/" + call.getId(), callUser, callback);

        // נכנסת – אצל מי שהתקבל
        Call callCompany = new Call(call.getId(), call.getTime(), call.getUser()); // User = מי שיצר את השיחה
        writeData(COMPANY_JOBS_PATH + "/" + call.getJob().getUser().getId() + "/" + call.getJob().getId() + "/calls/" + call.getId(), callCompany, callback);
    }

    // ---- יצירת מזהה יחודי לשיחה ----
    public String generateCallId() {
        return generateNewId(CALLS_PATH);
    }

    // ---- רשימת שיחות יוצאות של משתמש מסוים ----
    private void readOutgoingCalls(String uid, DatabaseService.DatabaseCallback<List<Call>> callback) {
        readData(DatabaseService.CALLS_PATH + "/" + uid) // שליפת calls של משתמש מסוים
                .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Call> outgoingCalls = new ArrayList<>();
                        for (DataSnapshot callSnapshot : snapshot.getChildren()) { // מעבר על כל השיחות היוצאות של המשתמש
                            Call call = callSnapshot.getValue(Call.class);
                            if (call != null)
                                outgoingCalls.add(call);
                        }
                        callback.onCompleted(outgoingCalls);  // החזרת רשימה של calls
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailed(error.toException());
                    }
                });
    }

    // ---- רשימת שיחות נכנסות של משתמש מסוים ----
    private void readIncomingCalls(String uid, DatabaseService.DatabaseCallback<List<Call>> callback) {
        getCompanyJobList(uid, new DatabaseService.DatabaseCallback<List<Job>>() {   // שליפת jobs של משתמש מסוים
            @Override
            public void onCompleted(List<Job> jobsList) {
                List<Call> incomingCalls = new ArrayList<>();
                if (jobsList == null || jobsList.isEmpty()) {
                    callback.onCompleted(incomingCalls);    // אם אין למשתמש עבודות, מחזיר רשימה ריקה
                    return;
                }

                final int totalJobs = jobsList.size();
                final int[] completedJobs = {0};

                for (Job job : jobsList) {      // שליפת calls של כל העבודות של המשתמש
                    readData(DatabaseService.COMPANY_JOBS_PATH + "/" + uid + "/" + job.getId() + "/calls")  // שליפת calls של עבודה ספציפית
                            .get().addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) { // אם יש לעבודה שיחות
                                    for (DataSnapshot callSnapshot : task.getResult().getChildren()) { // שליפת כל השיחות של העבודה
                                        Call call = callSnapshot.getValue(Call.class);
                                        if (call != null)
                                            incomingCalls.add(call);
                                    }
                                }
                                completedJobs[0]++;
                                // אם עברנו על כל ה-Jobs מחזירים את הרשימה
                                if (completedJobs[0] == totalJobs) {
                                    callback.onCompleted(incomingCalls);
                                }
                            }).addOnFailureListener(e -> {
                                completedJobs[0]++;
                                if (completedJobs[0] == totalJobs) {
                                    callback.onCompleted(incomingCalls);
                                }
                            });
                }
            }
            @Override
            public void onFailed(Exception e) {
                callback.onFailed(e);
            }
        });
    }


    // ---- קריאה של כלל השיחות של משתמש מסוים ----
    public void loadCallList(@NotNull final DatabaseCallback<List<Call>> callback) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();    // המשתמש הנוכחי
        readOutgoingCalls(uid, new DatabaseCallback<List<Call>>() { // רשימה של כל השיחות היוצאות
            @Override
            public void onCompleted(List<Call> outgoingCalls) {

                readIncomingCalls(uid, new DatabaseCallback<List<Call>>() { // רשימה של כל השיחות הנכנסות
                    @Override
                    public void onCompleted(List<Call> incomingCalls) {
                        List<Call> combined = new ArrayList<>();
                        if (outgoingCalls != null) combined.addAll(outgoingCalls);
                        if (incomingCalls != null) combined.addAll(incomingCalls);

                        combined.sort((c1, c2) -> Long.compare(c2.getTime(), c1.getTime()));     // ממיינים לפי זמן עולה
                        callback.onCompleted(combined);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        callback.onFailed(e);
                    }
                });

            }

            @Override
            public void onFailed(Exception e) {
                callback.onFailed(e);
            }
        });
    }
}