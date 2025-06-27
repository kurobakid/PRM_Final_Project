package com.example.finalproject.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        
        initializeViews(root);
        setupBannerSlider();
        setupCategoryChips();
        setupProductGrid();
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

    private void setupBannerSlider() {
        List<Banner> banners = new ArrayList<>();
        banners.add(new Banner("Special Offer", "Get 20% off on all smartphones", R.drawable.ic_launcher_background));
        banners.add(new Banner("New Arrivals", "Latest laptops and tablets", R.drawable.ic_launcher_background));
        banners.add(new Banner("Accessories", "Premium accessories at best prices", R.drawable.ic_launcher_background));
        
        bannerAdapter = new BannerAdapter(banners);
        bannerViewPager.setAdapter(bannerAdapter);
        
        // Auto-scroll banner
        bannerViewPager.postDelayed(new Runnable() {
    @Override
            public void run() {
                int currentItem = bannerViewPager.getCurrentItem();
                if (currentItem < banners.size() - 1) {
                    bannerViewPager.setCurrentItem(currentItem + 1);
                } else {
                    bannerViewPager.setCurrentItem(0);
                }
                bannerViewPager.postDelayed(this, 3000);
            }
        }, 3000);
    }

    private void setupCategoryChips() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Phones", R.drawable.ic_launcher_foreground));
        categories.add(new Category("Laptops", R.drawable.ic_launcher_foreground));
        categories.add(new Category("Tablets", R.drawable.ic_launcher_foreground));
        categories.add(new Category("Accessories", R.drawable.ic_launcher_foreground));
        
        categoryAdapter = new CategoryAdapter(categories);
        // Note: ChipGroup setup will be handled in the adapter
    }

    private void setupProductGrid() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("iPhone 15 Pro", "Apple", 999.99, 4.5, R.drawable.ic_launcher_foreground));
        products.add(new Product("Samsung Galaxy S24", "Samsung", 899.99, 4.3, R.drawable.ic_launcher_foreground));
        products.add(new Product("MacBook Pro M3", "Apple", 1999.99, 4.8, R.drawable.ic_launcher_foreground));
        products.add(new Product("Dell XPS 13", "Dell", 1299.99, 4.4, R.drawable.ic_launcher_foreground));
        products.add(new Product("iPad Pro", "Apple", 799.99, 4.6, R.drawable.ic_launcher_foreground));
        products.add(new Product("AirPods Pro", "Apple", 249.99, 4.7, R.drawable.ic_launcher_foreground));
        
        productAdapter = new ProductAdapter(products);
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productRecyclerView.setAdapter(productAdapter);
    }

    private void setupClickListeners() {
        searchEditText.setOnClickListener(v -> {
            // TODO: Navigate to search screen
        });
        
        cartIcon.setOnClickListener(v -> {
            // TODO: Navigate to cart screen
        });
        
        profileIcon.setOnClickListener(v -> {
            // TODO: Navigate to profile screen
        });
    }
}