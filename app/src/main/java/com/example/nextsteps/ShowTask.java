package com.example.nextsteps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowTask extends AppCompatActivity {

    private RecyclerView taskListView;
    private ShowTaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_task);

        taskListView = findViewById(R.id.taskListView);
        taskAdapter = new ShowTaskAdapter(taskList);
        taskListView.setAdapter(taskAdapter);
        taskListView.setLayoutManager(new LinearLayoutManager(this));
        loadTasks();
    }




    private void loadTasks() {
        String url = "https://mensstylehouse.com/aa/NextSteps/php/retriveTask.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        taskList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject taskObject = response.getJSONObject(i);
                            int id = taskObject.getInt("id");
                            String title = taskObject.getString("title");
                            String priority = taskObject.getString("priority");
                            String status = taskObject.getString("status");
                            String startDate = taskObject.getString("start_date");
                            String endDate = taskObject.getString("end_date");

                            taskList.add(new Task(id, title, priority, status, startDate, endDate));
                        }
                        taskAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing tasks", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            Toast.makeText(this, "Error fetching tasks: " + error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("VolleyError", "Error: " + error.toString());
        });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void updateTask(int taskId, String title, String priority, String startDate, String endDate) {
        String url = "https://mensstylehouse.com/aa/NextSteps/php/update_and_delete_task.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String statusResponse = jsonResponse.getString("status");

                        if ("success".equals(statusResponse)) {
                            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                            loadTasks();
                        } else {
                            String message = jsonResponse.optString("message", "Unknown error");
                            Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing update response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error updating task: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "update");
                params.put("id", String.valueOf(taskId));
                params.put("title", title);
                params.put("priority", priority);
                params.put("start_date", startDate);
                params.put("end_date", endDate);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void deleteTask(int taskId, int position) {
        String url = "https://mensstylehouse.com/aa/NextSteps/php/update_and_delete_task.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if ("success".equals(status)) {
                            taskList.remove(position);
                            taskAdapter.notifyItemRemoved(position);
                            Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = jsonResponse.optString("message", "Unknown error");
                            Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing delete response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error deleting task: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "delete");
                params.put("id", String.valueOf(taskId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private static class Task {
        int id;
        String title, priority, status, startDate, endDate;

        Task(int id, String title, String priority, String status, String startDate, String endDate) {
            this.id = id;
            this.title = title;
            this.priority = priority;
            this.status = status;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    private void markTaskAsComplete(int taskId, Button button, int position) {
        // Immediately disable the button and change the text to "Completed"
        button.setEnabled(false); // Disable to prevent multiple clicks
        String url = "https://mensstylehouse.com/aa/NextSteps/php/update_and_delete_task.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if ("success".equals(status)) {
                            taskList.get(position).status = "Completed";
                            button.setText("Completed");
                            taskAdapter.notifyItemChanged(position);
                            Toast.makeText(this, "Task marked as complete", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = jsonResponse.optString("message", "Unknown error");
                            button.setEnabled(true); // Allow retry
                            Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        button.setEnabled(true); // Allow retry
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    button.setEnabled(true); // Allow retry
                    Toast.makeText(this, "Failed to mark task as complete: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "update_status");
                params.put("id", String.valueOf(taskId));
                params.put("status", "Completed");
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);

    }






    private class ShowTaskAdapter extends RecyclerView.Adapter<ShowTaskAdapter.ShowViewHolder> {
        private List<Task> tasks;

        ShowTaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        private class ShowViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, priorityTextView, startDateTextView, endDateTextView;
            Button updateButton, deleteButton,mark_as_complete_button;

            public ShowViewHolder(@NonNull View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.taskTitle);
                priorityTextView = itemView.findViewById(R.id.taskPriority);
                startDateTextView = itemView.findViewById(R.id.startDates);
                endDateTextView = itemView.findViewById(R.id.endDates);
                updateButton = itemView.findViewById(R.id.updateButton);
                deleteButton = itemView.findViewById(R.id.deleteButton);
                mark_as_complete_button=itemView.findViewById(R.id.mark_as_complete_button);
            }
        }

        @NonNull
        @Override
        public ShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
            return new ShowViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ShowViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.titleTextView.setText(task.title);
            holder.priorityTextView.setText("Priority: " + task.priority);
            holder.startDateTextView.setText("Start Date: " + task.startDate);
            holder.endDateTextView.setText("End Date: " + task.endDate);


            // Set the button text and state based on the task's status
            if ("Completed".equals(task.status)) {
                holder.mark_as_complete_button.setText("Completed");
                holder.mark_as_complete_button.setEnabled(false); // Disable button if already completed
            } else {
                holder.mark_as_complete_button.setText("Mark as Complete");
                holder.mark_as_complete_button.setEnabled(true); // Enable button if not completed
            }


            holder.updateButton.setOnClickListener(v -> {
                // Handle update task
                updateTask(task.id, task.title, task.priority, task.startDate, task.endDate);
            });

            holder.deleteButton.setOnClickListener(v -> {
                // Handle delete task
                deleteTask(task.id, position);
            });
            holder.mark_as_complete_button.setOnClickListener(view ->
                    markTaskAsComplete(task.id, holder.mark_as_complete_button,position)
            );


        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }
    }
}
