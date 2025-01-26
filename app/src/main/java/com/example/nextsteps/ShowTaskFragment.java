package com.example.nextsteps;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowTaskFragment extends Fragment {

    private RecyclerView taskListView;
    private ShowTaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private SearchView search_view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_task, container, false);

        taskListView = view.findViewById(R.id.taskListView);
        search_view = view.findViewById(R.id.search_view);
        taskAdapter = new ShowTaskAdapter(taskList);
        taskListView.setAdapter(taskAdapter);
        taskListView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load tasks when the fragment is created
        loadTasks();

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchTasks(query);  // Perform search when the user submits the query
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    // If the query is empty, fetch all project details
                    loadTasks();  // Reload all projects
                } else {
                    // Otherwise, perform search with the new query
                    searchTasks(newText);
                }
                return false;
            }
        });

        return view;
    }



    private void searchTasks(String searchQuery) {
        SharedPreferences prefs = getActivity().getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        int userId = prefs.getInt("id", -1); // Get the logged-in user's ID

        if (userId == -1) {
            // Handle case where no user is logged in
            Toast.makeText(getContext(), "user id not found", Toast.LENGTH_SHORT).show();

            return;
        }

        String url = "https://mensstylehouse.com/aa/NextSteps/php/searchTask.php";

        // Create a new StringRequest for POST request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        taskList.clear();
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray tasks = jsonResponse.getJSONArray("tasks");
                            for (int i = 0; i < tasks.length(); i++) {
                                JSONObject taskObject = tasks.getJSONObject(i);
                                int id = taskObject.getInt("id");
                                String title = taskObject.getString("title");
                                String priority = taskObject.getString("priority");
                                String status = taskObject.getString("status");
                                String startDate = taskObject.getString("start_date");
                                String endDate = taskObject.getString("end_date");

                                taskList.add(new Task(id, title, priority, status, startDate, endDate));
                            }
                            taskAdapter.notifyDataSetChanged();
                        } else {
                            String message = jsonResponse.optString("message", "No tasks found");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ErrorParsingTask", e.getMessage());
                        Toast.makeText(getContext(), "Error parsing tasks", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Error fetching tasks: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("VolleyError", "Error: " + error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Send search query and user ID as POST parameters
                Map<String, String> params = new HashMap<>();
                params.put("search", searchQuery);  // Add search query to POST data
                params.put("user_id", String.valueOf(userId));  // Add user ID to ensure only their tasks are returned
                return params;
            }
        };

        // Adding the request to the Volley queue
        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }


    private void loadTasks() {
        SharedPreferences prefs = getActivity().getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        int userId = prefs.getInt("id",-1); // Get the logged-in user's ID

        if (userId == -1) {
            // Handle case where no user is logged in (maybe show an error or prompt the user to log in)
            Toast.makeText(getContext(),"userId in null",Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://mensstylehouse.com/aa/NextSteps/php/retriveTask.php?user_id=" + userId;

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
                        Toast.makeText(getContext(), "Error parsing tasks", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Error fetching tasks: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("VolleyError", "Error: " + error.toString());
                });

        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest);
    }



    private void updateTask(int taskId, String title, String priority, String startDate, String endDate) {
        String url = "https://mensstylehouse.com/aa/NextSteps/php/update_and_delete_task.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String statusResponse = jsonResponse.getString("status");

                        if ("success".equals(statusResponse)) {
                            Toast.makeText(getContext(), "Task updated successfully", Toast.LENGTH_SHORT).show();
                            loadTasks();
                        } else {
                            String message = jsonResponse.optString("message", "Unknown error");
                            Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing update response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Error updating task: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(requireContext()).add(stringRequest);
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
                            Toast.makeText(getContext(), "Task deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = jsonResponse.optString("message", "Unknown error");
                            Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing delete response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Error deleting task: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "delete");
                params.put("id", String.valueOf(taskId));
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(stringRequest);
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

    private class ShowTaskAdapter extends RecyclerView.Adapter<ShowTaskAdapter.ShowViewHolder> {
        private List<Task> tasks;

        ShowTaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        private class ShowViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, priorityTextView, startDateTextView, endDateTextView;
            Button updateButton, deleteButton, markAsCompleteButton;

            public ShowViewHolder(@NonNull View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.taskTitle);
                priorityTextView = itemView.findViewById(R.id.taskPriority);
                startDateTextView = itemView.findViewById(R.id.startDates);
                endDateTextView = itemView.findViewById(R.id.endDates);
                updateButton = itemView.findViewById(R.id.updateButton);
                deleteButton = itemView.findViewById(R.id.deleteButton);
                markAsCompleteButton = itemView.findViewById(R.id.mark_as_complete_button);
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

            if ("Completed".equals(task.status)) {
                holder.markAsCompleteButton.setText("Completed");
                holder.markAsCompleteButton.setEnabled(false);
            } else {
                holder.markAsCompleteButton.setText("Mark as Complete");
                holder.markAsCompleteButton.setEnabled(true);
            }

            holder.updateButton.setOnClickListener(v -> updateTask(task.id, task.title, task.priority, task.startDate, task.endDate));
            holder.deleteButton.setOnClickListener(v -> deleteTask(task.id, position));
            holder.markAsCompleteButton.setOnClickListener(v -> {
                holder.markAsCompleteButton.setEnabled(false);
                markTaskAsComplete(task.id, holder.markAsCompleteButton, position);
            });
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }
    }

    private void markTaskAsComplete(int taskId, Button button, int position) {
        button.setEnabled(false);
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
                            Toast.makeText(getContext(), "Task marked as complete", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = jsonResponse.optString("message", "Unknown error");
                            button.setEnabled(true);
                            Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        button.setEnabled(true);
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    button.setEnabled(true);
                    Toast.makeText(getContext(), "Failed to mark task as complete: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }
}
