package com.example.noaproj.adapters;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.noaproj.R;
import com.example.noaproj.model.Job;
import com.example.noaproj.model.User;

import java.util.List;


public class OfferAdapter extends RecyclerView.Adapter<com.example.noaproj.adapters.OfferAdapter.ViewHolder> {

    public interface OnJobClickListener {
        void onLongJobClick(Job job);

        void onApprove(Job job);

        void onReject(Job job);

        void onPhoneClick(Job job);

    }
    private final List<Job> jobList;
    private final OfferAdapter.OnJobClickListener onJobClickListener;
    User currentUser;

    public OfferAdapter(List<Job> jobList, User currentUser, OnJobClickListener onJobClickListener) {
        this.jobList = jobList;
        this.onJobClickListener = onJobClickListener;
        this.currentUser=currentUser;

    }

    public OfferAdapter(List<Job> jobList, OnJobClickListener onJobClickListener) {
          this.jobList = jobList;
          this.onJobClickListener = onJobClickListener;
    }


    @NonNull
    @Override
    public OfferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        return new OfferAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferAdapter.ViewHolder holder, int position) {
        Job job = jobList.get(position);
        if (job == null) return;

        holder.tvJobCompany2.setText(job.getCompany());
        holder.tvJobTypeAndTitle.setText(job.getType() + ", " + job.getTitle());
        holder.tvJobCityAndAddress.setText(job.getCity() + ", " + job.getAddress());
        holder.tvJobPhone2.setText(job.getPhone());
        holder.tvJobAge2.setText( "גיל העסקה מינימלי: " + job.getAge());
        holder.tvJobDetails2.setText(job.getDetails());
        holder.btnApprove.setVisibility(View.GONE);
        holder.btnReject.setVisibility(View.GONE);
        if (job.getUser() != null) {
            holder.tvJobUser2.setText(job.getUser().getfName() + " " + job.getUser().getlName());
        }

        // לחיצה ארוכה על הפריט
        holder.itemView.setOnLongClickListener(v -> {
            if (onJobClickListener != null) {
                onJobClickListener.onLongJobClick(job);
            }
            return true;
        });

        // עיצוב מס' טלפון
        holder.tvJobPhone2.setPaintFlags(holder.tvJobPhone2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.tvJobPhone2.setTextColor(holder.tvJobCompany2.getTextColors());
        // לחיצה על מס' טלפון או אייקון טלפון
        View.OnClickListener phoneClickListener = v -> {
            holder.tvJobPhone2.setTextColor(android.graphics.Color.parseColor("#1E88E5"));
            phoneClick(v, job);
        };
        holder.imgPhone.setOnClickListener(phoneClickListener);
        holder.tvJobPhone2.setOnClickListener(phoneClickListener);


        // לחיצה על אייקון סמן מיקום ומעבר לGoogleMaps
        holder.imgLocation.setOnClickListener(v -> {
            String address = job.getAddress().trim();
            String city = job.getCity().trim();
            String fullAddress = address + ", " + city;
            String url = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(fullAddress);
            Intent goMap = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            v.getContext().startActivity(goMap);
        });

        // לחיצה על אישור
        holder.btnApprove.setOnClickListener(v -> {
            if (onJobClickListener != null) {
                onJobClickListener.onApprove(job);
            }
        });

        // לחיצה על דחייה
        holder.btnReject.setOnClickListener(v -> {
            if (onJobClickListener != null) {
                onJobClickListener.onReject(job);
            }
        });

        if (currentUser!= null && currentUser.getIsAdmin()) {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        }
         else {
        holder.btnApprove.setVisibility(View.GONE);
        holder.btnReject.setVisibility(View.GONE);
         }

    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public void setJobList(List<Job> jobs) {
        jobList.clear();
        jobList.addAll(jobs);
        notifyDataSetChanged();
    }

    // מעבר לאפליקציית שיחות
    private void phoneClick(View v, Job job) {
        String phone = job.getPhone().trim();
        Intent goCall = new Intent(Intent.ACTION_DIAL);
        goCall.setData(Uri.parse("tel:" + phone));
        v.getContext().startActivity(goCall);
        if (onJobClickListener != null) {
            onJobClickListener.onPhoneClick(job);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobCompany2, tvJobTypeAndTitle, tvJobCityAndAddress, tvJobPhone2, tvJobAge2, tvJobDetails2,tvJobUser2;
        Button btnApprove, btnReject;
        ImageView imgPhone, imgLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhone = itemView.findViewById(R.id.imgPhone);
            imgLocation = itemView.findViewById(R.id.imgLocation);
            tvJobCityAndAddress = itemView.findViewById(R.id.tvJobCityAndAddress);
            tvJobTypeAndTitle = itemView.findViewById(R.id.tvJobTypeAndTitle);
            tvJobCompany2 = itemView.findViewById(R.id.tvJobCompany2);
            tvJobPhone2 = itemView.findViewById(R.id.tvJobPhone2);
            tvJobAge2 = itemView.findViewById(R.id.tvJobAge2);
            tvJobDetails2 = itemView.findViewById(R.id.tvJobDetails2);
            tvJobUser2 = itemView.findViewById(R.id.tvJobUser2);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);

        }
    }
}