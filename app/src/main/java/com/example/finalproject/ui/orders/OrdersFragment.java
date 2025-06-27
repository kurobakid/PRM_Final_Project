package com.example.finalproject.ui.orders;

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
import com.example.finalproject.adapter.OrdersAdapter;
import com.example.finalproject.model.Order;
import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private RecyclerView recyclerViewOrders;
    private OrdersAdapter ordersAdapter;
    private List<Order> orders = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);

        // Mock data
        orders.add(new Order("#1001", "Delivered", "2024-06-01", 1999.99));
        orders.add(new Order("#1002", "Shipped", "2024-06-05", 899.99));
        orders.add(new Order("#1003", "Pending", "2024-06-10", 499.99));

        ordersAdapter = new OrdersAdapter(orders, order -> {
            // TODO: Navigate to OrderDetailFragment
            Toast.makeText(getContext(), "View Order: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
        });
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewOrders.setAdapter(ordersAdapter);
        return view;
    }
} 