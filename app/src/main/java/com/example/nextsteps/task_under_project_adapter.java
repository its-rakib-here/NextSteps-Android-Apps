//package com.example.nextsteps;
//
////package com.example.adapters;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//
//import java.util.List;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//
//
//import java.util.List;
//
//public class TaskUnderProjectAdapter extends RecyclerView.Adapter<TaskUnderProjectAdapter.TaskViewHolder> {
//    private final List<HomeFragment.Task> taskList;
//
//    public TaskUnderProjectAdapter(List<HomeFragment.Task> taskList) {
//        this.taskList = taskList;
//    }
//
//    @NonNull
//    @Override
//    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_project_pending_task, parent, false);
//        return new TaskViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
//        Task task = taskList.get(position);
//        holder.tvTaskName.setText(task.getTaskName());
//        holder.tvTaskDescription.setText(task.getTaskDescription());
//        holder.tvTaskDistributedBy.setText("Distributed by: " + task.getDistributedBy());
//        holder.tvTaskStatus.setText(task.getStatus());
//        holder.tvTaskDeadline.setText("Due: " + task.getDeadline());
//    }
//
//    @Override
//    public int getItemCount() {
//        return taskList.size();
//    }
//
//    public static class TaskViewHolder extends RecyclerView.ViewHolder {
//        TextView tvTaskName, tvTaskDescription, tvTaskDistributedBy, tvTaskStatus, tvTaskDeadline;
//
//        public TaskViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvTaskName = itemView.findViewById(R.id.tvTaskName);
//            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
//            tvTaskDistributedBy = itemView.findViewById(R.id.tvTaskDistributedBy);
//            tvTaskStatus = itemView.findViewById(R.id.tvTaskStatus);
//            tvTaskDeadline = itemView.findViewById(R.id.tvTaskDeadline);
//        }
//    }
//}
//
