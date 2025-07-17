package com.example.workforcemanagement.ui.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.User;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users = new ArrayList<>();
    private final OnUserActionListener listener;
    private final Context context;

    public interface OnUserActionListener {
        void onEditUser(User user);
        void onDeleteUser(User user);
    }

    public UserAdapter(Context context, OnUserActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_management, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvUserName.setText(user.getUsername());
        holder.tvUserEmail.setText(user.getEmail());

        // Set is_active status
        boolean isActive = user.isActive();
        holder.tvStatus.setText(isActive ? "Hoạt động" : "Bị khóa");
        holder.tvStatus.setBackgroundResource(
                isActive ? R.drawable.ic_active : R.drawable.ic_lock
        );
        holder.tvStatus.setTextColor(
                isActive ?
                        context.getResources().getColor(R.color.active) :
                        context.getResources().getColor(R.color.error_color)
        );

        // Set click listeners
        holder.btnEdit.setOnClickListener(v -> listener.onEditUser(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteUser(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvUserName;
        TextView tvUserEmail;
        TextView tvStatus;
        ImageButton btnEdit;
        ImageButton btnDelete;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}