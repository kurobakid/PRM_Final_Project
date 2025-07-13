package com.example.finalproject.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.finalproject.R;
import com.example.finalproject.ui.auth.LoginActivity;
import com.example.finalproject.utils.FirebaseAuthHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {
    private MaterialCardView cardNotifications, cardPrivacy, cardHelp, cardAbout, cardSignOut;
    private FirebaseAuthHelper authHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        initViews(view);
        setupListeners();
        
        return view;
    }
    
    private void initViews(View view) {
        cardNotifications = view.findViewById(R.id.cardNotifications);
        cardPrivacy = view.findViewById(R.id.cardPrivacy);
        cardHelp = view.findViewById(R.id.cardHelp);
        cardAbout = view.findViewById(R.id.cardAbout);
        cardSignOut = view.findViewById(R.id.cardSignOut);
        
        authHelper = new FirebaseAuthHelper(requireActivity());
    }
    
    private void setupListeners() {
        cardNotifications.setOnClickListener(v -> {
            // Navigate to notifications settings
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_settingsFragment_to_notificationsFragment);
        });
        
        cardPrivacy.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Privacy Policy", Toast.LENGTH_SHORT).show();
            // TODO: Show privacy policy
        });
        
        cardHelp.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Help & Support", Toast.LENGTH_SHORT).show();
            // TODO: Show help section
        });
        
        cardAbout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "About App", Toast.LENGTH_SHORT).show();
            // TODO: Show about section
        });
        
        cardSignOut.setOnClickListener(v -> {
            signOut();
        });
    }
    
    private void signOut() {
        FirebaseUser user = authHelper.getCurrentUser();
        if (user != null) {
            authHelper.signOut();
            Toast.makeText(getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
            
            // Navigate to login activity
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
} 