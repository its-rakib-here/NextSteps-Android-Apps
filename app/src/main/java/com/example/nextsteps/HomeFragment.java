package com.example.nextsteps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    PieChart chartView;
    private Button btnDailyProgress, btnWeeklyProgress, btnMonthlyProgress,btn_overdue_tasks;
    private RecyclerView recyclerIndividualTasks, recyclerProjectsWithTasks;
    private TaskAdapter taskAdapter;
    private ProjectAdapter projectAdapter;
    private String currentFilter = "daily";
    TextView toolbarDate, toolbarDay, toolbarWelcomeMessage;
    ImageView appbarprofile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View homeFragment = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI components
        chartView = homeFragment.findViewById(R.id.piechart);
        btnDailyProgress = homeFragment.findViewById(R.id.btn_daily_progress);
        btnWeeklyProgress = homeFragment.findViewById(R.id.btn_weekly_progress);
        btnMonthlyProgress = homeFragment.findViewById(R.id.btn_monthly_progress);
        btn_overdue_tasks = homeFragment.findViewById(R.id.btn_overdue_tasks);

        recyclerIndividualTasks = homeFragment.findViewById(R.id.recycler_individual_tasks);
        recyclerProjectsWithTasks = homeFragment.findViewById(R.id.recycler_projects_with_tasks);
        toolbarDate = homeFragment.findViewById(R.id.toolbar_date);
        toolbarDay = homeFragment.findViewById(R.id.toolbar_day);
        toolbarWelcomeMessage = homeFragment.findViewById(R.id.toolbar_welcome_message);
        appbarprofile = homeFragment.findViewById(R.id.appbarprofile);
        recyclerProjectsWithTasks = homeFragment.findViewById(R.id.recycler_projects_with_tasks);


        appbarprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), UserProfile.class));
            }
        });

        // Set Date
        String currentDate = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());
        toolbarDate.setText(currentDate);

        // Set Day
        String currentDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
        toolbarDay.setText(currentDay);

        // Set Welcome Message
        String userName = "Rakib"; // Replace with the username from the database
        String welcomeMessage = "Welcome back, " + userName + "! Let's finish your today's work";
        toolbarWelcomeMessage.setText(welcomeMessage);

        // Initialize adapters
        taskAdapter = new TaskAdapter(new ArrayList<>());
        projectAdapter = new ProjectAdapter(new ArrayList<>());

        // Set up RecyclerViews
        recyclerIndividualTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerIndividualTasks.setHasFixedSize(true);
        recyclerIndividualTasks.setAdapter(taskAdapter);

        recyclerProjectsWithTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerProjectsWithTasks.setHasFixedSize(true);
        recyclerProjectsWithTasks.setAdapter(projectAdapter);

        // Set up button click listeners
        btnDailyProgress.setOnClickListener(v -> {
            currentFilter = "daily";
            fetchTaskData(currentFilter);
            fetchProjectsWithTasks(currentFilter);

        });

        btnWeeklyProgress.setOnClickListener(v -> {
            currentFilter = "weekly";
            fetchTaskData(currentFilter);
            fetchProjectsWithTasks(currentFilter);

        });

        btnMonthlyProgress.setOnClickListener(v -> {
            currentFilter = "monthly";
            fetchTaskData(currentFilter);
            fetchProjectsWithTasks(currentFilter);

        });

        btn_overdue_tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFilter="overdue";
                fetchTaskData(currentFilter);
                fetchProjectsWithTasks(currentFilter);

            }
        });
        // Fetch initial data
        fetchTaskData(currentFilter);
        fetchProjectsWithTasks(currentFilter);

        return homeFragment;
    }

    private void fetchTaskData(String filterType) {
        int userId = getUserId();
        String url = "https://mensstylehouse.com/aa/NextSteps/php/slectAllPendingandActiveTask.php";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("filter", filterType);
        } catch (JSONException e) {
            Log.e("JSONException", "Error creating JSON: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        int pendingCount = response.getInt("pending");
                        int completedCount = response.getInt("completed");
                        int overdueCount = response.getInt("overdue");
//                        Log.d("task","t"+response.toString());
//                        Log.d("Response", "Response: " + response.toString());

                        updatePieChart(pendingCount, completedCount, overdueCount);

                        List<Task> tasks = parseTasks(response.getJSONArray("tasks"));
                        taskAdapter.updateTasks(tasks);
                    } catch (JSONException e) {
                        Log.e("ParseError", "Error parsing task data: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("VolleyError", "Error fetching task data: " + error.getMessage());
                    Toast.makeText(requireContext(), "Failed to fetch tasks. Please try again.", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void fetchProjectsWithTasks(String filter) {
        int userId = getUserId(); // Assuming this retrieves the user ID
        String url = "https://mensstylehouse.com/aa/NextSteps/php/showProjectOnhomePageBasedOnPendingTask.php";

        Log.d("forProjeect","user id"+userId);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "get_filtered_projects");
            jsonObject.put("user_id", userId);
            jsonObject.put("filter", filter); // Pass the filter parameter
        } catch (JSONException e) {
            Log.e("JSONException", "Error creating JSON: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        Log.d("response","r"+response);
                        Log.d("Raw", "Raw Response: " + response.toString());  // Log raw response
                        if (response.getBoolean("success")) {
                            if (response.has("projects")) {
                                List<Project> projects = parseProjects(response.getJSONArray("projects"));
                                projectAdapter.updateProjects(projects);
                            } else {
                                Log.e("ParseError", "No projects key in response");
                            }
                        } else {
                            Toast.makeText(requireContext(), "No projects found.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("ParseError", "Error parsing project data: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("VolleyError", "Error fetching project data: " + error.getMessage());
                    Toast.makeText(requireContext(), "Failed to fetch projects. Please try again.", Toast.LENGTH_SHORT).show();
                }
        );


        // Add the request to the Volley request queue
        Volley.newRequestQueue(requireContext()).add(request);
    }


    private void updatePieChart(int pendingCount, int completedCount, int overdueCount) {
        // Calculate total tasks
        int totalTasks = pendingCount + completedCount + overdueCount;
        if (totalTasks <= 0) {
            Toast.makeText(getContext(), "No tasks available to display.", Toast.LENGTH_SHORT).show();
            chartView.clearChart(); // Clear the chart if no data
            return;
        }

        // Calculate percentages
        float pendingPercentage = (pendingCount / (float) totalTasks) * 100;
        float completedPercentage = (completedCount / (float) totalTasks) * 100;
        float overduePercentage = (overdueCount / (float) totalTasks) * 100;
        // Log values for debugging
        Log.d("PieChart", "Total tasks: " + totalTasks);
        Log.d("PieChart", "Pending: " + pendingPercentage);
        Log.d("PieChart", "Completed: " + completedPercentage);
        Log.d("PieChart", "Overdue: " + overduePercentage);

        // Clear previous data from the chart
        chartView.clearChart();

        // Add slices to the pie chart with task counts and percentages
        chartView.addPieSlice(new PieModel(
                String.format("Pending (%.1f%%)", pendingPercentage),
                pendingCount,
                Color.parseColor("#FFA726"))); // Orange
        chartView.addPieSlice(new PieModel(
                String.format("Completed (%.1f%%)", completedPercentage),
                completedCount,
                Color.parseColor("#66BB6A"))); // Green
        chartView.addPieSlice(new PieModel(
                String.format("Overdue (%.1f%%)", overduePercentage),
                overdueCount,
                Color.parseColor("#EF5350"))); // Red

        // Animate the pie chart
        chartView.startAnimation();
    }





    private int getUserId() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id", -1);
    }

    private List<Task> parseTasks(JSONArray tasksArray) throws JSONException {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < tasksArray.length(); i++) {
            JSONObject taskObject = tasksArray.getJSONObject(i);
            tasks.add(new Task(
                    taskObject.getString("title"),
                    taskObject.getString("start_date"),
                    taskObject.getString("end_date"),
                    taskObject.getString("status")
            ));
        }
        return tasks;
    }

    private List<Project> parseProjects(JSONArray projectsArray) throws JSONException {
        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < projectsArray.length(); i++) {
            JSONObject projectObject = projectsArray.getJSONObject(i);
            projects.add(new Project(
                    projectObject.getString("projectTitle"),
                    projectObject.getInt("pendingTaskCount"),
                    projectObject.getString("projectDescription"),
                    projectObject.getString("projectStatus"),
                    projectObject.getString("startDate"),
                    projectObject.getString("endDate")



            ));
        }
        return projects;
    }

    // TaskAdapter
    private static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

        private List<Task> taskList;

        public TaskAdapter(List<Task> taskList) {
            this.taskList = taskList;
        }

        public void updateTasks(List<Task> newTasks) {
            this.taskList = newTasks;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_pending_task, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = taskList.get(position);
            holder.taskTitle.setText(task.getTitle());
            holder.taskStartDate.setText(task.getStartDate());
            holder.taskEndDate.setText(task.getEndDate());
            holder.taskStatus.setText(task.getStatus());
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        static class TaskViewHolder extends RecyclerView.ViewHolder {
            TextView taskTitle, taskStartDate, taskEndDate, taskStatus;

            public TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                taskTitle = itemView.findViewById(R.id.tvTaskTitle);
                taskEndDate = itemView.findViewById(R.id.tvEndTime);
                taskStatus = itemView.findViewById(R.id.tvTaskStatus);
                taskStartDate = itemView.findViewById(R.id.tvStartTime);


            }
        }
    }

    // ProjectAdapter
    private static class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

        private List<Project> projectList;

        public ProjectAdapter(List<Project> projectList) {
            this.projectList = projectList;
        }

        public void updateProjects(List<Project> newProjects) {
            this.projectList = newProjects;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_project_pending, parent, false);
            return new ProjectViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
            Project project = projectList.get(position);
            holder.projectName.setText(project.getName());
            holder.tvPendingTasksCount.setText(String.valueOf(project.getPendingTaskCount()));
            holder.tvTaskDescription.setText(project.getProjectDescription());
            holder.tvProjectStatus.setText(project.getProjectStatus());
            holder.tvStartDate.setText(project.getProjectStartDate());
            holder.tvEndDate.setText(project.getProjectEndDate());
        }


        @Override
        public int getItemCount() {
            return projectList.size();
        }

        static class ProjectViewHolder extends RecyclerView.ViewHolder {
            TextView projectName,tvTaskDescription,tvPendingTasksCount,tvProjectStatus,tvStartDate,tvEndDate;

            public ProjectViewHolder(@NonNull View itemView) {
                super(itemView);
                projectName = itemView.findViewById(R.id.tvTaskTitle);
                tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
                tvPendingTasksCount = itemView.findViewById(R.id.tvPendingTasksCount);
                tvProjectStatus = itemView.findViewById(R.id.tvProjectStatus);
                tvStartDate = itemView.findViewById(R.id.tvStartDate);
                tvEndDate = itemView.findViewById(R.id.tvEndDate);

            }
        }
    }

    // Task Model
    private static class Task {
        private final String title;
        private final String startDate;
        private final String endDate;
        private final String status;

        public Task(String title, String startDate, String endDate, String status) {
            this.title = title;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
        }

        public String getTitle() {
            return title;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getStatus() {
            return status;
        }
    }

    // Project Model
// Project Model
    private static class Project {
        private final String name;
        private final int pendingTaskCount;
        private final String projectDescription;
        private final String projectStartDate;
        private final String projectEndDate;
        private final String projectStatus;

        public Project(String name, int pendingTaskCount, String projectDescription, String projectStatus, String projectStartDate, String projectEndDate) {
            this.name = name;
            this.pendingTaskCount = pendingTaskCount;
            this.projectDescription = projectDescription;
            this.projectStartDate = projectStartDate;
            this.projectEndDate = projectEndDate;
            this.projectStatus = projectStatus;
        }

        public String getName() {
            return name;
        }

        public int getPendingTaskCount() {
            return pendingTaskCount;
        }

        public String getProjectDescription() {
            return projectDescription;
        }

        public String getProjectStartDate() {
            return projectStartDate;
        }

        public String getProjectEndDate() {
            return projectEndDate;
        }

        public String getProjectStatus() {
            return projectStatus;
        }
    }

}