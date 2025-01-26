package com.example.nextsteps;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    TextView user_email,user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize the Log Out button
        Button logoutButton = findViewById(R.id.logout_button);
        user_email=findViewById(R.id.user_email);
        user_name=findViewById(R.id.user_name);

        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("id", -1);
        fetchUserData(userId);


        // Set onClickListener for Log Out button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
    }

    private void logoutUser() {
        String url = "https://mensstylehouse.com/aa/NextSteps/php/logout.php"; // Replace with your actual logout URL

        // Retrieve the user_id from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create JSON object with user_id
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("user_id", userId);
        } catch (JSONException e) {
            Log.e("LogoutError", "Error creating request JSON: " + e.getMessage());
            return;
        }

        // Make a POST request to logout endpoint
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestData,
                response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");

                        if ("success".equalsIgnoreCase(status)) {
                            // Clear stored user data
                            clearUserSession();

                            // Show success message
                            Toast.makeText(UserProfile.this, message, Toast.LENGTH_SHORT).show();

                            // Navigate back to LoginActivity
                            Intent intent = new Intent(UserProfile.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Show error message
                            Toast.makeText(UserProfile.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("LogoutError", "JSON parsing error: " + e.getMessage());
                        Toast.makeText(UserProfile.this, "Response error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("LogoutError", "Network error: " + error.getMessage());
                    Toast.makeText(UserProfile.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void clearUserSession() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


    private void fetchUserData(int userId) {
        // Initialize the Volley RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://mensstylehouse.com/aa/NextSteps/php/get_user_info.php";

        // Create the StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        // Parse the response into a JSON Array
                        JSONArray jsonArray = new JSONArray(response);

                        if (jsonArray.length() == 0) {
                            Toast.makeText(UserProfile.this, "No user data found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Extract user data from the first object in the array
                        JSONObject user = jsonArray.getJSONObject(0);
                        String name = user.getString("name");
                        String email = user.getString("email");

                        // Set the extracted data to the respective TextViews
                        user_name.setText(name);
                        user_email.setText(email);

                    } catch (JSONException e) {
                        Log.e("FetchUserError", "JSON parsing error: " + e.getMessage());
                        Toast.makeText(UserProfile.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("FetchUserError", "Network error: " + error.getMessage());
                    Toast.makeText(UserProfile.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add the POST parameters
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId)); // Convert int userId to String
                return params;
            }
        };

        // Set a retry policy for the request
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Maximum number of retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier
        ));

        // Add the request to the queue
        queue.add(stringRequest);
    }



}
