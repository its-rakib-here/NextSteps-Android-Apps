package com.example.nextsteps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class SignUpActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvAlreadyHaveAccount;
    private ImageView ivProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvAlreadyHaveAccount = findViewById(R.id.tvAlreadyHaveAccount);
        ivProfileImage = findViewById(R.id.ivProfileImage);

        // Set a default image in the ImageView
        ivProfileImage.setImageResource(R.drawable.appbarprofile);

        // Pick image
        ivProfileImage.setOnClickListener(view -> {
            if (checkCameraPermission()) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(intent);
            }
        });

        // Sign Up button listener
        btnSignUp.setOnClickListener(v -> handleSignUp());

        // Already have an account? Listener
        tvAlreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleSignUp() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate Inputs
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full Name is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Convert profile image to Base64
        BitmapDrawable bitmapDrawable = (BitmapDrawable) ivProfileImage.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        String image64 = Base64.encodeToString(imageByte, Base64.DEFAULT);

        // Send data to the server
        sendData(fullName, email, password, confirmPassword, image64);
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 123);
            return false;
        }
    }

    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle bundle = result.getData().getExtras();
                        if (bundle != null) {
                            Bitmap bitmap = (Bitmap) bundle.get("data");
                            ivProfileImage.setImageBitmap(bitmap);
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Image not captured", Toast.LENGTH_LONG).show();
                    }
                }
            });

    private void sendData(String name, String email, String password, String confirmPassword, String image) {
        String url = "https://mensstylehouse.com/aa/NextSteps/php/signup.php";
        RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);

        try {
            JSONObject requestData = new JSONObject();
            requestData.put("name", name);
            requestData.put("email", email);
            requestData.put("password", password);
            requestData.put("confirmPassword", confirmPassword);
            requestData.put("image", image);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url, requestData,
                    response -> {
                        Log.d("Response", response.toString());
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            if("Email already registered".equalsIgnoreCase(message))
                            {
                                Toast.makeText(SignUpActivity.this, "This email is already exist", Toast.LENGTH_SHORT).show();

                            }
                            if ("success".equalsIgnoreCase(status)) {
                                Toast.makeText(SignUpActivity.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(SignUpActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(SignUpActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show()
            );

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            Toast.makeText(SignUpActivity.this, "Error creating request: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
