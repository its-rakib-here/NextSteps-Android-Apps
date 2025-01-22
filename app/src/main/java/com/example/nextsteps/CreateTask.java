package com.example.nextsteps;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateTask extends AppCompatActivity {

    private static final String TAG = "CreateTask";

    // Constants for URLs and SharedPreferences keys
    private static final String BASE_URL = "https://mensstylehouse.com/aa/NextSteps/php/";
    private static final String CREATE_TASK_URL = BASE_URL + "createtask.php";
    private static final String UPDATE_TASK_URL = BASE_URL + "update_and_delete_task.php";
    private static final String FETCH_TASK_URL = BASE_URL + "get_task_details.php";
    private static final String SHARED_PREF_NAME = "MyApp";
    private static final String USER_ID_KEY = "id";

    // UI Elements
    private TextView starting_date, ending_date;
    private EditText task_title;
    private RadioGroup priority_radio_group;
    private String selectedPriority = "";
    private Button save_button;

    private int currentTaskId = -1; // Default to -1 for new task creation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // Initialize UI elements
        initializeUI();

        // Set up calendar icon click listeners
        findViewById(R.id.starting_calendar_icon).setOnClickListener(view -> showDatePicker(starting_date));
        findViewById(R.id.endig_calendar_icon).setOnClickListener(view -> showDatePicker(ending_date));

        // Handle priority selection
        priority_radio_group.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            selectedPriority = selectedRadioButton != null ? selectedRadioButton.getText().toString() : "";
        });

        // Check for task update scenario
        if (getIntent().hasExtra("task_id")) {
            currentTaskId = getIntent().getIntExtra("task_id", -1);
            fetchTaskDetails(currentTaskId); // Load task details for editing
        }

        // Handle save button click
        save_button.setOnClickListener(view -> handleSaveButtonClick());
    }

    private void initializeUI() {
        starting_date = findViewById(R.id.starting_date);
        ending_date = findViewById(R.id.ending_date);
        task_title = findViewById(R.id.task_title);
        priority_radio_group = findViewById(R.id.priority_radio_group);
        save_button = findViewById(R.id.save_button); // FIX: Ensure save_button is initialized
    }

    private void showDatePicker(TextView targetTextView) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    targetTextView.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void handleSaveButtonClick() {
        String taskTitle = task_title.getText().toString();
        String startDate = starting_date.getText().toString();
        String endDate = ending_date.getText().toString();

        int userId = getUserIdFromPreferences();
        if (userId == -1) {
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isInputInvalid(taskTitle, startDate, endDate)) return;

        if (currentTaskId != -1) {
            // Update task
            updateTask(currentTaskId, taskTitle, startDate, endDate, userId);
        } else {
            // Create a new task
            createTask(taskTitle, startDate, endDate, userId);
        }
    }

    private boolean isInputInvalid(String taskTitle, String startDate, String endDate) {
        if (taskTitle.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || selectedPriority.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!isDateOrderValid(startDate, endDate)) {
            Toast.makeText(this, "Start date must be before or equal to end date.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private int getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(USER_ID_KEY, -1);
    }

    private boolean isDateOrderValid(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return !sdf.parse(startDate).after(sdf.parse(endDate));
        } catch (ParseException e) {
            Log.e(TAG, "Date parsing error", e);
            return false;
        }
    }

    private void createTask(String taskTitle, String startDate, String endDate, int userId) {
        sendTaskRequest(CREATE_TASK_URL, -1, taskTitle, startDate, endDate, userId);
    }

    private void updateTask(int taskId, String taskTitle, String startDate, String endDate, int userId) {
        sendTaskRequest(UPDATE_TASK_URL, taskId, taskTitle, startDate, endDate, userId);
    }

    private void sendTaskRequest(String url, int taskId, String taskTitle, String startDate, String endDate, int userId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    String message = taskId == -1 ? "Task created successfully!" : "Task updated successfully!";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    resetFields();
                },
                error -> {
                    Log.e(TAG, "Error saving task", error);
                    Toast.makeText(this, "Failed to save task. Try again.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (taskId != -1) params.put("task_id", String.valueOf(taskId));
                params.put("title", taskTitle);
                params.put("start_date", startDate);
                params.put("end_date", endDate);
                params.put("priority", selectedPriority);
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void fetchTaskDetails(int taskId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_TASK_URL,
                this::populateTaskDetails,
                error -> {
                    Log.e(TAG, "Error fetching task details", error);
                    Toast.makeText(this, "Failed to fetch task details.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("task_id", String.valueOf(taskId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void populateTaskDetails(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            task_title.setText(jsonObject.getString("title"));
            starting_date.setText(jsonObject.getString("start_date"));
            ending_date.setText(jsonObject.getString("end_date"));

            String priority = jsonObject.getString("priority");
            if (priority.equals("High")) {
                priority_radio_group.check(R.id.priority_high);
            } else if (priority.equals("Medium")) {
                priority_radio_group.check(R.id.priority_medium);
            } else {
                priority_radio_group.check(R.id.priority_low);
            }
            Log.d("server", "details" + response.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing task details", e);
            Toast.makeText(this, "Failed to load task details.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetFields() {
        task_title.setText("");
        starting_date.setText("");
        ending_date.setText("");
        priority_radio_group.clearCheck();
        selectedPriority = "";
        currentTaskId = -1; // Reset task ID to indicate new task creation
    }
}
