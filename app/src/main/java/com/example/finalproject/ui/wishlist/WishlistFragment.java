package com.example.finalproject.ui.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.adapter.WishlistAdapter;
import com.example.finalproject.model.Product;
import com.example.finalproject.utils.FirebaseRepository;
import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {
    private RecyclerView recyclerViewWishlist;
    private TextView textViewEmptyWishlist;
    private WishlistAdapter wishlistAdapter;
    private FirebaseRepository repository;
    private List<Product> wishlistItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        
        repository = new FirebaseRepository();
        
        recyclerViewWishlist = view.findViewById(R.id.recyclerViewWishlist);
        textViewEmptyWishlist = view.findViewById(R.id.textViewEmptyWishlist);

        setupRecyclerView();
        loadWishlistFromFirebase();
        
        return view;
    }

    private void setupRecyclerView() {
        wishlistAdapter = new WishlistAdapter(wishlistItems, new WishlistAdapter.OnWishlistActionListener() {
            @Override
            public void onAddToCart(Product product) {
                // TODO: Add to cart functionality
                Toast.makeText(getContext(), "Added to cart: " + product.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRemoveFromWishlist(Product product, int position) {
                if (position >= 0 && position < wishlistItems.size()) {
                    wishlistItems.remove(position);
                    wishlistAdapter.notifyItemRemoved(position);
                    updateEmptyWishlistVisibility();
                    // TODO: Remove from Firebase wishlist
                    Toast.makeText(getContext(), "Removed from wishlist: " + product.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerViewWishlist.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewWishlist.setAdapter(wishlistAdapter);
    }

    private void loadWishlistFromFirebase() {
        repository.loadUserWishlist(new FirebaseRepository.DataCallback<Product>() {
            @Override
            public void onSuccess(List<Product> data) {
                if (isAdded()) {
                    wishlistItems.clear();
                    wishlistItems.addAll(data);
                    wishlistAdapter.notifyDataSetChanged();
                    updateEmptyWishlistVisibility();
                }
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load wishlist", Toast.LENGTH_SHORT).show();
                    // Load fallback data for demo
                    loadFallbackWishlistData();
                }
            }
        });
    }

    private void loadFallbackWishlistData() {
        wishlistItems.clear();
        wishlistItems.add(new Product("iPhone 15 Pro", "Apple", 999.99, 4.5, R.drawable.ic_launcher_foreground));
        wishlistItems.add(new Product("MacBook Pro M3", "Apple", 1999.99, 4.8, R.drawable.ic_launcher_foreground));
        wishlistItems.add(new Product("AirPods Pro", "Apple", 249.99, 4.7, R.drawable.ic_launcher_foreground));
        wishlistAdapter.notifyDataSetChanged();
        updateEmptyWishlistVisibility();
    }

    private void updateEmptyWishlistVisibility() {
        if (wishlistItems.isEmpty()) {
            recyclerViewWishlist.setVisibility(View.GONE);
            textViewEmptyWishlist.setVisibility(View.VISIBLE);
        } else {
            recyclerViewWishlist.setVisibility(View.VISIBLE);
            textViewEmptyWishlist.setVisibility(View.GONE);
        }
    }
} 