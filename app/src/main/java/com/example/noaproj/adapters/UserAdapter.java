package com.example.noaproj.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noaproj.R;
import com.example.noaproj.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final List<User> userList;

    public UserAdapter() {
        userList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        if (user == null) return;

        holder.tvName.setText(user.getfName() + " " + user.getlName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvPhone.setText(user.getPhone());

        String initials = "";
        if (user.getfName() != null && !user.getfName().isEmpty()) {
            initials += user.getfName().charAt(0) + ".";
        }
        if (user.getlName() != null && !user.getlName().isEmpty()) {
            initials += user.getlName().charAt(0) + ".";
        }
        holder.tvInitials.setText(initials.toUpperCase());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<User> users) {
        userList.clear();
        userList.addAll(users);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvEmail, tvPhone, tvInitials;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFname_userList);
            tvEmail = itemView.findViewById(R.id.tvLname_userList);
            tvPhone = itemView.findViewById(R.id.tvPhone_userList);
            tvInitials = itemView.findViewById(R.id.tvInitials_userList);
        }
    }
}