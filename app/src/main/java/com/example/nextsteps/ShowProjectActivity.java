package com.example.nextsteps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowProjectActivity extends AppCompatActivity {

    private static final String PROJECT_DETAILS_URL = "https://mensstylehouse.com/aa/NextSteps/php/get_project_details.php";
    private RecyclerView recyclerView;
    private ProjectAdapter projectAdapter;
    private List<Project> projectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_project);

        recyclerView = findViewById(R.id.project_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectList = new ArrayList<>();
        projectAdapter = new ProjectAdapter(projectList);
        recyclerView.setAdapter(projectAdapter);

        // Fetch all projects (no need for project_id)
        fetchProjectDetails();
    }

    private void fetchProjectDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PROJECT_DETAILS_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray projectsArray = jsonResponse.getJSONArray("projects");
                            projectList.clear();

                            for (int i = 0; i < projectsArray.length(); i++) {
                                JSONObject projectObject = projectsArray.getJSONObject(i);
                                int projectId = projectObject.getInt("id");
                                String projectTitle = projectObject.getString("title");
                                String projectDescription = projectObject.getString("description");
                                String startDate = projectObject.getString("start_date");
                                String finishDate = projectObject.getString("end_date");
                                String status = projectObject.getString("status");

                                // Add the project to the list
                                projectList.add(new Project(projectId, projectTitle, projectDescription, startDate, finishDate, status));
                            }

                            projectAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("ShowProjectActivity", "JSON Parsing error: " + e.getMessage());
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ShowProjectActivity", "Volley error: " + error.getMessage());
                    Toast.makeText(this, "Failed to fetch project details. Please try again.", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "fetchProjects");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }



    // Project model class
    public class Project {

        private int projectID;
        private String projectTitle;
        private String projectDescription;
        private String startDate;
        private String finishDate;
        private String status;

        public Project(int projectID,String projectTitle, String projectDescription, String startDate, String finishDate, String status) {
            this.projectID=projectID;
            this.projectTitle = projectTitle;
            this.projectDescription = projectDescription;
            this.startDate = startDate;
            this.finishDate = finishDate;
            this.status = status;
        }

        public String getProjectTitle() {
            return projectTitle;
        }

        public String getProjectDescription() {
            return projectDescription;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getFinishDate() {
            return finishDate;
        }

        public String getStatus() {
            return status;
        }

        public int getProjectID() {
            return projectID;
        }
    }

    // Project Adapter
    public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

        private List<Project> projectList;

        public ProjectAdapter(List<Project> projectList) {
            this.projectList = projectList;
        }

        @Override
        public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);
            return new ProjectViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ProjectViewHolder holder, int position) {
            Project project = projectList.get(position);
            holder.project_title.setText(project.getProjectTitle());
            holder.project_description.setText(project.getProjectDescription());
            holder.start_date.setText(project.getStartDate());
            holder.end_date.setText(project.getFinishDate());
            holder.project_status.setText(project.getStatus());

            // Disable the button if the project is already completed
            if ("completed".equals(project.getStatus())) {
                holder.mark_as_complete_button.setText("Completed");
                holder.mark_as_complete_button.setEnabled(false);
            } else {
                holder.mark_as_complete_button.setText("Mark as Complete");
                holder.mark_as_complete_button.setEnabled(true);
            }

            holder.project.setOnClickListener(view -> {
                // Assume 'project' contains the list of IDs or add logic to collect multiple project IDs
                ArrayList<Integer> selectedProjectIds = new ArrayList<>();
                selectedProjectIds.add(project.getProjectID());
                Intent intent = new Intent(ShowProjectActivity.this, ShowTaskUnderProject.class);
                intent.putExtra("project_ids", selectedProjectIds);
                startActivity(intent);
            });

            holder.delete_button.setOnClickListener(view -> deleteProject(project.getProjectID()));

            holder.mark_as_complete_button.setOnClickListener(view -> markProjectAsComplete(project.getProjectID(), holder.mark_as_complete_button));
        }


        @Override
        public int getItemCount() {
            return projectList.size();
        }

        public class ProjectViewHolder extends RecyclerView.ViewHolder {

            public TextView project_title, project_description, start_date, end_date, project_status;
            LinearLayout project;
            Button delete_button,update_button,mark_as_complete_button;

            public ProjectViewHolder(View itemView) {
                super(itemView);
                project_title = itemView.findViewById(R.id.project_title);
                project_description = itemView.findViewById(R.id.project_description);
                start_date = itemView.findViewById(R.id.start_date);
                end_date = itemView.findViewById(R.id.end_date);
                project_status = itemView.findViewById(R.id.project_status);
                project=itemView.findViewById(R.id.project);
                delete_button=itemView.findViewById(R.id.delete_button);
                update_button=itemView.findViewById(R.id.update_button);
                mark_as_complete_button=itemView.findViewById(R.id.mark_as_complete_button);

            }
        }
    }

    private void markProjectAsComplete(int projectId, Button button) {
        // Immediately disable the button and change the text to "Completed"
        button.setText("Completed");
        button.setEnabled(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PROJECT_DETAILS_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            Toast.makeText(this, "Project marked as complete", Toast.LENGTH_SHORT).show();
                        } else {
                            // If the request fails, enable the button back for retrying
                            button.setEnabled(true);
                            button.setText("Mark as Complete");
                            Toast.makeText(this, "Failed to mark project as complete", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // In case of error, enable the button again to allow retry
                        button.setEnabled(true);
                        button.setText("Mark as Complete");
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // In case of network error, enable the button back for retrying
                    button.setEnabled(true);
                    button.setText("Mark as Complete");
                    Toast.makeText(this, "Failed to mark project as complete: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "markProjectComplete");
                params.put("project_id", String.valueOf(projectId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchProjectDetails(); // Refresh the UI with the current filter
    }

    private void deleteProject(int projectId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PROJECT_DETAILS_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            Toast.makeText(this, "Project deleted successfully", Toast.LENGTH_SHORT).show();
                            fetchProjectDetails(); // Refresh the list
                        } else {
                            Toast.makeText(this, "Failed to delete project", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to delete project: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "deleteProject");
                params.put("project_id", String.valueOf(projectId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

}
