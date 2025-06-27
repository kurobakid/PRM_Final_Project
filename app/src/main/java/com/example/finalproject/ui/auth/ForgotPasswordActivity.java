package com.example.finalproject.ui.auth;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.finalproject.R;
import com.example.finalproject.utils.FirebaseAuthHelper;

public class ForgotPasswordActivity extends Activity {
    private EditText emailPhoneEditText;
    private Button resetButton;
    private TextView loginText;
    private FirebaseAuthHelper authHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authHelper = new FirebaseAuthHelper(this);

        emailPhoneEditText = findViewById(R.id.editTextEmailPhone);
        resetButton = findViewById(R.id.buttonReset);
        loginText = findViewById(R.id.textLogin);

        resetButton.setOnClickListener(v -> {
            String emailPhone = emailPhoneEditText.getText().toString().trim();
            
            if (TextUtils.isEmpty(emailPhone)) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if it's a valid email
            if (!Patterns.EMAIL_ADDRESS.matcher(emailPhone).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show loading state
            resetButton.setEnabled(false);
            resetButton.setText("Sending...");

            // Use Firebase Authentication
            authHelper.resetPassword(emailPhone, new FirebaseAuthHelper.AuthCallback() {
                @Override
                public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                    Toast.makeText(ForgotPasswordActivity.this, 
                        "Password reset email sent! Check your inbox.", Toast.LENGTH_LONG).show();
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(ForgotPasswordActivity.this, 
                        "Reset failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    // Reset button state
                    resetButton.setEnabled(true);
                    resetButton.setText("Reset Password");
                }
            });
        });

        loginText.setOnClickListener(v -> {
            finish();
        });
    }
} 