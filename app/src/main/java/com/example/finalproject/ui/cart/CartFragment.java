package com.example.finalproject.ui.cart;

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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.adapter.CartAdapter;
import com.example.finalproject.model.Product;
import com.example.finalproject.utils.FirebaseRepository;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView recyclerViewCart;
    private TextView textViewTotal, textViewEmptyCart;
    private Button buttonCheckout;
    private CartAdapter cartAdapter;
    private FirebaseRepository repository;
    private List<Product> cartItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        
        repository = new FirebaseRepository();
        
        recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
        textViewTotal = view.findViewById(R.id.textViewTotal);
        textViewEmptyCart = view.findViewById(R.id.textViewEmptyCart);
        buttonCheckout = view.findViewById(R.id.buttonCheckout);

        setupRecyclerView();
        loadCartFromFirebase();
        setupClickListeners();

        return view;
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(cartItems, new CartAdapter.OnCartChangeListener() {
            @Override
            public void onQuantityChanged() {
                updateTotal();
            }
            
            @Override
            public void onItemRemoved(int position) {
                if (position >= 0 && position < cartItems.size()) {
                    cartItems.remove(position);
                    cartAdapter.notifyItemRemoved(position);
                    updateTotal();
                    updateEmptyCartVisibility();
                }
            }
        });
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCart.setAdapter(cartAdapter);
    }

    private void loadCartFromFirebase() {
        repository.loadUserCart(new FirebaseRepository.DataCallback<Product>() {
            @Override
            public void onSuccess(List<Product> data) {
                if (isAdded()) {
                    cartItems.clear();
                    cartItems.addAll(data);
                    cartAdapter.notifyDataSetChanged();
                    updateTotal();
                    updateEmptyCartVisibility();
                }
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load cart", Toast.LENGTH_SHORT).show();
                    // Load fallback data for demo
                    loadFallbackCartData();
                }
            }
        });
    }

    private void loadFallbackCartData() {
        cartItems.clear();
        cartItems.add(new Product("iPhone 15 Pro", "Apple", 999.99, 4.5, R.drawable.ic_launcher_foreground));
        cartItems.add(new Product("Samsung Galaxy S24", "Samsung", 899.99, 4.3, R.drawable.ic_launcher_foreground));
        cartAdapter.notifyDataSetChanged();
        updateTotal();
        updateEmptyCartVisibility();
    }

    private void setupClickListeners() {
        buttonCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Navigate to checkout screen
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_cartFragment_to_checkoutFragment);
        });
    }

    private void updateTotal() {
        double total = 0;
        for (Product product : cartItems) {
            total += product.getPrice() * product.getQuantity(); // Now using actual quantity
        }
        textViewTotal.setText(String.format("Total: $%.2f", total));
    }

    private void updateEmptyCartVisibility() {
        if (cartItems.isEmpty()) {
            recyclerViewCart.setVisibility(View.GONE);
            textViewEmptyCart.setVisibility(View.VISIBLE);
            buttonCheckout.setEnabled(false);
            textViewTotal.setText("Total: $0.00");
        } else {
            recyclerViewCart.setVisibility(View.VISIBLE);
            textViewEmptyCart.setVisibility(View.GONE);
            buttonCheckout.setEnabled(true);
        }
    }
} 