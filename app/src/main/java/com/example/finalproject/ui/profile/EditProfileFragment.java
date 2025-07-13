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
import com.example.finalproject.model.User;
import com.example.finalproject.utils.FirebaseAuthHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileFragment extends Fragment {
    private EditText editTextEmail, editTextName, editTextPhone;
    private MaterialButton buttonSaveProfile;
    private FirebaseAuthHelper authHelper;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextName = view.findViewById(R.id.editTextName);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        buttonSaveProfile = view.findViewById(R.id.buttonSaveProfile);

        authHelper = new FirebaseAuthHelper(requireActivity());
        FirebaseUser user = authHelper.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user != null) {
            String uid = user.getUid();

            // Hiển thị email từ FirebaseAuth
            editTextEmail.setText(user.getEmail());

            // Load user data từ Firestore
            db.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User u = documentSnapshot.toObject(User.class);
                            if (u != null) {
                                editTextName.setText(u.getName());
                                editTextPhone.setText(u.getPhone());
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                    });
        }

        buttonSaveProfile.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                editTextName.setError("Name required");
                return;
            }

            if (user != null) {
                String uid = user.getUid();
                String email = user.getEmail();

                User updatedUser = new User(uid, name, email, phone);

                db.collection("users").document(uid)
                        .set(updatedUser)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        return view;
    }
}
