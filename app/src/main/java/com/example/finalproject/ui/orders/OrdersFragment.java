package com.example.finalproject.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.adapter.OrdersAdapter;
import com.example.finalproject.model.Order;
import com.example.finalproject.utils.FirebaseRepository;
import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private RecyclerView recyclerViewOrders;
    private TextView textViewEmptyOrders;
    private OrdersAdapter ordersAdapter;
    private FirebaseRepository repository;
    private List<Order> orders = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        
        repository = new FirebaseRepository();
        
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        textViewEmptyOrders = view.findViewById(R.id.textViewEmptyOrders);

        setupRecyclerView();
        loadOrdersFromFirebase();
        
        return view;
    }

    private void setupRecyclerView() {
        ordersAdapter = new OrdersAdapter(orders, order -> {
            // TODO: Navigate to OrderDetailFragment
            Toast.makeText(getContext(), "View Order: " + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
        });
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewOrders.setAdapter(ordersAdapter);
    }

    private void loadOrdersFromFirebase() {
        repository.loadUserOrders(new FirebaseRepository.DataCallback<Order>() {
            @Override
            public void onSuccess(List<Order> data) {
                if (isAdded()) {
                    orders.clear();
                    orders.addAll(data);
                    ordersAdapter.notifyDataSetChanged();
                    updateEmptyOrdersVisibility();
                }
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                    // Load fallback data for demo
                    loadFallbackOrdersData();
                }
            }
        });
    }

    private void loadFallbackOrdersData() {
        orders.clear();
        orders.add(new Order("#1001", "Delivered", "2024-06-01", 1999.99));
        orders.add(new Order("#1002", "Shipped", "2024-06-05", 899.99));
        orders.add(new Order("#1003", "Pending", "2024-06-10", 499.99));
        ordersAdapter.notifyDataSetChanged();
        updateEmptyOrdersVisibility();
    }

    private void updateEmptyOrdersVisibility() {
        if (orders.isEmpty()) {
            recyclerViewOrders.setVisibility(View.GONE);
            textViewEmptyOrders.setVisibility(View.VISIBLE);
        } else {
            recyclerViewOrders.setVisibility(View.VISIBLE);
            textViewEmptyOrders.setVisibility(View.GONE);
        }
    }
} 