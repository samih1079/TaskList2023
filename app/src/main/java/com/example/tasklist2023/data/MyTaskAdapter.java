package com.example.tasklist2023.data;
// MyTaskAdapter.java

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Optional: For efficient image loading
import com.example.tasklist2023.R;

import java.util.ArrayList;
import java.util.List;

public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.MyTaskViewHolder> {

    private Context context;
    private List<MyTask> taskList;

    public MyTaskAdapter(Context context, List<MyTask> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public MyTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mytask, parent, false);
        return new MyTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTaskViewHolder holder, int position) {
        MyTask task = taskList.get(position);

        holder.tvTitle.setText(task.getShortTitle());
        holder.tvDueDate.setText("Due: " + (task.getTime() != 0 ? task.getTime() : "N/A"));
        holder.tvPriority.setText("Priority: " + (task.getImportance() != 0 ? task.getImportance() : "N/A"));

        // Handle Image Loading (using Glide is recommended for efficiency)
        if (task.getImage() != null && !task.getImage().isEmpty()) {
            holder.ivImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(Uri.parse(task.getImage()))
                    .placeholder(R.drawable.ic_launcher_background) // Optional placeholder
                    .error(com.google.android.material.R.drawable.mtrl_ic_error) // Optional error image
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setVisibility(View.GONE);
        }

        // You can add OnClickListeners here for item interaction
        // holder.itemView.setOnClickListener(new View.OnClickListener() { ... });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class MyTaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDueDate, tvPriority;
        ImageView ivImage;

        public MyTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTaskTitle);
            tvDueDate = itemView.findViewById(R.id