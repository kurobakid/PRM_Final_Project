package com.example.finalproject.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.adapter.NotificationAdapter;
import com.example.finalproject.model.Notification;
import com.example.finalproject.utils.FirebaseAuthHelper;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationsFragment extends Fragment {
    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private FirebaseFirestore db;
    private FirebaseAuthHelper authHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        
        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        
        db = FirebaseFirestore.getInstance();
        authHelper = new FirebaseAuthHelper(requireActivity());
        notificationList = new ArrayList<>();
        
        setupRecyclerView();
        loadNotifications();
        
        return view;
    }
    
    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter(notificationList, notification -> {
            // Handle notification click
            handleNotificationClick(notification);
        });
        
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewNotifications.setAdapter(notificationAdapter);
    }
    
    private void loadNotifications() {
        FirebaseUser user = authHelper.getCurrentUser();
        if (user == null) return;
        
        // For demo purposes, create some sample notifications
        // In a real app, these would come from Firebase
        createSampleNotifications();
        
        // Load from Firebase (commented out for demo)
        /*
        db.collection("notifications")
                .whereEqualTo("userId", user.getUid())
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notificationList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Notification notification = document.toObject(Notification.class);
                        if (notification != null) {
                            notification.setId(document.getId());
                            notificationList.add(notification);
                        }
                    }
                    notificationAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
                });
        */
    }
    
    private void createSampleNotifications() {
        notificationList.clear();
        
        // Order status notifications
        notificationList.add(new Notification(
            "1",
            "Order Status Update",
            "Your order #ORD-12345 has been shipped!",
            "order",
            new Date(),
            false
        ));
        
        notificationList.add(new Notification(
            "2",
            "Order Delivered",
            "Your order #ORD-12340 has been delivered successfully.",
            "order",
            new Date(System.currentTimeMillis() - 86400000), // 1 day ago
            false
        ));
        
        // Promotional notifications
        notificationList.add(new Notification(
            "3",
            "Special Offer",
            "Get 20% off on all smartphones! Limited time offer.",
            "promotion",
            new Date(System.currentTimeMillis() - 172800000), // 2 days ago
            false
        ));
        
        notificationList.add(new Notification(
            "4",
            "Flash Sale",
            "Flash sale on laptops! Up to 30% discount.",
            "promotion",
            new Date(System.currentTimeMillis() - 259200000), // 3 days ago
            false
        ));
        
        // System notifications
        notificationList.add(new Notification(
            "5",
            "Welcome to ElectroShop",
            "Thank you for joining us! Explore our latest products.",
            "system",
            new Date(System.currentTimeMillis() - 345600000), // 4 days ago
            false
        ));
        
        notificationAdapter.notifyDataSetChanged();
    }
    
    private void handleNotificationClick(Notification notification) {
        switch (notification.getType()) {
            case "order":
                // Navigate to order details
                Toast.makeText(getContext(), "Viewing order details", Toast.LENGTH_SHORT).show();
                break;
            case "promotion":
                // Navigate to promotions
                Toast.makeText(getContext(), "Viewing promotion", Toast.LENGTH_SHORT).show();
                break;
            case "system":
                // Show system message
                Toast.makeText(getContext(), notification.getMessage(), Toast.LENGTH_LONG).show();
                break;
        }
        
        // Mark as read
        markNotificationAsRead(notification);
    }
    
    private void markNotificationAsRead(Notification notification) {
        notification.setRead(true);
        notificationAdapter.notifyDataSetChanged();
        
        // Update in Firebase (commented out for demo)
        /*
        db.collection("notifications")
                .document(notification.getId())
                .update("read", true)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to mark as read", Toast.LENGTH_SHORT).show();
                });
        */
    }
} 