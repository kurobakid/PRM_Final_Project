package com.example.finalproject.ui.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.finalproject.R;
import com.example.finalproject.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderConfirmationFragment extends Fragment {
    private TextView textViewOrderNumber, textViewOrderDate, textViewOrderStatus;
    private TextView textViewOrderTotal, textViewEstimatedDelivery;
    private Button buttonViewOrder, buttonContinueShopping;
    private FirebaseFirestore db;
    private String orderId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_confirmation, container, false);
        
        initViews(view);
        setupListeners();
        
        db = FirebaseFirestore.getInstance();
        
        // Get order ID from arguments
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
        }
        
        if (orderId != null) {
            loadOrderDetails(orderId);
        } else {
            // Show sample confirmation for demo
            showSampleConfirmation();
        }
        
        return view;
    }
    
    private void initViews(View view) {
        textViewOrderNumber = view.findViewById(R.id.textViewOrderNumber);
        textViewOrderDate = view.findViewById(R.id.textViewOrderDate);
        textViewOrderStatus = view.findViewById(R.id.textViewOrderStatus);
        textViewOrderTotal = view.findViewById(R.id.textViewOrderTotal);
        textViewEstimatedDelivery = view.findViewById(R.id.textViewEstimatedDelivery);
        buttonViewOrder = view.findViewById(R.id.buttonViewOrder);
        buttonContinueShopping = view.findViewById(R.id.buttonContinueShopping);
    }
    
    private void setupListeners() {
        buttonViewOrder.setOnClickListener(v -> {
            // Navigate to order details
            Bundle bundle = new Bundle();
            bundle.putString("orderId", orderId);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_orderConfirmationFragment_to_ordersFragment, bundle);
        });
        
        buttonContinueShopping.setOnClickListener(v -> {
            // Navigate back to home
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_orderConfirmationFragment_to_homeFragment);
        });
    }
    
    private void loadOrderDetails(String orderId) {
        db.collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Order order = documentSnapshot.toObject(Order.class);
                        if (order != null) {
                            displayOrderConfirmation(order);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Show sample confirmation on failure
                    showSampleConfirmation();
                });
    }
    
    private void showSampleConfirmation() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        
        textViewOrderNumber.setText("ORD-2024-001");
        textViewOrderDate.setText(sdf.format(new java.util.Date()));
        textViewOrderStatus.setText("Confirmed");
        textViewOrderTotal.setText("$2,067.97");
        textViewEstimatedDelivery.setText("3-5 business days");
    }
    
    private void displayOrderConfirmation(Order order) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        
        textViewOrderNumber.setText(order.getOrderNumber());
        textViewOrderDate.setText(sdf.format(order.getDate()));
        textViewOrderStatus.setText(order.getStatus());
        textViewOrderTotal.setText(String.format("$%.2f", order.getTotal()));
        textViewEstimatedDelivery.setText("3-5 business days");
    }
} 