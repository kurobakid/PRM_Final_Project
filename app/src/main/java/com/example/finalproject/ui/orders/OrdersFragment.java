package com.example.finalproject.ui.orders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.adapter.OrdersAdapter;
import com.example.finalproject.model.Order;
import com.example.finalproject.ui.admin.OrderDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private List<Order> orders;
    private FirebaseFirestore db;
    private String currentUserId;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orders = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ordersAdapter = new OrdersAdapter(orders, order -> {
            Intent intent = new Intent(getContext(), OrderDetailActivity.class);
            intent.putExtra("order", order);
            startActivity(intent);
        });

        recyclerView.setAdapter(ordersAdapter);

        loadOrders();

        return view;
    }

    private void loadOrders() {
        db.collection("orders")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orders.clear();
                    for (var doc : queryDocumentSnapshots) {
                        Order order = doc.toObject(Order.class);
                        order.setId(doc.getId());
                        orders.add(order);
                    }
                    // Sort orders by date descending in Java
                    orders.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                    ordersAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
