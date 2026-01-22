package com.example.noaproj.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.example.noaproj.R;
import com.example.noaproj.model.Job;
import com.example.noaproj.model.User;

import java.util.ArrayList;
import java.util.List;


    public class OfferAdapter extends RecyclerView.Adapter<com.example.noaproj.adapters.OfferAdapter.ViewHolder> {

        public interface OnJobClickListener {
        void onJobClick(Job job);
        void onLongJobClick(Job job);
    }

    private final List<Job> jobList;
    private final OfferAdapter.OnJobClickListener onJobClickListener;
    public OfferAdapter(@Nullable final OfferAdapter.OnJobClickListener onJobClickListener) {
        jobList = new ArrayList<>();
        this.onJobClickListener = onJobClickListener;
    }

    @NonNull
    @Override
    public OfferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new OfferAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferAdapter.ViewHolder holder, int position) {
        Job job = jobList.get(position);
        if (job == null) return;

        holder.tvJobTitle2.setText(job.getTitle());
        holder.tvJobType2.setText(job.getType());
        holder.tvJobCompany2.setText(job.getCompany());
        holder.tvAddress2.setText(job.getAddress());
        holder.tvJobCity2.setText(job.getCity());
        holder.tvJobPhone2.setText(job.getPhone());
        holder.tvJobDetails2.setText(job.getDetails());
        if (job.getUser() != null) {
            holder.tvJobUser2.setText(job.getUser().getfName() + " " + job.getUser().getlName() );
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
        TextView tvJobTitle2, tvJobType2, tvJobCompany2, tvAddress2, tvJobCity2, tvJobPhone2, tvJobDetails2,tvJobUser2;
        //Chip chipRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle2 = itemView.findViewById(R.id.tvJobTitle2);
            tvJobType2 = itemView.findViewById(R.id.tvJobType2);
            tvJobCompany2 = itemView.findViewById(R.id.tvJobCompany2);
            tvAddress2 = itemView.findViewById(R.id.tvAddress2);
            tvJobCity2 = itemView.findViewById(R.id.tvJobCity2);
            tvJobPhone2 = itemView.findViewById(R.id.tvJobPhone2);
            tvJobDetails2 = itemView.findViewById(R.id.tvJobDetails2);
            tvJobUser2 = itemView.findViewById(R.id.tvJobUser2);

            // chipRole = itemView.findViewById(R.id.chip_user_role);
        }
    }
}
