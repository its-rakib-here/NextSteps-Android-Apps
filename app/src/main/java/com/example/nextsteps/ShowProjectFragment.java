package com.example.nextsteps;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowProjectFragment extends Fragment {

    private static final String PROJECT_DETAILS_URL = "https://mensstylehouse.com/aa/NextSteps/php/get_project_details.php";
    private RecyclerView recyclerView;
    private ProjectAdapter projectAdapter;
    private List<Project> projectList;
    private SearchView search_view;

    private static final String SEARCH_PROJECT_URL = "https://mensstylehouse.com/aa/NextSteps/php/searchProject.php";


    public ShowProjectFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_project, container, false);

        recyclerView = view.findViewById(R.id.project_recycleview);
        search_view = view.findViewById(R.id.search_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        projectList = new ArrayList<>();
        projectAdapter = new ProjectAdapter(projectList);
        recyclerView.setAdapter(projectAdapter);

        fetchProjectDetails();

        // Add listener to filter projects based on search query
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProjects(query);  // Call searchProjects to get filtered data from server
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProjects(newText);  // Call searchProjects to get filtered data from server
                return false;
            }
        });

        return view;
    }

    // This method will fetch and update the project list based on the search query
    private void searchProjects(String query) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEARCH_PROJECT_URL,
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

                                projectList.add(new Project(projectId, projectTitle, projectDescription, startDate, finishDate, status));
                            }

                            projectAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("ShowProjectFragment", "JSON Parsing error: " + e.getMessage());
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ShowProjectFragment", "Volley error: " + error.getMessage());
                    Toast.makeText(getContext(), "Failed to search projects. Please try again.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("query", query);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(stringRequest);
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

                                projectList.add(new Project(projectId, projectTitle, projectDescription, startDate, finishDate, status));
                            }

                            projectAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("ShowProjectFragment", "JSON Parsing error: " + e.getMessage());
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ShowProjectFragment", "Volley error: " + error.getMessage());
                    Toast.makeText(getContext(), "Failed to fetch project details. Please try again.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "fetchProjects");
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }

    // Inner Project class
    public class Project {
        private int projectID;
        private String projectTitle;
        private String projectDescription;
        private String startDate;
        private String finishDate;
        private String status;

        public Project(int projectID, String projectTitle, String projectDescription, String startDate, String finishDate, String status) {
            this.projectID = projectID;
            this.projectTitle = projectTitle;
            this.projectDescription = projectDescription;
            this.startDate = startDate;
            this.finishDate = finishDate;
            this.status = status;
        }

        public int getProjectID() {
            return projectID;
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
    }

    // Inner ProjectAdapter class
    public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

        private List<Project> projectList;

        public ProjectAdapter(List<Project> projectList) {
            this.projectList = projectList;
        }

        @NonNull
        @Override
        public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);
            return new ProjectViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
            Project project = projectList.get(position);
            holder.project_title.setText(project.getProjectTitle());
            holder.project_description.setText(project.getProjectDescription());
            holder.start_date.setText(project.getStartDate());
            holder.end_date.setText(project.getFinishDate());
            holder.project_status.setText(project.getStatus());

            if ("completed".equals(project.getStatus())) {
                holder.mark_as_complete_button.setText("Completed");
                holder.mark_as_complete_button.setEnabled(false);
            } else {
                holder.mark_as_complete_button.setText("Mark as Complete");
                holder.mark_as_complete_button.setEnabled(true);
            }

            holder.project.setOnClickListener(view -> {
                ArrayList<Integer> selectedProjectIds = new ArrayList<>();
                selectedProjectIds.add(project.getProjectID());
                Intent intent = new Intent(getActivity(), ShowTaskUnderProject.class);
                intent.putExtra("project_ids", selectedProjectIds);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return projectList.size();
        }

        public class ProjectViewHolder extends RecyclerView.ViewHolder {
            TextView project_title, project_description, start_date, end_date, project_status;
            Button mark_as_complete_button;
            LinearLayout project;

            public ProjectViewHolder(View itemView) {
                super(itemView);
                project_title = itemView.findViewById(R.id.project_title);
                project_description = itemView.findViewById(R.id.project_description);
                start_date = itemView.findViewById(R.id.start_date);
                end_date = itemView.findViewById(R.id.end_date);
                project_status = itemView.findViewById(R.id.project_status);
                mark_as_complete_button = itemView.findViewById(R.id.mark_as_complete_button);
                project = itemView.findViewById(R.id.project);
            }
        }
    }
}
