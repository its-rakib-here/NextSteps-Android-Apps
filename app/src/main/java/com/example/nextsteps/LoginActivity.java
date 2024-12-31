package com.example.nextsteps;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {


    private EditText etEmail, etPassword;
    private Button   btnLogin, btnSignUp;
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

        // Perform login logic (e.g., authenticate with server or database)
       // if (email.equals("test@example.com") && password.equals("password")) {
            // Simulate successful login
            sendLoginInformation(email,password);


//            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
//            // Navigate to another activity after successful login (e.g., MainActivity)
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish(); // Close the login activity
        //}
    }

    private void navigateToSignUp() {
        // Navigate to SignUpActivity
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void sendLoginInformation(String etEmail, String etPassword) {
        String url = "https://mensstylehouse.com/aa/NextSteps/php/login.php";
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        try {
            JSONObject requestData = new JSONObject();
            requestData.put("email", etEmail); // Match JSON key
            requestData.put("password", etPassword); // Match JSON key

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url, requestData,
                    response -> {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            if ("success".equalsIgnoreCase(status)) {
                                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(LoginActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show()
            );

            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            Toast.makeText(LoginActivity.this, "Error creating request: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
