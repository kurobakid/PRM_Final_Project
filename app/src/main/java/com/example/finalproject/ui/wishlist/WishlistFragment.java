package com.example.finalproject.ui.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.adapter.WishlistAdapter;
import com.example.finalproject.model.Product;
import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {
    private RecyclerView recyclerViewWishlist;
    private WishlistAdapter wishlistAdapter;
    private List<Product> wishlistItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        recyclerViewWishlist = view.findViewById(R.id.recyclerViewWishlist);

        // Mock wishlist data
        wishlistItems.add(new Product("iPhone 15 Pro", "Apple", 999.99, 4.5, R.drawable.ic_launcher_foreground));
        wishlistItems.add(new Product("MacBook Pro M3", "Apple", 1999.99, 4.8, R.drawable.ic_launcher_foreground));
        wishlistItems.add(new Product("AirPods Pro", "Apple", 249.99, 4.7, R.drawable.ic_launcher_foreground));

        wishlistAdapter = new WishlistAdapter(wishlistItems, new WishlistAdapter.OnWishlistActionListener() {
            @Override
            public void onAddToCart(Product product) {
                Toast.makeText(getContext(), "Added to cart: " + product.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRemoveFromWishlist(Product product, int position) {
                wishlistItems.remove(position);
                wishlistAdapter.notifyItemRemoved(position);
                Toast.makeText(getContext(), "Removed from wishlist: " + product.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewWishlist.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewWishlist.setAdapter(wishlistAdapter);
        return view;
    }
} 