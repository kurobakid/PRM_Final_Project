package com.example.finalproject.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.finalproject.R;
import com.example.finalproject.ui.auth.LoginActivity;
import com.example.finalproject.utils.FirebaseAuthHelper;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private TextView textViewName, textViewEmail, textViewPhone;
    private Button buttonEditProfile, buttonAddressBook, buttonChangePassword, buttonSignOut;
    private Button buttonSettings, buttonNotifications;
    private FirebaseAuthHelper authHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        textViewName = view.findViewById(R.id.textViewProfileName);
        textViewEmail = view.findViewById(R.id.textViewProfileEmail);
        textViewPhone = view.findViewById(R.id.textViewProfilePhone);
        buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        buttonAddressBook = view.findViewById(R.id.buttonAddressBook);
        buttonChangePassword = view.findViewById(R.id.buttonChangePassword);
        buttonSignOut = view.findViewById(R.id.buttonSignOut);
        buttonSettings = view.findViewById(R.id.buttonSettings);
        buttonNotifications = view.findViewById(R.id.buttonNotifications);

        authHelper = new FirebaseAuthHelper(requireActivity());
        FirebaseUser user = authHelper.getCurrentUser();
        if (user != null) {
            textViewName.setText(user.getDisplayName() != null ? user.getDisplayName() : "No Name");
            textViewEmail.setText(user.getEmail());
            textViewPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "No Phone");
        }

        buttonEditProfile.setOnClickListener(v -> {
            // TODO: Navigate to EditProfileFragment
            Toast.makeText(getContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show();
        });
        
        buttonAddressBook.setOnClickListener(v -> {
            // Navigate to AddressBookFragment
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_profileFragment_to_addressBookFragment);
        });
        
        buttonChangePassword.setOnClickListener(v -> {
            // TODO: Navigate to ChangePasswordFragment
            Toast.makeText(getContext(), "Change Password clicked", Toast.LENGTH_SHORT).show();
        });
        
        buttonSettings.setOnClickListener(v -> {
            // Navigate to SettingsFragment
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_profileFragment_to_settingsFragment);
        });
        
        buttonNotifications.setOnClickListener(v -> {
            // Navigate to NotificationsFragment
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_profileFragment_to_notificationsFragment);
        });
        
        buttonSignOut.setOnClickListener(v -> {
            authHelper.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });
        return view;
    }
} 