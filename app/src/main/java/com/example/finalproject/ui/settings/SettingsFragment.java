package com.example.finalproject.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.example.finalproject.R;

public class SettingsFragment extends Fragment {
    private Switch switchNotifications;
    private Switch switchEmailNotifications;
    private Switch switchPushNotifications;
    private Switch switchDarkMode;
    private Switch switchLocationServices;
    private TextView textViewLanguage;
    private TextView textViewCurrency;
    private TextView textViewAbout;
    private TextView textViewPrivacyPolicy;
    private TextView textViewTermsOfService;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        
        initializeViews(view);
        loadSettings();
        setupListeners();
        
        return view;
    }
    
    private void initializeViews(View view) {
        switchNotifications = view.findViewById(R.id.switchNotifications);
        switchEmailNotifications = view.findViewById(R.id.switchEmailNotifications);
        switchPushNotifications = view.findViewById(R.id.switchPushNotifications);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        switchLocationServices = view.findViewById(R.id.switchLocationServices);
        textViewLanguage = view.findViewById(R.id.textViewLanguage);
        textViewCurrency = view.findViewById(R.id.textViewCurrency);
        textViewAbout = view.findViewById(R.id.textViewAbout);
        textViewPrivacyPolicy = view.findViewById(R.id.textViewPrivacyPolicy);
        textViewTermsOfService = view.findViewById(R.id.textViewTermsOfService);
    }
    
    private void loadSettings() {
        // Load saved preferences
        switchNotifications.setChecked(sharedPreferences.getBoolean("notifications_enabled", true));
        switchEmailNotifications.setChecked(sharedPreferences.getBoolean("email_notifications", true));
        switchPushNotifications.setChecked(sharedPreferences.getBoolean("push_notifications", true));
        switchDarkMode.setChecked(sharedPreferences.getBoolean("dark_mode", false));
        switchLocationServices.setChecked(sharedPreferences.getBoolean("location_services", false));
    }
    
    private void setupListeners() {
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            switchEmailNotifications.setEnabled(isChecked);
            switchPushNotifications.setEnabled(isChecked);
            Toast.makeText(getContext(), "Notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
        
        switchEmailNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("email_notifications", isChecked).apply();
            Toast.makeText(getContext(), "Email notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
        
        switchPushNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("push_notifications", isChecked).apply();
            Toast.makeText(getContext(), "Push notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
        
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            Toast.makeText(getContext(), "Dark mode " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
            // TODO: Apply dark mode theme
        });
        
        switchLocationServices.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("location_services", isChecked).apply();
            Toast.makeText(getContext(), "Location services " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
        
        textViewLanguage.setOnClickListener(v -> {
            // TODO: Show language selection dialog
            Toast.makeText(getContext(), "Language selection", Toast.LENGTH_SHORT).show();
        });
        
        textViewCurrency.setOnClickListener(v -> {
            // TODO: Show currency selection dialog
            Toast.makeText(getContext(), "Currency selection", Toast.LENGTH_SHORT).show();
        });
        
        textViewAbout.setOnClickListener(v -> {
            // TODO: Navigate to AboutActivity
            Toast.makeText(getContext(), "About", Toast.LENGTH_SHORT).show();
        });
        
        textViewPrivacyPolicy.setOnClickListener(v -> {
            // TODO: Navigate to PrivacyPolicyActivity
            Toast.makeText(getContext(), "Privacy Policy", Toast.LENGTH_SHORT).show();
        });
        
        textViewTermsOfService.setOnClickListener(v -> {
            // TODO: Navigate to TermsOfServiceActivity
            Toast.makeText(getContext(), "Terms of Service", Toast.LENGTH_SHORT).show();
        });
    }
} 