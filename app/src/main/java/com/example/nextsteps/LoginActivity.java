package com.example.nextsteps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignUp;
    private TextView tvDontHaveAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvDontHaveAccount = findViewById(R.id.tvDontHaveAccount);

        // Check if the user is already logged in
        if (isUserLoggedIn()) {
            navigateToMainActivity();
        }

        // Handle Login button click
        btnLogin.setOnClickListener(view -> handleLogin());

        // Handle Sign Up button click
        btnSignUp.setOnClickListener(view -> navigateToSignUp());

        // Handle "Don't Have an Account" text click
        tvDontHaveAccount.setOnClickListener(view -> navigateToSignUp());
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        // Send login information
        sendLoginInformation(email, password);
    }

    private void navigateToSignUp() {
        // Navigate to SignUpActivity
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        return sharedPreferences.contains("email") && sharedPreferences.contains("password");
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendLoginInformation(String email, String password) {
        String url = "https://mensstylehouse.com/aa/NextSteps/php/login.php";
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        try {
            JSONObject requestData = new JSONObject();
            requestData.put("email", email); // Match JSON key
            requestData.put("password", password); // Match JSON key

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url, requestData,
                    response -> {
                        try {
                            // Log the raw response for debugging
                            Log.d("Response", "Server response: " + response.toString());

                            String status = response.getString("status");

                            // Check the login status
                            if ("success".equalsIgnoreCase(status)) {
                                int userId = response.getInt("user_id");

                                // Store the user_id, email, and password in SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("id", userId);
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.apply();

                                // Navigate to MainActivity
                                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                navigateToMainActivity();
                            } else {
                                // Display error message if login failed
                                String message = response.optString("message", "Unknown error");
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // Handle error in response
                            Log.e("JSONError", "Error parsing response: " + e.getMessage(), e);
                            Toast.makeText(LoginActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        // Log the error and show a toast message
                        Log.e("VolleyError", "Error: " + error.getMessage(), error);
                        Toast.makeText(LoginActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
            );

            // Add the request to the Volley queue
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            // Handle error in creating the request
            Log.e("RequestError", "Error creating request: " + e.getMessage(), e);
            Toast.makeText(LoginActivity.this, "Error creating request: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}