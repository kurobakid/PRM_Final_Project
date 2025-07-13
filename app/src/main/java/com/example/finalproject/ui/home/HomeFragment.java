package com.example.finalproject.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.finalproject.R;
import com.example.finalproject.adapter.BannerAdapter;
import com.example.finalproject.adapter.CategoryAdapter;
import com.example.finalproject.adapter.ProductAdapter;
import com.example.finalproject.model.Banner;
import com.example.finalproject.model.Category;
import com.example.finalproject.model.Product;
import com.example.finalproject.utils.FirebaseRepository;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private EditText searchEditText;
    private ViewPager2 bannerViewPager;
    private ChipGroup categoryChipGroup;
    private RecyclerView productRecyclerView;
    private ImageView cartIcon, profileIcon;
    
    private BannerAdapter bannerAdapter;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    
    private FirebaseRepository repository;
    private List<Banner> banners = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<Product> products = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        
        repository = new FirebaseRepository();
        
        initializeViews(root);
        setupAdapters();
        loadDataFromFirebase();
        setupClickListeners();
        
        return root;
    }

    private void initializeViews(View root) {
        searchEditText = root.findViewById(R.id.editTextSearch);
        bannerViewPager = root.findViewById(R.id.viewPagerBanner);
        categoryChipGroup = root.findViewById(R.id.chipGroupCategories);
        productRecyclerView = root.findViewById(R.id.recyclerViewProducts);
        cartIcon = root.findViewById(R.id.imageViewCart);
        profileIcon = root.findViewById(R.id.imageViewProfile);
    }

    private void setupAdapters() {
        // Initialize adapters with empty data
        bannerAdapter = new BannerAdapter(banners);
        bannerViewPager.setAdapter(bannerAdapter);

        categoryAdapter = new CategoryAdapter(categories);
        categoryAdapter.setOnCategoryClickListener(category -> {
            // TODO: Filter products by category
            Toast.makeText(getContext(), "Filter by: " + category.getName(), Toast.LENGTH_SHORT).show();
        });

        productAdapter = new ProductAdapter(products);
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
        
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productRecyclerView.setAdapter(productAdapter);
    }

    private void loadDataFromFirebase() {
        // Load banners
        repository.loadBanners(new FirebaseRepository.DataCallback<Banner>() {
            @Override
            public void onSuccess(List<Banner> data) {
                if (isAdded()) {
                    banners.clear();
                    banners.addAll(data);
                    bannerAdapter.notifyDataSetChanged();
                    setupBannerAutoScroll();
                }
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load banners", Toast.LENGTH_SHORT).show();
                    // Load fallback data
                    loadFallbackBanners();
                }
            }
        });

        // Load categories
        repository.loadCategories(new FirebaseRepository.DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
                if (isAdded()) {
                    categories.clear();
                    categories.addAll(data);
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                    // Load fallback data
                    loadFallbackCategories();
                }
            }
        });

        // Load products
        repository.loadProducts(new FirebaseRepository.DataCallback<Product>() {
            @Override
            public void onSuccess(List<Product> data) {
                if (isAdded()) {
                    products.clear();
                    products.addAll(data);
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                    // Load fallback data
                    loadFallbackProducts();
                }
            }
        });
    }

    private void setupBannerAutoScroll() {
        if (banners.size() > 1) {
            bannerViewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isAdded() && banners.size() > 0) {
                        int currentItem = bannerViewPager.getCurrentItem();
                        if (currentItem < banners.size() - 1) {
                            bannerViewPager.setCurrentItem(currentItem + 1);
                        } else {
                            bannerViewPager.setCurrentItem(0);
                        }
                        bannerViewPager.postDelayed(this, 3000);
                    }
                }
            }, 3000);
        }
    }

    // Fallback methods for when Firebase data is not available
    private void loadFallbackBanners() {
        banners.clear();
        banners.add(new Banner("Special Offer", "Get 20% off on all smartphones", R.drawable.ic_launcher_background));
        banners.add(new Banner("New Arrivals", "Latest laptops and tablets", R.drawable.ic_launcher_background));
        banners.add(new Banner("Accessories", "Premium accessories at best prices", R.drawable.ic_launcher_background));
        bannerAdapter.notifyDataSetChanged();
        setupBannerAutoScroll();
    }

    private void loadFallbackCategories() {
        categories.clear();
        categories.add(new Category("Phones", R.drawable.ic_launcher_foreground));
        categories.add(new Category("Laptops", R.drawable.ic_launcher_foreground));
        categories.add(new Category("Tablets", R.drawable.ic_launcher_foreground));
        categories.add(new Category("Accessories", R.drawable.ic_launcher_foreground));
        categoryAdapter.notifyDataSetChanged();
    }

    private void loadFallbackProducts() {
        products.clear();
        products.add(new Product("iPhone 15 Pro", "Apple", 999.99, 4.5, R.drawable.ic_launcher_foreground));
        products.add(new Product("Samsung Galaxy S24", "Samsung", 899.99, 4.3, R.drawable.ic_launcher_foreground));
        products.add(new Product("MacBook Pro M3", "Apple", 1999.99, 4.8, R.drawable.ic_launcher_foreground));
        products.add(new Product("Dell XPS 13", "Dell", 1299.99, 4.4, R.drawable.ic_launcher_foreground));
        products.add(new Product("iPad Pro", "Apple", 799.99, 4.6, R.drawable.ic_launcher_foreground));
        products.add(new Product("AirPods Pro", "Apple", 249.99, 4.7, R.drawable.ic_launcher_foreground));
        productAdapter.notifyDataSetChanged();
    }

    private void setupClickListeners() {
        searchEditText.setOnClickListener(v -> {
            // Navigate to search screen
            androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_searchFragment);
        });
        
        cartIcon.setOnClickListener(v -> {
            // TODO: Navigate to cart screen
        });
        
        profileIcon.setOnClickListener(v -> {
            // TODO: Navigate to profile screen
        });
    }
}