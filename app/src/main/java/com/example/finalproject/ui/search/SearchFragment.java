package com.example.finalproject.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.adapter.ProductAdapter;
import com.example.finalproject.model.Product;
import com.example.finalproject.utils.FirebaseRepository;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private EditText editTextSearch;
    private RecyclerView recyclerViewSearchResults;
    private ProductAdapter productAdapter;
    private FirebaseRepository repository;
    private List<Product> searchResults = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        
        repository = new FirebaseRepository();
        
        editTextSearch = view.findViewById(R.id.editTextSearch);
        recyclerViewSearchResults = view.findViewById(R.id.recyclerViewSearchResults);
        
        setupRecyclerView();
        setupSearchListener();
        
        return view;
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(searchResults);
        productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                // TODO: Navigate to product detail
                Toast.makeText(getContext(), "View: " + product.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToCartClick(Product product) {
                repository.addToCart(product, new FirebaseRepository.DataCallback<Void>() {
                    @Override
                    public void onSuccess(List<Void> data) {
                        Toast.makeText(getContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onWishlistClick(Product product) {
                // TODO: Add to wishlist
                Toast.makeText(getContext(), "Added to wishlist: " + product.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        
        recyclerViewSearchResults.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewSearchResults.setAdapter(productAdapter);
    }
    
    private void setupSearchListener() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.length() >= 2) { // Start searching after 2 characters
                    performSearch(query);
                } else {
                    // Clear results if query is too short
                    searchResults.clear();
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void performSearch(String query) {
        repository.searchProducts(query, new FirebaseRepository.DataCallback<Product>() {
            @Override
            public void onSuccess(List<Product> data) {
                if (isAdded()) {
                    searchResults.clear();
                    searchResults.addAll(data);
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Search failed: " + error, Toast.LENGTH_SHORT).show();
                    // Show fallback results
                    loadFallbackResults(query);
                }
            }
        });
    }

    private void loadFallbackResults(String query) {
        // Fallback hardcoded data for when Firebase search fails
        List<Product> fallbackProducts = new ArrayList<>();
        fallbackProducts.add(new Product("iPhone 15 Pro", "Apple", 999.99, 4.5, R.drawable.ic_launcher_foreground));
        fallbackProducts.add(new Product("Samsung Galaxy S24", "Samsung", 899.99, 4.3, R.drawable.ic_launcher_foreground));
        fallbackProducts.add(new Product("MacBook Pro M3", "Apple", 1999.99, 4.8, R.drawable.ic_launcher_foreground));
        fallbackProducts.add(new Product("Dell XPS 13", "Dell", 1299.99, 4.4, R.drawable.ic_launcher_foreground));
        fallbackProducts.add(new Product("iPad Pro", "Apple", 799.99, 4.6, R.drawable.ic_launcher_foreground));
        fallbackProducts.add(new Product("AirPods Pro", "Apple", 249.99, 4.7, R.drawable.ic_launcher_foreground));
        fallbackProducts.add(new Product("Sony WH-1000XM5", "Sony", 349.99, 4.9, R.drawable.ic_launcher_foreground));
        fallbackProducts.add(new Product("Apple Watch Series 9", "Apple", 399.99, 4.6, R.drawable.ic_launcher_foreground));
        
        // Filter fallback results
        searchResults.clear();
        String lowerQuery = query.toLowerCase();
        for (Product product : fallbackProducts) {
            if (product.getName().toLowerCase().contains(lowerQuery) || 
                product.getBrand().toLowerCase().contains(lowerQuery)) {
                searchResults.add(product);
            }
        }
        productAdapter.notifyDataSetChanged();
    }
} 