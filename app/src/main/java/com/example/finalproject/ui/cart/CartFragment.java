package com.example.finalproject.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.adapter.CartAdapter;
import com.example.finalproject.model.Product;
import com.example.finalproject.ui.payment.ConfirmActivity;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView recyclerViewCart;
    private TextView textViewTotal;
    private Button buttonCheckout;
    private CartAdapter cartAdapter;
    private List<Product> cartItems = new ArrayList<>();

    private double totalAmount = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
        textViewTotal = view.findViewById(R.id.textViewTotal);
        buttonCheckout = view.findViewById(R.id.buttonCheckout);

        // Mock data for demonstration
        cartItems.add(new Product("iPhone 15 Pro", "Apple", 999.99, 4.5, R.drawable.ic_launcher_foreground));
        cartItems.add(new Product("Samsung Galaxy S24", "Samsung", 899.99, 4.3, R.drawable.ic_launcher_foreground));

        cartAdapter = new CartAdapter(cartItems, new CartAdapter.OnCartChangeListener() {
            @Override
            public void onQuantityChanged() {
                updateTotal();
            }
            @Override
            public void onItemRemoved(int position) {
                cartItems.remove(position);
                cartAdapter.notifyItemRemoved(position);
                updateTotal();
            }
        });
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCart.setAdapter(cartAdapter);
        updateTotal();

        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            // TODO: Navigate to confirm screen
            @Override
            public void onClick(View v) {
                if(cartItems.isEmpty()) {
                    Toast.makeText(getActivity(), "Your cart is empty. Please add items to proceed.", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed to confirm logic
                    // For now, just show a message
                    Intent intent = new Intent(getActivity(), ConfirmActivity.class);
                    intent.putExtra("total", totalAmount);
                    intent.putExtra("cartItems", new ArrayList<>(cartItems));
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    private void updateTotal() {
        double total = 0;
        for (Product product : cartItems) {
            total += product.getPrice(); // For now, quantity is always 1
        }
        totalAmount = total;
        textViewTotal.setText(String.format("Total: $%.2f", total));
    }
} 