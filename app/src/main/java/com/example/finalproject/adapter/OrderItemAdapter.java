// File: app/src/main/java/com/example/finalproject/adapter/OrderItemAdapter.java
package com.example.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalproject.R;
import com.example.finalproject.model.Product;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private final List<Product> orderItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public OrderItemAdapter(List<Product> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new OrderItemViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        holder.bind(orderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewProduct;
        private final TextView textViewName, textViewBrand, textViewPrice, textViewQuantity;

        public OrderItemViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewCartProduct);
            textViewName = itemView.findViewById(R.id.textViewCartProductName);
            textViewBrand = itemView.findViewById(R.id.textViewCartProductBrand);
            textViewPrice = itemView.findViewById(R.id.textViewCartProductPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewCartProductQuantity);

            itemView.findViewById(R.id.buttonIncrease).setVisibility(View.GONE);
            itemView.findViewById(R.id.buttonDecrease).setVisibility(View.GONE);
            itemView.findViewById(R.id.buttonRemove).setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                if (listener != null && getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick((Product) v.getTag());
                }
            });
        }

        public void bind(Product product) {
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.address_selection_background)
                    .into(imageViewProduct);

            textViewName.setText(product.getName());
            textViewBrand.setText(product.getBrand());
            textViewPrice.setText(product.getFormattedPrice());
            textViewQuantity.setText(String.valueOf(product.getQuantity()));
            itemView.setTag(product);
        }
    }
}