package com.example.noaproj.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noaproj.R;
import com.example.noaproj.model.Call;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ViewHolder> {

    private final List<Call> callList;

    public CallAdapter(List<Call> callList){
        this.callList = callList;
    }

    @NonNull
    @Override
    public CallAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_call, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CallAdapter.ViewHolder holder, int position) {
        Call call = callList.get(position);
        if (call == null) return;


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvTime.setText(sdf.format(new Date(call.getTime())));

        if (call.getJob() != null) {    // שיחה יוצאת
            holder.tvPhone.setText(call.getJob().getPhone());
            holder.tvNameCall.setText(call.getJob().getCompany());
            holder.imgPhone.setImageResource(R.drawable.baseline_call_received_24);
        }
        else if (call.getUser() != null) {  // שיחה נכנסת
            holder.tvPhone.setText(call.getUser().getPhone());
            holder.tvNameCall.setText(call.getUser().getfName() + " " + call.getUser().getlName());
        }
    }

    @Override
    public int getItemCount() {
        return callList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameCall, tvPhone, tvTime;
        ImageView imgPhone;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvNameCall = itemView.findViewById(R.id.tvNameCall);
            tvPhone = itemView.findViewById(R.id.tvPhoneCall);
            tvTime = itemView.findViewById(R.id.tvCallTime);
            imgPhone = itemView.findViewById(R.id.imgCallPhone);
        }
    }
}