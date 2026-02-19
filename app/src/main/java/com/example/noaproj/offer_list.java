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

public class offer_list extends AppCompatActivity {
    private static final String TAG = "ReadOffers";
    DatabaseService databaseService;
    ArrayList<Job> jobArrayList=new ArrayList<>();
    RecyclerView rcOffers;
    TextView tv_offer_count;

    OfferAdapter adapter;
    int totalOffers;

    String uid="";


    FirebaseAuth mAuth;

    User currentUser=null;


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
        mAuth=FirebaseAuth.getInstance();
        uid=  mAuth.getCurrentUser().getUid();

        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                currentUser=user;
                adapter = new OfferAdapter(jobArrayList, currentUser, new OfferAdapter.OnJobClickListener(){
                    @Override
                    public void onJobClick(Job job) {
                        jobArrayList.clear();
                        readNewJobs();
                    }

                    @Override
                    public void onLongJobClick(Job job) {

                    }
                });
                rcOffers.setAdapter(adapter);

                setupAdapterListeners();

                if(currentUser.getIsAdmin()) {
                    readNewJobs();
                }
                else return;
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
        Log.d(TAG, "initViews started");

        checkSMSPermission();

    }

    private void setupAdapterListeners() {
        adapter.setOnJobActionListener(new OfferAdapter.OnJobActionListener() {
            @Override
            public void onApprove(Job job) {
                if(job.getUser()!=null) {
                    sendApprovalSMS(job);
                }
            }

            @Override
            public void onReject(Job job) {
                if(job.getUser()!=null) {
                    showRejectReasonDialog(job);
                }
            }
        });
    }

    private static final int SMS_PERMISSION_CODE = 1; // קוד זיהוי להרשאה
    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {    // האם יש הרשאה לSMS

            // אם לא, מבקש הרשאה מהמנהל
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "הרשאה ניתנה! אפשר לשלוח SMS", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "הרשאה נדחתה! SMS לא יישלח", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void sendApprovalSMS(Job job) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {    // בדיקה שיש הרשאה
            SmsManager smsManager = SmsManager.getDefault();
            String msg = "העבודה '" + job.getTitle() + "' אושרה!";
            String userPhone = job.getUser().getPhone();
            smsManager.sendTextMessage(userPhone, null, msg, null, null);
            Toast.makeText(this, "SMS נשלח למשתמש", Toast.LENGTH_SHORT).show();
        } else {
            // מבקשים הרשאה אם אין
            checkSMSPermission();
        }
    }
    public void sendRejectionSMS(String userPhone, String reason) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {  // בדיקה שיש הרשאה
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(userPhone, null, reason, null, null);
            Toast.makeText(this, "SMS נשלח למשתמש", Toast.LENGTH_SHORT).show();
        } else {
            // מבקשים הרשאה אם אין
            checkSMSPermission();
        }
    }
     private void showRejectReasonDialog(Job job) {
         EditText input = new EditText(this);
         input.setHint("הקלד את סיבת הדחייה כאן");

         new androidx.appcompat.app.AlertDialog.Builder(this)
                 .setTitle("דחיית עבודה")
                 .setMessage("אנא הקלד את סיבת הדחייה:")
                 .setView(input)
                 .setPositiveButton("אישור", (dialog, which) -> {
                     String reason = input.getText().toString().trim();
                     if (!reason.isEmpty()) {
                         sendRejectionSMS(job.getUser().getPhone(), reason);
                     } else {
                         Toast.makeText(this, "יש להזין סיבה", Toast.LENGTH_SHORT).show();
                     }
                 })
                 .setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss())
                 .show();
        }

    private void readNewJobs() {

        databaseService.getJobList(new DatabaseService.DatabaseCallback<List<Job>>() {
            @Override
            public void onCompleted(List<Job> jobsList) {
                if(jobsList!=null) {
                    //Log.d(TAG, "onCompleted: " + jobsList);
                    for (int i = 0; i < jobsList.size(); i++) {
                        Log.d(TAG, "Job " + i + ": " + jobsList.get(i));

                        if (jobsList.get(i).getStatus().contains("new"))
                        {

                            jobArrayList.add(jobsList.get(i));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    totalOffers= jobArrayList.size();
                    tv_offer_count.setText("Total offers:" + totalOffers);
                    Log.d(TAG, "tv_offer_count found: " + (tv_offer_count != null));

                }
            }


            @Override
            public void onFailed(Exception e) {

            }
        });

    }

    private void initViews() {
        tv_offer_count = findViewById(R.id.tv_offer_count);
        databaseService = DatabaseService.getInstance();
        Log.d(TAG, "databaseService initialized");

        rcOffers = findViewById(R.id.rv_users_list);
        Log.d(TAG, "rcOffers found: " + (rcOffers != null));

        rcOffers.setLayoutManager(new LinearLayoutManager(this));
        jobArrayList = new ArrayList<>();



        Log.d(TAG, "initViews finished");
    }
}
