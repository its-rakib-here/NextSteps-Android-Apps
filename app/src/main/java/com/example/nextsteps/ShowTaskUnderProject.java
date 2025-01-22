package com.example.nextsteps;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowTaskUnderProject extends AppCompatActivity {



    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private static final String BASE_URL = "https://mensstylehouse.com/aa/NextSteps/php/manageProjectTask.php";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_task_under_project);

        recyclerView = findViewById(R.id.task_under_project_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);  // Initially hide the progress bar

        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(adapter);

        // Retrieve project IDs from the intent
        ArrayList<Integer> projectIds = (ArrayList<Integer>) getIntent().getSerializableExtra("project_ids");

        if (projectIds != null && !projectIds.isEmpty()) {
            Log.d("ShowTaskUnderProject", "Received Project IDs: " + projectIds.toString());
            // Fetch tasks for these project IDs
            fetchTasks(projectIds);
        } else {
            Toast.makeText(this, "No project IDs passed", Toast.LENGTH_SHORT).show();
        }
    }



    private void fetchTasks(List<Integer> projectIds) {
        String url = BASE_URL;

        // Create a JSON array to send as the POST request body
        JSONArray projectIdsJsonArray = new JSONArray(projectIds);
        JSONObject postData = new JSONObject();
        try {
            postData.put("project_ids", projectIdsJsonArray);
            Log.d("PostData", postData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating request data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar while waiting for the response
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    progressBar.setVisibility(View.GONE); // Hide the progress bar when response is received
                    Log.d("RawResponse", "Response: " + response.toString());
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray tasks = response.getJSONArray("tasks");
                            taskList.clear();

                            if (tasks.length() == 0) {
                                Log.d("TaskArray", "No tasks found in the array.");
                                Toast.makeText(this, "No tasks found", Toast.LENGTH_SHORT).show();
                            }

                            // In the response handling section
                            for (int i = 0; i < tasks.length(); i++) {
                                JSONObject obj = tasks.getJSONObject(i);

                                String status = obj.optString("status", "pending"); // Default to "pending" if status is missing
                                taskList.add(new Task(
                                        obj.getString("task_id"),
                                        obj.getString("title"),
                                        obj.getString("description"),
                                        obj.getString("start_date"),
                                        obj.getString("end_date"),
                                        status, // Using the safely fetched status
                                        obj.optString("distribute_task_name", "Not Assigned") // Default to "Not Assigned" if missing
                                ));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No tasks found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSONException", "Error parsing response", e);
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar on error
                    Log.e("VolleyError", "Error: " + error.getMessage());
                    error.printStackTrace();
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }
        );

        // Add the request to the Volley queue
        Volley.newRequestQueue(this).add(request);
    }



    // Method to mark task as completed
    private void markTaskAsComplete(int taskId) {
        String url = BASE_URL;

        // Prepare the data to update the task status
        JSONObject postData = new JSONObject();
        try {
            postData.put("task_id", taskId);
            Log.d("TaskID", "Task ID sent: " + taskId);

            postData.put("status", "completed"); // Update the task to completed
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating request data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar while waiting for the response
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    progressBar.setVisibility(View.GONE); // Hide the progress bar when response is received
                    try {
                        if (response.getBoolean("success")) {
                            // Task marked as completed successfully
                            Toast.makeText(this, "Task marked as complete", Toast.LENGTH_SHORT).show();
                            // Update the button text to "Completed" and update UI
                        } else {
                            Toast.makeText(this, "Error updating task", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar on error
                    Log.e("VolleyError", "Error: " + error.getMessage());
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }
        );

        // Add the request to the Volley queue
        Volley.newRequestQueue(this).add(request);
    }





    // Task class constructor adjusted to match the response format
    public class Task {
        private String task_id;
        private String title;
        private String description;
        private String startDate;
        private String endDate;
        private String distributeTaskName;
        private String status;

        public Task(String task_id,String title, String description, String startDate, String endDate, String status,String distributeTaskName) {
            this.task_id=task_id;
            this.title = title;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status=status;
            this.distributeTaskName = distributeTaskName;
        }

        public int getTask_id() {
            try {
                return Integer.parseInt(task_id);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return -1; // or another default/error value
            }
        }


        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getDistributeTaskName() {
            return distributeTaskName;
        }
        public String getStatus() {
            return status;
        }
    }

    // Adapter for the RecyclerView
    private class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

        private final List<Task> tasks;

        TaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_under_project_item, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.title.setText(task.getTitle());
            holder.description.setText(task.getDescription());
            holder.startTime.setText(task.getStartDate());
            holder.endTime.setText(task.getEndDate());
            holder.distributeTaskName.setText(task.getDistributeTaskName());

            // Check if the task is already completed and disable the button if so
            if ("completed".equals(task.getStatus())) {
                holder.mark_as_complete_button.setText("Completed");
                holder.mark_as_complete_button.setEnabled(false); // Disable the button
            } else {
                holder.mark_as_complete_button.setText("Mark as Complete");
                holder.mark_as_complete_button.setEnabled(true); // Enable the button
            }

            holder.mark_as_complete_button.setOnClickListener(view -> {
                // Call the method to mark the task as completed
                markTaskAsComplete(task.getTask_id());
                // Change the button text to "Completed"
                holder.mark_as_complete_button.setText("Completed");
                holder.mark_as_complete_button.setEnabled(false); // Disable the button
            });



            holder.delete_task_button.setOnClickListener(v -> {
                Log.d("TaskID", "Task ID sent: " + task.getTask_id());

                deleteTask(task.getTask_id()); // Call deleteTask when the button is clicked
            });
        }


        @Override
        public int getItemCount() {
            return tasks.size();
        }

        class TaskViewHolder extends RecyclerView.ViewHolder {
            TextView title, description, startTime, endTime, distributeTaskName, status;
            Button mark_as_complete_button,delete_task_button,update_task_button;

            TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.task_title);
                description = itemView.findViewById(R.id.task_description);
                startTime = itemView.findViewById(R.id.start_time);
                endTime = itemView.findViewById(R.id.end_time);
                distributeTaskName = itemView.findViewById(R.id.distribute_task_name);
                //status = itemView.findViewById(R.id.task_status);
                mark_as_complete_button = itemView.findViewById(R.id.mark_as_complete_button);
                delete_task_button = itemView.findViewById(R.id.delete_task_button);
                update_task_button = itemView.findViewById(R.id.update_task_button);

            }
        }
    }

    // Method to delete task
    private void deleteTask(int taskId) {
        String url = BASE_URL;

        // Prepare the data to delete the task
        JSONObject postData = new JSONObject();
        try {
            postData.put("task_id", taskId); // The task ID to be deleted
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating request data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar while waiting for the response
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    progressBar.setVisibility(View.GONE); // Hide the progress bar when response is received
                    try {
                        Log.d("Response","response"+response.toString());
                        if (response.getBoolean("success")) {
                            // Task deleted successfully
                            Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                            // Optionally, refresh the task list to remove the deleted task
                            // You can notify the adapter to remove the item from the list
                        } else {
                            Toast.makeText(this, "Error deleting task", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar on error
                    Log.e("VolleyError", "Error: " + error.getMessage());
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }
        );

        // Add the request to the Volley queue
        Volley.newRequestQueue(this).add(request);
    }


}


