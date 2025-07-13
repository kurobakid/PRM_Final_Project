package com.example.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.model.Order;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {
    private List<Order> orders;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrdersAdapter(List<Order> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewOrderId, textViewOrderStatus, textViewOrderDate, textViewOrderTotal;
        private Button buttonViewDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderId = itemView.findViewById(R.id.textViewOrderId);
            textViewOrderStatus = itemView.findViewById(R.id.textViewOrderStatus);
            textViewOrderDate = itemView.findViewById(R.id.textViewOrderDate);
            textViewOrderTotal = itemView.findViewById(R.id.textViewOrderTotal);
            buttonViewDetails = itemView.findViewById(R.id.buttonViewDetails);
        }

        public void bind(Order order) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            textViewOrderId.setText(order.getOrderId());
            textViewOrderStatus.setText(order.getStatus());
            textViewOrderDate.setText(sdf.format(order.getDate()));
            textViewOrderTotal.setText(String.format("$%.2f", order.getTotal()));
            buttonViewDetails.setOnClickListener(v -> {
                if (listener != null) listener.onOrderClick(order);
            });
        }
    }
} 