package com.example.noaproj.adapters;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.example.noaproj.R;
import com.example.noaproj.UserActivity;
import com.example.noaproj.model.Call;
import com.example.noaproj.model.Job;
import com.example.noaproj.model.User;
import com.example.noaproj.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;


public class OfferAdapter extends RecyclerView.Adapter<com.example.noaproj.adapters.OfferAdapter.ViewHolder> {

    public interface OnJobClickListener {
        void onJobClick(Job job);

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


    public OfferAdapter(@Nullable final OfferAdapter.OnJobClickListener onJobClickListener) {
        jobList = new ArrayList<>();
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
        holder.tvJobDetails2.setText(job.getDetails());
        holder.btnApprove.setVisibility(View.GONE);
        holder.btnReject.setVisibility(View.GONE);
        if (job.getUser() != null) {
            holder.tvJobUser2.setText(job.getUser().getfName() + " " + job.getUser().getlName());
        }



        /*/ Show admin chip if user is admin
        if (user.isAdmin()) {
            holder.chipRole.setVisibility(View.VISIBLE);
            holder.chipRole.setText("Admin");
        } else {
            holder.chipRole.setVisibility(View.GONE);
        }
        /*/

        holder.itemView.setOnClickListener(v -> {
            if (onJobClickListener != null) {
                onJobClickListener.onJobClick(job);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onJobClickListener != null) {
                onJobClickListener.onLongJobClick(job);
            }
            return true;
        });

        holder.imgPhone.setOnClickListener(v -> {
          // לחיצה על תמונת הטלפון ומעבר לאפליקציית שיחות
            String phone = job.getPhone().trim();
            Intent goCall = new Intent(Intent.ACTION_DIAL);
            goCall.setData(Uri.parse("tel:" + phone));
            v.getContext().startActivity(goCall);

            if (onJobClickListener != null) {
                onJobClickListener.onPhoneClick(job);
            }
        });
        holder.imgLocation.setOnClickListener(v -> {
            String address = job.getAddress().trim();
            String url = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(address);
            Intent goMap = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            v.getContext().startActivity(goMap);
        });
        holder.btnApprove.setOnClickListener(v -> {
            if (onJobClickListener != null) {
                onJobClickListener.onApprove(job);

            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (onJobClickListener != null) {
                onJobClickListener.onReject(job);

            }

        });

        if (currentUser!= null && currentUser.getIsAdmin()) {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
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





    public void addJob(Job job) {
        jobList.add(job);
        notifyItemInserted(jobList.size() - 1);
    }
    public void updateJob(Job job) {
        int index = jobList.indexOf(job);
        if (index == -1) return;
        jobList.set(index, job);
        notifyItemChanged(index);
    }

    public void removeJob(Job job) {
        int index = jobList.indexOf(job);
        if (index == -1) return;
        jobList.remove(index);
        notifyItemRemoved(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobCompany2, tvJobTypeAndTitle, tvJobCityAndAddress, tvJobPhone2, tvJobDetails2,tvJobUser2;
        Button btnApprove, btnReject;
        ImageView imgPhone, imgLocation;
        //Chip chipRole;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhone = itemView.findViewById(R.id.imgPhone);
            imgLocation = itemView.findViewById(R.id.imgLocation);
            tvJobCityAndAddress = itemView.findViewById(R.id.tvJobCityAndAddress);
            tvJobTypeAndTitle = itemView.findViewById(R.id.tvJobTypeAndTitle);
            tvJobCompany2 = itemView.findViewById(R.id.tvJobCompany2);
            tvJobPhone2 = itemView.findViewById(R.id.tvJobPhone2);
            tvJobDetails2 = itemView.findViewById(R.id.tvJobDetails2);
            tvJobUser2 = itemView.findViewById(R.id.tvJobUser2);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);

        }
    }
}