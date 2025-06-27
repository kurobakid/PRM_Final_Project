package com.example.finalproject.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.utils.FirebaseAuthHelper;

public class SplashActivity extends Activity {
    private FirebaseAuthHelper authHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        authHelper = new FirebaseAuthHelper(this);

        // Delay for 2 seconds, then check authentication status
        new Handler().postDelayed(() -> {
            if (authHelper.isUserLoggedIn()) {
                // User is already logged in, go to MainActivity
                authHelper.navigateToMainActivity();
            } else {
                // User is not logged in, go to LoginActivity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
} 