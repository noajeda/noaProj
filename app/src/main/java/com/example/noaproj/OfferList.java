    package com.example.noaproj;

    import android.content.pm.PackageManager;
    import android.os.Bundle;
    import android.telephony.SmsManager;
    import android.util.Log;
    import android.widget.EditText;
    import android.widget.TextView;

    import androidx.activity.EdgeToEdge;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.noaproj.adapters.OfferAdapter;
    import com.example.noaproj.model.Job;
    import com.example.noaproj.model.User;
    import com.example.noaproj.services.DatabaseService;
    import com.google.firebase.auth.FirebaseAuth;

    import java.util.ArrayList;
    import java.util.List;

    import android.Manifest;
    import android.widget.Toast;

    public class OfferList extends AppCompatActivity {
        private static final String TAG = "ReadOffers";
        DatabaseService databaseService;
        ArrayList<Job> jobArrayList = new ArrayList<>();
        RecyclerView rcOffers;
        TextView tv_offer_count;

        OfferAdapter adapter;
        int totalOffers;

        String uid = "";
        FirebaseAuth mAuth;
        User currentUser = null;
        private static final int SMS_PERMISSION_CODE = 1; // קוד זיהוי להרשאה

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
            setUpOfferAdapter();
        }

        private void initViews() {
            tv_offer_count = findViewById(R.id.tv_offer_count);
            databaseService = DatabaseService.getInstance();
            rcOffers = findViewById(R.id.rv_users_list);
            rcOffers.setLayoutManager(new LinearLayoutManager(this));
            jobArrayList = new ArrayList<>();
        }

        private void setUpOfferAdapter() {
            mAuth = FirebaseAuth.getInstance();
            uid = mAuth.getCurrentUser().getUid();
            databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
                @Override
                public void onCompleted(User user) {
                    currentUser = user;
                    adapter = new OfferAdapter(jobArrayList, currentUser, new OfferAdapter.OnJobClickListener() {
                        @Override
                        public void onJobClick(Job job) {
                        }

                        @Override
                        public void onLongJobClick(Job job) {
                        }

                        @Override
                        public void onApprove(Job job) {  // לחיצה על כפתור אישור העבודה
                            if (ContextCompat.checkSelfPermission(OfferList.this, Manifest.permission.SEND_SMS)
                                    == PackageManager.PERMISSION_GRANTED) {
                                sendApprovalSMS(job);

                            } else {
                                // אם אין הרשאה, מבקשים
                                checkSMSPermission();
                            }
                        }
                        @Override
                        public void onReject(Job job) { // לחיצה על כפתור דחיית העבודה
                            showRejectReasonDialog(job); // פתיחת דיאלוג שמציג את סיבת הדחייה
                        }

                        @Override
                        public void onPhoneClick(Job job) {
                        }
                    });
                    rcOffers.setAdapter(adapter);
                }

                @Override
                public void onFailed(Exception e) {

                }
            });
            readNewJobs();
        }

        // ---- שליפת העבודות החדשות ממסד הנתונים והצגתם במסך המנהל ----
        private void readNewJobs() {
            databaseService.getJobList(new DatabaseService.DatabaseCallback<List<Job>>() {
                @Override
                public void onCompleted(List<Job> jobsList) {
                    if (jobsList != null) {
                        for (int i = 0; i < jobsList.size(); i++) {
                            Log.d(TAG, "Job " + i + ": " + jobsList.get(i));
                            if (jobsList.get(i).getStatus().contains("new")) {
                                jobArrayList.add(jobsList.get(i));
                            }
                        }
                        adapter.notifyDataSetChanged();
                        totalOffers = jobArrayList.size();
                        tv_offer_count.setText("סך כל ההצעות: " + totalOffers);
                        Log.d(TAG, "tv_offer_count found: " + (tv_offer_count != null));
                    }
                }

                @Override
                public void onFailed(Exception e) {

                }
            });

        }

        // ---- שליחת SMS למשתמש שעבודתו אושרה ----
        public void sendApprovalSMS(Job job) {
            job.setStatus("approve");
            DatabaseService.getInstance().updateJob(job, new DatabaseService.DatabaseCallback<Void>() {  // עדכון סטטוס העבודה במסד הנתונים

                @Override
                public void onCompleted(Void object) {
                    //    sendUserNotification(job.getUser().getId(), "מאושר", "העבודה אושרה בהצלחה");
                    jobArrayList.remove(job);
                    adapter.notifyDataSetChanged(); // עדכון רשימת העבודות במסך המנהל
                    totalOffers = jobArrayList.size();
                    tv_offer_count.setText("סך כל ההצעות: " + totalOffers);

                    SmsManager smsManager = SmsManager.getDefault();
                    String msg = "העבודה '" + job.getTitle() + "' שהעלת באפליקציית NewJobs אושרה!"; // יצירת תוכן ההודעה
                    String userPhone = job.getUser().getPhone();
                    smsManager.sendTextMessage(userPhone, null, msg, null, null); // שליחת הSMS
                    Toast.makeText(OfferList.this, "SMS נשלח למשתמש", Toast.LENGTH_SHORT).show(); // הצגת הודעה למנהל שהSMS נשלח
                }


                @Override
                public void onFailed(Exception e) {

                }
            });
        }

        // ---- כתיבת סיבת הדחייה ----
        private void showRejectReasonDialog(Job job) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {  // בדיקה האם יש הרשאה

                EditText input = new EditText(this);
                input.setHint("הקלד את סיבת הדחייה כאן");

                // יצירת הדיאלוג
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("דחיית עבודה")
                        .setMessage("אנא הקלד את סיבת הדחייה:")
                        .setView(input)
                        .setPositiveButton("אישור", (dialog, which) -> { // לחיצה על אישור
                            String reason = input.getText().toString().trim(); // המרת הקלט של המנהל לסיבה
                            if (!reason.isEmpty()) { // בדיקה האם יש סיבה
                                sendRejectionSMS(job, reason); // שליחת SMS למשתמש הכוללת את הסיבה
                            } else { // הצגת הודעה מתאימה למנהל במידה ואין
                                Toast.makeText(this, "יש להזין סיבה", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss())
                        .show();
            }
            else {
                checkSMSPermission();  // בקשת הרשאה במידה ואין
            }
        }

        // ---- שליחת הודעת הדחייה ----
        public void sendRejectionSMS(Job job, String reason) {
            job.setStatus("disapprove");
            DatabaseService.getInstance().updateJob(job, new DatabaseService.DatabaseCallback<Void>() {

                @Override
                public void onCompleted(Void object) {
                String userPhone = job.getUser().getPhone();
                String msg = "העבודה '" + job.getTitle() + "' שהעלת באפליקציית NewJobs נדחתה!" + "\n" + "סיבת הדחייה: " + reason; // יצירת תוכן ההודעה
                    SmsManager smsManager = SmsManager.getDefault();
                    ArrayList<String> parts = smsManager.divideMessage(msg);
                    smsManager.sendMultipartTextMessage(userPhone, null, parts, null, null);
                adapter.notifyDataSetChanged();
                totalOffers = jobArrayList.size();
                tv_offer_count.setText("סך כל ההצעות: " + totalOffers);
                Toast.makeText(OfferList.this, "SMS נשלח למשתמש", Toast.LENGTH_SHORT).show();  // הצגת הודעה למנהל שהSMS נשלח
            }

            @Override
            public void onFailed(Exception e) {

            }
        });

        }

        // ----  בקשת הרשאה מהמנהל לשליחת SMS ----
        private void checkSMSPermission() {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                return; // יש הרשאה
            }

            //
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == SMS_PERMISSION_CODE) {
                 // אם יש תוצאה והיא מתן הרשאה
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "הרשאה ניתנה!", Toast.LENGTH_SHORT).show();
                } else {
                        Toast.makeText(this, "הרשאה נדחתה!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }