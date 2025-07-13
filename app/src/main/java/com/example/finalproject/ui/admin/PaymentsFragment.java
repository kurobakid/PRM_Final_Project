package com.example.finalproject.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.adapter.OrdersAdapter;
import com.example.finalproject.model.Order;
import com.example.finalproject.ui.admin.OrderDetailActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class PaymentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private List<Order> orderList;
    private FirebaseFirestore db;

    public PaymentsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payments, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();

        // FIXED: Pass both orderList and listener to constructor
        ordersAdapter = new OrdersAdapter(orderList, order -> {
            // Admin click order -> mở OrderDetailActivity
            Intent intent = new Intent(getContext(), OrderDetailActivity.class);
            intent.putExtra("order", order);
            startActivity(intent);
        });

        recyclerView.setAdapter(ordersAdapter);

        db = FirebaseFirestore.getInstance();

        loadOrders();

        return view;
    }

    private void loadOrders() {
        db.collection("orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    for (var doc : queryDocumentSnapshots) {
                        Order order = doc.toObject(Order.class);
                        order.setId(doc.getId());
                        orderList.add(order);
                    }
                    ordersAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Log error nếu cần
                });
    }
}
