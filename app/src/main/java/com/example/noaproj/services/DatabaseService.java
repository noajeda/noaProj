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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;


/// a service to interact with the Firebase Realtime Database.
/// this class is a singleton, use getInstance() to get an instance of this class
/// @see #getInstance()
/// @see FirebaseDatabase
public class DatabaseService {

    /// tag for logging
    /// @see Log
    private static final String TAG = "DatabaseService";

    /// paths for different data types in the database
    /// @see DatabaseService#readData(String)
    private static final String USERS_PATH = "users",

    COMPANY_JOBS_PATH = "company_jobs",
            JOBS_PATH = "jobs",
            CALLS_PATH = "calls",
            USER_NOTIFICATINS_PATH = "user_notifications";

    /// callback interface for database operations
    /// @param <T> the type of the object to return
    /// @see DatabaseCallback#onCompleted(Object)
    /// @see DatabaseCallback#onFailed(Exception)
    public interface DatabaseCallback<T> {
        /// called when the operation is completed successfully
        public void onCompleted(T object);

        /// called when the operation fails with an exception
        public void onFailed(Exception e);
    }

    /// the instance of this class
    /// @see #getInstance()
    private static DatabaseService instance;

    /// the reference to the database
    /// @see DatabaseReference
    /// @see FirebaseDatabase#getReference()
    private final DatabaseReference databaseReference;

    /// use getInstance() to get an instance of this class
    /// @see DatabaseService#getInstance()
    private DatabaseService() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /// get an instance of this class
    /// @return an instance of this class
    /// @see DatabaseService
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }


    // region private generic methods
    // to write and read data from the database

    /// write data to the database at a specific path
    /// @param path the path to write the data to
    /// @param data the data to write (can be any object, but must be serializable, i.e. must have a default constructor and all fields must have getters and setters)
    /// @param callback the callback to call when the operation is completed
    /// @see DatabaseCallback


    // ---- כתיבת נתונים במסד הנתונים בנתיב מסוים ----
    private void writeData(@NotNull final String path, @NotNull final Object data, final @Nullable DatabaseCallback<Void> callback) {
        readData(path).setValue(data, (error, ref) -> {
            if (error != null) {
                if (callback == null) return;
                callback.onFailed(error.toException());
            } else {
                if (callback == null) return;
                callback.onCompleted(null);
            }
        });
    }

    /// remove data from the database at a specific path
    /// @param path the path to remove the data from
    /// @param callback the callback to call when the operation is completed
    /// @see DatabaseCallback

    // ---- מחיקת נתונים ממסד הנתונים בנתיב מסוים ----
    private void deleteData(@NotNull final String path, @Nullable final DatabaseCallback<Void> callback) {
        readData(path).removeValue((error, ref) -> {
            if (error != null) {
                if (callback == null) return;
                callback.onFailed(error.toException());
            } else {
                if (callback == null) return;
                callback.onCompleted(null);
            }
        });
    }

    /// read data from the database at a specific path
    /// @param path the path to read the data from
    /// @return a DatabaseReference object to read the data from
    /// @see DatabaseReference

    //
    private DatabaseReference readData(@NotNull final String path) {
        return databaseReference.child(path);
    }


    /// get data from the database at a specific path
    /// @param path the path to get the data from
    /// @param clazz the class of the object to return
    /// @param callback the callback to call when the operation is completed
    /// @see DatabaseCallback
    /// @see Class

    // שליפת אובייקט ממסד הנתונים
    private <T> void getData(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<T> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            T data = task.getResult().getValue(clazz);
            callback.onCompleted(data);
        });
    }

    /// get a list of data from the database at a specific path
    /// @param path the path to get the data from
    /// @param clazz the class of the objects to return
    /// @param callback the callback to call when the operation is completed

    // שליפת רשימת אובייקטיםם ממסד הנתונים
    private <T> void getDataList(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<List<T>> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<T> tList = new ArrayList<>();
            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                T t = dataSnapshot.getValue(clazz);
                tList.add(t);
            }

            callback.onCompleted(tList);
        });
    }


    /// generate a new id for a new object in the database
    /// @param path the path to generate the id for
    /// @return a new id for the object
    /// @see String
    /// @see DatabaseReference#push()

    // ייצור מזהה יחודי חדש במסד הנתונים
    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }


    /// run a transaction on the data at a specific path </br>
    /// good for incrementing a value or modifying an object in the database
    /// @param path the path to run the transaction on
    /// @param clazz the class of the object to return
    /// @param function the function to apply to the current value of the data
    /// @param callback the callback to call when the operation is completed
    /// @see DatabaseReference#runTransaction(Transaction.Handler)
    private <T> void runTransaction(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull UnaryOperator<T> function, @NotNull final DatabaseCallback<T> callback) {
        readData(path).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                T currentValue = currentData.getValue(clazz);
                if (currentValue == null) {
                    currentValue = function.apply(null);
                } else {
                    currentValue = function.apply(currentValue);
                }
                currentData.setValue(currentValue);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Log.e(TAG, "Transaction failed", error.toException());
                    callback.onFailed(error.toException());
                    return;
                }
                T result = currentData != null ? currentData.getValue(clazz) : null;
                callback.onCompleted(result);
            }


        });

    }

    // endregion of private methods for reading and writing data

    // public methods to interact with the database

    // region User Section


    /// create a new user in the database
    /// @param user the user object to create
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive void
    ///            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see User

    // ---- יצירת משתמש חדש
    public void createNewUser(@NotNull final User user,
                              @Nullable final DatabaseCallback<String> callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "createUserWithEmail:success");
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        user.setId(uid);
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
        mAuth.signInWithEmailAndPassword(email, password)
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

    // השגת משתמש מסויים באמצעות מזהה יחודי
    public void getUser(@NotNull final String uid, @NotNull final DatabaseCallback<User> callback) {
        getData(USERS_PATH + "/" + uid, User.class, callback);
    }

    /// get all the users from the database
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive a list of user objects
    ///            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see List
    /// @see User

    // ---- השגת רשימת כל המשתמשים ממסד הנתונים ----
    public void getUserList(@NotNull final DatabaseCallback<List<User>> callback) {
        getDataList(USERS_PATH, User.class, callback);
    }

    /// delete a user from the database
    /// @param uid the user id to delete
    /// @param callback the callback to call when the operation is completed
    public void deleteUser(@NotNull final String uid, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(USERS_PATH + "/" + uid, callback);
    }

    /// get a user by email and password
    /// @param email the email of the user
    /// @param password the password of the user
    /// @param callback the callback to call when the operation is completed
    ///            the callback will receive the user object
    ///          if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see User
    public void getUserByEmailAndPassword(@NotNull final String email, @NotNull final String password, @NotNull final DatabaseCallback<User> callback) {
        readData(USERS_PATH).orderByChild("email").equalTo(email).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Error getting data", task.getException());
                        callback.onFailed(task.getException());
                        return;
                    }
                    if (task.getResult().getChildrenCount() == 0) {
                        callback.onFailed(new Exception("User not found"));
                        return;
                    }
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null || !Objects.equals(user.getPassword(), password)) {
                            callback.onFailed(new Exception("Invalid email or password"));
                            return;
                        }

                        callback.onCompleted(user);
                        return;

                    }
                });
    }

    /// check if an email already exists in the database
    /// @param email the email to check
    /// @param callback the callback to call when the operation is completed
    public void checkIfEmailExists(@NotNull final String email, @NotNull final DatabaseCallback<Boolean> callback) {
        readData(USERS_PATH).orderByChild("email").equalTo(email).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Error getting data", task.getException());
                        callback.onFailed(task.getException());
                        return;
                    }
                    boolean exists = task.getResult().getChildrenCount() > 0;
                    callback.onCompleted(exists);
                });
    }

    public void updateUser(@NotNull final User user, @Nullable final DatabaseCallback<Void> callback) {
        runTransaction(USERS_PATH + "/" + user.getId(), User.class, currentUser -> user, new DatabaseCallback<User>() {
            @Override
            public void onCompleted(User object) {
                if (callback != null) {
                    callback.onCompleted(null);
                }
            }

            @Override
            public void onFailed(Exception e) {
                if (callback != null) {
                    callback.onFailed(e);
                }
            }
        });
    }


    // endregion User Section

    // region job section

    /// create a new job in the database
    /// @param job the job object to create
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive void
    ///             if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see Job
    public void createNewJob(@NotNull final Job job, @Nullable final DatabaseCallback<Void> callback) {
        writeData(JOBS_PATH + "/" + job.getId(), job, callback);
        writeData(COMPANY_JOBS_PATH + "/" + job.getUser().getId() + "/" + job.getId(), job, callback);

    }


    public void updateJob(@NotNull Job job, @Nullable DatabaseCallback<Void> callback) {

        writeData(JOBS_PATH + "/" + job.getId(), job, callback);
        writeData(COMPANY_JOBS_PATH + "/" + job.getUser().getId() + "/" + job.getId(), job, null);
    }



    /// get a job from the database
    /// @param jobId the id of the job to get
    /// @param callback the callback to call when the operation is completed
    ///               the callback will receive the job object
    ///              if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see Job
    public void getJob(@NotNull final String jobId, @NotNull final DatabaseCallback<Job> callback) {
        getData(JOBS_PATH + "/" + jobId, Job.class, callback);
    }

    /// get a job from the database
    /// @param comanyId the id of the job to get
    /// @param callback the callback to call when the operation is completed
    ///               the callback will receive the job object
    ///              if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see Job
    public void getCompanyJob(@NotNull final String comanyId, @NotNull final String jobId, @NotNull final DatabaseCallback<Job> callback) {
        getData(COMPANY_JOBS_PATH + "/" + comanyId + "/" + jobId, Job.class, callback);
    }


    /// get all the jobs from the database
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive a list of job objects
    ///            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see List
    /// @see Job
    public void getJobList(@NotNull final DatabaseCallback<List<Job>> callback) {
        getDataList(JOBS_PATH, Job.class, callback);
    }


    /// get all the jobs from the database
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive a list of job objects
    ///            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see List
    /// @see Job
    public void getCompanyJobList(@NotNull final String companyId, @NotNull final DatabaseCallback<List<Job>> callback) {
        getDataList(COMPANY_JOBS_PATH + "/" + companyId + "/", Job.class, callback);
    }


    /// generate a new id for a new job in the database
    /// @return a new id for the job
    /// @see #generateNewId(String)
    /// @see Job
    public String generateJobId() {
        return generateNewId(JOBS_PATH);
    }

    /// delete a job from the database
    /// @param jobId the id of the job to delete
    /// @param callback the callback to call when the operation is completed
    public void deleteJob(@NotNull final String jobId, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(JOBS_PATH + "/" + jobId, callback);

    }

    public void createNewCall(@NotNull final Call call, @Nullable final DatabaseCallback<Void> callback) {
        // יוצאת – אצל מי שהתקשר
        Call callUser = new Call(call.getId(), call.getJob(), call.getTime());
        writeData(CALLS_PATH + "/" + call.getUser().getId() + "/" + call.getId(), callUser, callback);

        // נכנסת – אצל מי שהתקבל
        Call callCompany = new Call(call.getId(), call.getTime(), call.getUser()); // User = מי שיצר את השיחה
        writeData(COMPANY_JOBS_PATH + "/" + call.getJob().getUser().getId() + "/" + call.getJob().getId() + "/calls/" + call.getId(), callCompany, callback);
    }


    public String generateCallId() {
        return generateNewId(CALLS_PATH);
    }


    // ---- רשימת שיחות יוצאות ----
    private void readOutgoingCalls(String uid, DatabaseService.DatabaseCallback<List<Call>> callback) {
        readData(DatabaseService.CALLS_PATH + "/" + uid) // ניגשים לנתונים של calls
                .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Call> outgoingCalls = new ArrayList<>();
                        for (DataSnapshot callSnapshot : snapshot.getChildren()) { // מעבר על כל השיחות
                            Call call = callSnapshot.getValue(Call.class); // המרת כל נתון שהתקבל לאובייקט מסוג call
                            if (call != null) outgoingCalls.add(call);
                        }
                        callback.onCompleted(outgoingCalls);  // החזרת כל הcalls
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailed(error.toException());
                    }
                });
    }

    // ---- רשימת שיחות נכנסות ----
    private void readIncomingCalls(String uid, DatabaseService.DatabaseCallback<List<Call>> callback) {
        getCompanyJobList(uid, new DatabaseService.DatabaseCallback<List<Job>>() {        // קורא את כל ה-Jobs של המשתמש
            @Override
            public void onCompleted(List<Job> jobsList) {
                List<Call> incomingCalls = new ArrayList<>();
                if (jobsList == null || jobsList.isEmpty()) {
                    callback.onCompleted(incomingCalls);    // אם אין למשתמש עבודות, מחזיר רשימה ריקה
                    return;
                }

                final int totalJobs = jobsList.size();
                final int[] processedJobs = {0};

                for (Job job : jobsList) {      // קוראים את כל השיחות עבור כל job
                    readData(DatabaseService.COMPANY_JOBS_PATH + "/" + uid + "/" + job.getId() + "/calls")  // קריאת Job ספציפי
                            .get().addOnCompleteListener(task -> {
                                processedJobs[0]++;
                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (DataSnapshot callSnapshot : task.getResult().getChildren()) { // קריאת כל השיחות של job
                                        Call call = callSnapshot.getValue(Call.class);    // המרת כל נתון שהתקבל לאובייקט מסוג call
                                        if (call != null) incomingCalls.add(call);
                                    }
                                }
                                // אם סיימנו את כל ה-Jobs מחזירים את הרשימה
                                if (processedJobs[0] == totalJobs) {
                                    callback.onCompleted(incomingCalls);
                                }
                            }).addOnFailureListener(e -> {
                                processedJobs[0]++;
                                if (processedJobs[0] == totalJobs) {
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


    // ---- קריאה של כלל השיחות ----
    public void listenToCallList(@NotNull final DatabaseCallback<List<Call>> callback) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();      // המשתמש הנוכחי
        readOutgoingCalls(uid, new DatabaseCallback<List<Call>>() {
            @Override
            public void onCompleted(List<Call> outgoingCalls) {

                readIncomingCalls(uid, new DatabaseCallback<List<Call>>() {
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


    /*
       public void getCallList(@NotNull final DatabaseCallback<List<Call>> callback) {
        readData(CALLS_PATH).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onFailed(task.getException());
                return;
            }

            List<Call> allCalls = new ArrayList<>();
            for (DataSnapshot userSnapshot : task.getResult().getChildren()) { // כל userId
                for (DataSnapshot callSnapshot : userSnapshot.getChildren()) { // כל call תחת המשתמש
                    Call call = callSnapshot.getValue(Call.class);
                    if (call != null) {
                        allCalls.add(call);
                    }
                }
            }
            callback.onCompleted(allCalls);
        });
    }
     */

// DatabaseService.java
    /*/
    public void updateUserToken(@NotNull String uid, @NotNull String token, @Nullable DatabaseCallback<Void> callback) {
        writeData(USERS_PATH + "/" + uid + "/fcmToken", token, callback);
    }

    public void addNotificationToDatabase(String userId, String title, String message, @Nullable DatabaseCallback<Void> callback) {
        String notificationId = String.valueOf(System.currentTimeMillis());
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("message", message);
        data.put("timestamp", System.currentTimeMillis());

        writeData("notifications/" + userId + "/" + notificationId, data, callback);
    }
    public void saveFcmToken(String userId, String token, DatabaseCallback<Void> callback) {
        writeData("users/" + userId + "/fcmToken", token, callback);
    }
     */

    /*/
    public void updateUserToken(String uid, String token) {
        readData(USERS_PATH + "/" + uid + "/token").setValue(token);
    }
    public void addNotificationToDatabase(String userId, String title, String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("body", message);
        notification.put("timestamp", System.currentTimeMillis());

        FirebaseDatabase.getInstance().getReference()
                .child("notifications")
                .child(userId)
                .push()
                .setValue(notification);
    }
    /*/
}