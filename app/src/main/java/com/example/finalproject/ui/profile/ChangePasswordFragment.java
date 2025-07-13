package com.example.finalproject.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.finalproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordFragment extends Fragment {
    private EditText editTextNewPassword;
    private MaterialButton buttonChangePassword;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        buttonChangePassword = view.findViewById(R.id.buttonChangePassword);
        mAuth = FirebaseAuth.getInstance();

        buttonChangePassword.setOnClickListener(v -> {
            String newPassword = editTextNewPassword.getText().toString().trim();

            if (TextUtils.isEmpty(newPassword)) {
                editTextNewPassword.setError("Password required");
                return;
            }

            mAuth.getCurrentUser().updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to change password: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        return view;
    }
}
