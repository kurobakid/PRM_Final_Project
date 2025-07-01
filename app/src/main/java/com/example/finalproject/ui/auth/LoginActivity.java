package com.example.finalproject.ui.auth;

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
import com.example.finalproject.ui.admin.AdminLoginActivity;
import com.example.finalproject.utils.FirebaseAuthHelper;

public class LoginActivity extends Activity {
    private EditText emailPhoneEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpText, forgotPasswordText, adminLoginText;
    private FirebaseAuthHelper authHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authHelper = new FirebaseAuthHelper(this);

        emailPhoneEditText = findViewById(R.id.editTextEmailPhone);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        signUpText = findViewById(R.id.textSignUp);
        forgotPasswordText = findViewById(R.id.textForgotPassword);
        adminLoginText = findViewById(R.id.textAdminLogin);

        loginButton.setOnClickListener(v -> {
            String emailPhone = emailPhoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            
            if (TextUtils.isEmpty(emailPhone) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show loading state
            loginButton.setEnabled(false);
            loginButton.setText("Logging in...");

            // Use Firebase Authentication
            authHelper.signIn(emailPhone, password, new FirebaseAuthHelper.AuthCallback() {
                @Override
                public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    authHelper.navigateToMainActivity();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(LoginActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    // Reset button state
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            });
        });

        signUpText.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

        forgotPasswordText.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        adminLoginText.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminLoginActivity.class));
        });
    }
} 