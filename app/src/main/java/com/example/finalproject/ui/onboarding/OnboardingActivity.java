package com.example.finalproject.ui.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.finalproject.R;
import com.example.finalproject.ui.auth.LoginActivity;
import com.example.finalproject.adapter.OnboardingAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Button buttonSkip;
    private Button buttonNext;
    private Button buttonGetStarted;
    private OnboardingAdapter onboardingAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        
        // Check if user has already seen onboarding
        if (sharedPreferences.getBoolean("onboarding_completed", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        initializeViews();
        setupOnboarding();
    }
    
    private void initializeViews() {
        viewPager = findViewById(R.id.viewPagerOnboarding);
        tabLayout = findViewById(R.id.tabLayoutOnboarding);
        buttonSkip = findViewById(R.id.buttonSkip);
        buttonNext = findViewById(R.id.buttonNext);
        buttonGetStarted = findViewById(R.id.buttonGetStarted);
    }
    
    private void setupOnboarding() {
        onboardingAdapter = new OnboardingAdapter();
        viewPager.setAdapter(onboardingAdapter);
        
        // Connect ViewPager with TabLayout
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Tab configuration if needed
        }).attach();
        
        // Set up button listeners
        buttonSkip.setOnClickListener(v -> {
            completeOnboarding();
        });
        
        buttonNext.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < onboardingAdapter.getItemCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1);
            }
        });
        
        buttonGetStarted.setOnClickListener(v -> {
            completeOnboarding();
        });
        
        // Update button visibility based on current page
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateButtonVisibility(position);
            }
        });
        
        // Set initial button visibility
        updateButtonVisibility(0);
    }
    
    private void updateButtonVisibility(int position) {
        if (position == onboardingAdapter.getItemCount() - 1) {
            // Last page
            buttonSkip.setVisibility(View.GONE);
            buttonNext.setVisibility(View.GONE);
            buttonGetStarted.setVisibility(View.VISIBLE);
        } else {
            // Not last page
            buttonSkip.setVisibility(View.VISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
            buttonGetStarted.setVisibility(View.GONE);
        }
    }
    
    private void completeOnboarding() {
        // Mark onboarding as completed
        sharedPreferences.edit().putBoolean("onboarding_completed", true).apply();
        
        // Navigate to login
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
} 