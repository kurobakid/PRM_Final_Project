package com.example.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.model.Product;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Product> cartItems;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onQuantityChanged(int position, Product product, int newQuantity);
        void onItemRemoved(int position, Product product);
    }

    public CartAdapter(List<Product> cartItems, OnCartChangeListener listener) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        if (cartItems != null && position < cartItems.size()) {
            holder.bind(cartItems.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewProduct;
        private TextView textViewName, textViewBrand, textViewPrice, textViewQuantity;
        private Button buttonIncrease, buttonDecrease, buttonRemove;
        private int quantity = 1;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewCartProduct);
            textViewName = itemView.findViewById(R.id.textViewCartProductName);
            textViewBrand = itemView.findViewById(R.id.textViewCartProductBrand);
            textViewPrice = itemView.findViewById(R.id.textViewCartProductPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewCartProductQuantity);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }

        public void bind(Product product, int position) {
            imageViewProduct.setImageResource(product.getImageResource());
            textViewName.setText(product.getName());
            textViewBrand.setText(product.getBrand());
            textViewPrice.setText(product.getFormattedPrice());
            quantity = product.getQuantity();
            textViewQuantity.setText(String.valueOf(quantity));

            buttonIncrease.setOnClickListener(v -> {
                quantity++;
                textViewQuantity.setText(String.valueOf(quantity));
                product.setQuantity(quantity);
                if (listener != null) {
                    listener.onQuantityChanged(position, product, quantity);
                }
            });

            buttonDecrease.setOnClickListener(v -> {
                if (quantity > 1) {
                    quantity--;
                    textViewQuantity.setText(String.valueOf(quantity));
                    product.setQuantity(quantity);
                    if (listener != null) {
                        listener.onQuantityChanged(position, product, quantity);
                    }
                }
            });

            buttonRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemRemoved(position, product);
                }
            });
        }
    }
} 