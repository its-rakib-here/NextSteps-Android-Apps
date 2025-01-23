package com.example.nextsteps;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProjectActivity extends AppCompatActivity {

    private LinearLayout taskContainer;
    private Button add_new_task_button, save_project_button;
    private TextView project_finish_date, project_start_date;
    private EditText project_description, project_title;

    private static final String PROJECT_URL = "https://mensstylehouse.com/aa/NextSteps/php/create_project.php";
    private static final String TASK_URL = "https://mensstylehouse.com/aa/NextSteps/php/create-task_under_project.php";
    private static final String USER_ID = "id";
    private static final String PREFS_NAME = "MyApp";

    private RequestQueue requestQueue;

    private int projectId = -1;  // To store the project ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Initialize views
        taskContainer = findViewById(R.id.task_container);
        add_new_task_button = findViewById(R.id.add_new_task_button);
        project_finish_date = findViewById(R.id.project_finish_date);
        project_start_date = findViewById(R.id.project_start_date);
        save_project_button = findViewById(R.id.save_project_button);
        project_description = findViewById(R.id.project_description);
        project_title = findViewById(R.id.project_title);

        // Initialize RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Add task button functionality
        project_start_date.setOnClickListener(view -> showDatePicker(project_start_date));
        project_finish_date.setOnClickListener(view -> showDatePicker(project_finish_date));

        add_new_task_button.setOnClickListener(v -> addNewTask());

        // Save project button functionality
        save_project_button.setOnClickListener(v -> saveProject());
    }

    private int getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(USER_ID, -1);
    }

    private void addNewTask() {
        if (projectId == -1) {
            Log.e("Debug", "Invalid project ID");
            Toast.makeText(this, "Please save the project first", Toast.LENGTH_SHORT).show();
            return;
        }

        View taskView = getLayoutInflater().inflate(R.layout.task_layout, taskContainer, false);
        EditText task_title = taskView.findViewById(R.id.task_title);
        EditText task_description = taskView.findViewById(R.id.task_description);
        EditText distribute_task_name = taskView.findViewById(R.id.distribute_task_name);
        TextView start_time = taskView.findViewById(R.id.start_time);
        TextView end_time = taskView.findViewById(R.id.end_time);
        Button delete_task_button = taskView.findViewById(R.id.delete_task_button);
        Button save_task_button = taskView.findViewById(R.id.save_task_button);

        start_time.setOnClickListener(v -> showDatePicker(start_time));
        end_time.setOnClickListener(v -> showDatePicker(end_time));

        delete_task_button.setOnClickListener(v -> taskContainer.removeView(taskView));

        save_task_button.setOnClickListener(v -> saveTask(task_title, distribute_task_name,task_description, start_time, end_time, taskView));
        taskContainer.addView(taskView);

    }


    private void showDatePicker(TextView targetTextView) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            targetTextView.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveProject() {
        String title = project_title.getText().toString().trim();
        String description = project_description.getText().toString().trim();
        String startDate = project_start_date.getText().toString().trim();
        String endDate = project_finish_date.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
            Toast.makeText(this, "Please fill all project details", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PROJECT_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        if (jsonResponse.getBoolean("success")) {
                            projectId = jsonResponse.getInt("project_id");
                            saveProjectIdToPreferences(projectId);
                            Log.d("ProjectSave", "Project ID: " + projectId);
                            Log.d("ProjectSave", "User ID: " + getUserIdFromPreferences());

                            Toast.makeText(this, "Project saved successfully", Toast.LENGTH_SHORT).show();

                            // Hide the UI after successful save
                           // hideSaveProjectUI();
                        } else {
                            String message = jsonResponse.getString("message");
                            Log.e("ProjectSave", "Server Error: " + message);
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("ProjectSave", "JSON Parsing Error: " + e.getMessage(), e);
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        String responseBody;
                        try {
                            responseBody = new String(error.networkResponse.data, "utf-8");
                            Log.e("ProjectSave", "Error Code: " + statusCode + " Response: " + responseBody);
                        } catch (Exception e) {
                            Log.e("ProjectSave", "Error parsing error response: " + e.getMessage(), e);
                        }
                    } else {
                        Log.e("ProjectSave", "Error: " + error.getMessage(), error);
                    }
                    Toast.makeText(this, "Failed to save project: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("project_title", title);
                params.put("project_description", description);
                params.put("project_start_date", startDate);
                params.put("project_finish_date", endDate);
                params.put("user_id", String.valueOf(getUserIdFromPreferences()));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


    private void hideSaveProjectUI() {
        // Hide all views except the "Add New Task" button
        findViewById(R.id.project_title_label).setVisibility(View.GONE);
        findViewById(R.id.project_title).setVisibility(View.GONE);
        findViewById(R.id.project_description).setVisibility(View.GONE);
        findViewById(R.id.project_dates_label).setVisibility(View.GONE);
        findViewById(R.id.project_start_date_label).setVisibility(View.GONE);
        findViewById(R.id.project_start_date).setVisibility(View.GONE);
        findViewById(R.id.project_finish_date_label).setVisibility(View.GONE);
        findViewById(R.id.project_finish_date).setVisibility(View.GONE);
        findViewById(R.id.task_section_title).setVisibility(View.GONE);
        findViewById(R.id.task_container).setVisibility(View.GONE);
        findViewById(R.id.save_project_button).setVisibility(View.GONE);

        // Ensure "Add New Task" button remains visible
        findViewById(R.id.add_new_task_button).setVisibility(View.VISIBLE);
    }

    private void saveProjectIdToPreferences(int projectId) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("project_id", projectId);
        editor.apply();
    }

    private void saveTask(EditText taskTitle,EditText distribute_task_name, EditText taskDescription, TextView startTime, TextView endTime, View taskView) {
        String taskTitleText = taskTitle.getText().toString().trim();
        String taskDescriptionText = taskDescription.getText().toString().trim();
        String startTimeText = startTime.getText().toString().trim();
        String endTimeText = endTime.getText().toString().trim();
        String name =distribute_task_name.getText().toString().trim();

        if (TextUtils.isEmpty(taskTitleText) || TextUtils.isEmpty(taskDescriptionText) ||
                TextUtils.isEmpty(startTimeText) || TextUtils.isEmpty(endTimeText)) {
            Toast.makeText(this, "Please fill all task details", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject taskObject = new JSONObject();
        try {
            taskObject.put("project_id", projectId);
            taskObject.put("task_title", taskTitleText);
            taskObject.put("task_description", taskDescriptionText);
            taskObject.put("start_date", startTimeText);
            taskObject.put("end_date", endTimeText);
            taskObject.put("distribute_task_name", name);

            Log.d("TaskData", "Task JSON: " + taskObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating task data", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, TASK_URL,
                response -> {
                    Log.d("ServerResponse", "Response: " + response);
                   // Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show();
                    taskContainer.removeView(taskView);  // Remove the task layout after saving
                },
                error -> {
                    String errorMessage = error.networkResponse != null && error.networkResponse.data != null
                            ? new String(error.networkResponse.data)
                            : error.getMessage();
                    Log.e("ErrorResponse", "Error: " + errorMessage);
                    Toast.makeText(this, "Failed to save task: " + errorMessage, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String taskData = "[" + taskObject.toString() + "]";
                params.put("task_data", taskData);
                Log.d("RequestParams", "Params: " + params);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


}
