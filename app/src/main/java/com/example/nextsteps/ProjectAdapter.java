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
//public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
//
//    private List<Project> projects;
//
//    public ProjectAdapter(List<Project> projects) {
//        this.projects = projects;
//    }
//
//    @NonNull
//    @Override
//    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.home_item_project_task, parent, false);
//        return new ProjectViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
//        Project project = projects.get(position);
//        holder.projectName.setText(project.getProjectName());
//        holder.pendingTasksCount.setText(String.valueOf(project.getPendingTasksCount()));
//    }
//
//    @Override
//    public int getItemCount() {
//        return projects.size();
//    }
//
//    public void updateProjects(List<Project> newProjects) {
//        this.projects = newProjects;
//        notifyDataSetChanged();
//    }
//
//    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
//        TextView projectName, pendingTasksCount;
//
//        public ProjectViewHolder(@NonNull View itemView) {
//            super(itemView);
//            projectName = itemView.findViewById(R.id.project_name);
//            pendingTasksCount = itemView.findViewById(R.id.pending_task_count);
//        }
//    }
//}
