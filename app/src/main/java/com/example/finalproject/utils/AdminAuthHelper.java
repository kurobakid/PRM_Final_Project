package com.example.finalproject.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.finalproject.ui.admin.AdminActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminAuthHelper {
    private static final String TAG = "AdminAuthHelper";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Context context;

    public AdminAuthHelper(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void checkAdminRole(AdminRoleCallback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            callback.onNotAuthenticated();
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if ("admin".equals(role)) {
                            callback.onAdminAccess();
                        } else {
                            callback.onNotAdmin();
                        }
                    } else {
                        // User document doesn't exist, create it with default role
                        createUserDocument(user.getUid(), "user", callback);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError("Failed to check admin role: " + e.getMessage());
                });
    }

    private void createUserDocument(String userId, String role, AdminRoleCallback callback) {
        db.collection("users")
                .document(userId)
                .set(new UserRole(userId, role))
                .addOnSuccessListener(aVoid -> {
                    if ("admin".equals(role)) {
                        callback.onAdminAccess();
                    } else {
                        callback.onNotAdmin();
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError("Failed to create user document: " + e.getMessage());
                });
    }

    public void createAdminUser(String email, String password, String adminCode, AdminCreationCallback callback) {
        // Check if admin code is correct (you can change this to your preferred admin code)
        if (!"ADMIN123".equals(adminCode)) {
            callback.onError("Invalid admin code");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        createUserDocument(user.getUid(), "admin", new AdminRoleCallback() {
                            @Override
                            public void onAdminAccess() {
                                callback.onSuccess("Admin user created successfully");
                            }

                            @Override
                            public void onNotAdmin() {
                                callback.onError("Failed to set admin role");
                            }

                            @Override
                            public void onNotAuthenticated() {
                                callback.onError("Authentication failed");
                            }

                            @Override
                            public void onError(String error) {
                                callback.onError(error);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError("Failed to create admin user: " + e.getMessage());
                });
    }

    public void navigateToAdminPanel() {
        Intent intent = new Intent(context, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public interface AdminRoleCallback {
        void onAdminAccess();
        void onNotAdmin();
        void onNotAuthenticated();
        void onError(String error);
    }

    public interface AdminCreationCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    // User role data class
    public static class UserRole {
        private String userId;
        private String role;
        private String email;
        private long createdAt;

        public UserRole() {
            // Required for Firestore
        }

        public UserRole(String userId, String role) {
            this.userId = userId;
            this.role = role;
            this.createdAt = System.currentTimeMillis();
        }

        // Getters and setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    }
} 