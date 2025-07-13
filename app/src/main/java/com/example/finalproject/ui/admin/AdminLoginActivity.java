package com.example.finalproject.ui.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.finalproject.R;
import com.example.finalproject.ui.auth.LoginActivity;
import com.example.finalproject.utils.AdminAuthHelper;
import com.example.finalproject.utils.FirebaseAuthHelper;

public class AdminLoginActivity extends Activity {
    private EditText emailEditText, passwordEditText, adminCodeEditText;
    private Button loginButton, createAdminButton;
    private TextView backToLoginText;
    private AdminAuthHelper adminAuthHelper;
    private FirebaseAuthHelper authHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        adminAuthHelper = new AdminAuthHelper(this);
        authHelper = new FirebaseAuthHelper(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.editTextAdminEmail);
        passwordEditText = findViewById(R.id.editTextAdminPassword);
        adminCodeEditText = findViewById(R.id.editTextAdminCode);
        loginButton = findViewById(R.id.buttonAdminLogin);
        createAdminButton = findViewById(R.id.buttonCreateAdmin);
        backToLoginText = findViewById(R.id.textBackToLogin);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> {
            loginAsAdmin();
        });

        createAdminButton.setOnClickListener(v -> {
            createAdminUser();
        });

        backToLoginText.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loginAsAdmin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // First authenticate with Firebase
        authHelper.signIn(email, password, new FirebaseAuthHelper.AuthCallback() {
            @Override
            public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                // Then check if user is admin
                adminAuthHelper.checkAdminRole(new AdminAuthHelper.AdminRoleCallback() {
                    @Override
                    public void onAdminAccess() {
                        Toast.makeText(AdminLoginActivity.this, "Admin login successful!", Toast.LENGTH_SHORT).show();
                        adminAuthHelper.navigateToAdminPanel();
                        finish();
                    }

                    @Override
                    public void onNotAdmin() {
                        Toast.makeText(AdminLoginActivity.this, "Access denied. Admin privileges required.", Toast.LENGTH_LONG).show();
                        authHelper.signOut();
                        resetLoginButton();
                    }

                    @Override
                    public void onNotAuthenticated() {
                        Toast.makeText(AdminLoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        resetLoginButton();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(AdminLoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        resetLoginButton();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AdminLoginActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                resetLoginButton();
            }
        });
    }

    private void createAdminUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String adminCode = adminCodeEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(adminCode)) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        createAdminButton.setEnabled(false);
        createAdminButton.setText("Creating...");

        adminAuthHelper.createAdminUser(email, password, adminCode, new AdminAuthHelper.AdminCreationCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(AdminLoginActivity.this, message, Toast.LENGTH_LONG).show();
                resetCreateButton();
                // Auto-login after creation
                loginAsAdmin();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminLoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                resetCreateButton();
            }
        });
    }

    private void resetLoginButton() {
        loginButton.setEnabled(true);
        loginButton.setText("Login as Admin");
    }

    private void resetCreateButton() {
        createAdminButton.setEnabled(true);
        createAdminButton.setText("Create Admin Account");
    }
} 