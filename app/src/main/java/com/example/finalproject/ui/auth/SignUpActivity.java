package com.example.finalproject.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.finalproject.R;
import com.example.finalproject.utils.FirebaseAuthHelper;

public class SignUpActivity extends Activity {
    private EditText fullNameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private TextView loginText;
    private FirebaseAuthHelper authHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        authHelper = new FirebaseAuthHelper(this);

        fullNameEditText = findViewById(R.id.editTextFullName);
        emailEditText = findViewById(R.id.editTextEmail);
        phoneEditText = findViewById(R.id.editTextPhone);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        signUpButton = findViewById(R.id.buttonSignUp);
        loginText = findViewById(R.id.textLogin);

        signUpButton.setOnClickListener(v -> {
            String fullName = fullNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || 
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) || 
                TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show loading state
            signUpButton.setEnabled(false);
            signUpButton.setText("Creating Account...");

            // Use Firebase Authentication
            authHelper.createUser(email, password, new FirebaseAuthHelper.AuthCallback() {
                @Override
                public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                    Toast.makeText(SignUpActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    authHelper.navigateToMainActivity();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(SignUpActivity.this, "Sign up failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    // Reset button state
                    signUpButton.setEnabled(true);
                    signUpButton.setText("Sign Up");
                }
            });
        });

        loginText.setOnClickListener(v -> {
            finish();
        });
    }
} 