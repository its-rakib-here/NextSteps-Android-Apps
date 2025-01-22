//package com.example.nextsteps;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
//
//    private List<ShowTaskUnderProject.Task> tasks;
//
//    public TaskAdapter(List<ShowTaskUnderProject.Task> tasks) {
//        this.tasks = tasks;
//    }
//
//    @NonNull
//    @Override
//    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.home_item_pending_task, parent, false);
//        return new TaskViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
//        ShowTaskUnderProject.Task task = tasks.get(position);
//        holder.taskTitle.setText(task.getTaskTitle());
//        holder.startTime.setText(task.getStartTime());
//        holder.endTime.setText(task.getEndTime());
//        holder.taskStatus.setText(task.getTaskStatus());
//    }
//
//    @Override
//    public int getItemCount() {
//        return tasks.size();
//    }
//
//    public void updateTasks(List<Task> newTasks) {
//        this.tasks = newTasks;
//        notifyDataSetChanged();
//    }
//
//    public static class TaskViewHolder extends RecyclerView.ViewHolder {
//        TextView taskTitle, startTime, endTime, taskStatus;
//
//        public TaskViewHolder(@NonNull View itemView) {
//            super(itemView);
//            taskTitle = itemView.findViewById(R.id.task_title);
//            startTime = itemView.findViewById(R.id.start_time);
//            endTime = itemView.findViewById(R.id.end_time);
//            taskStatus = itemView.findViewById(R.id.task_status);
//        }
//    }
//}
