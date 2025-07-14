package com.example.finalproject.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.finalproject.ui.home.AllProductsActivity;
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
    private Button viewAllButton;

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
        productRecyclerView = root.findViewById(R.id.recyclerViewProducts);
        viewAllButton = root.findViewById(R.id.buttonViewAll);
    }

    private void setupAdapters() {
        bannerAdapter = new BannerAdapter(banners);
        bannerViewPager.setAdapter(bannerAdapter);

        categoryAdapter = new CategoryAdapter(categories);
        categoryAdapter.setOnCategoryClickListener(category -> {
            Toast.makeText(getContext(), "Filter by: " + category.getName(), Toast.LENGTH_SHORT).show();
        });

        productAdapter = new ProductAdapter(products);
        productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
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
                }
            }
        });

        // Load products (limit 6)
        repository.loadProducts(new FirebaseRepository.DataCallback<Product>() {
            @Override
            public void onSuccess(List<Product> data) {
                if (isAdded()) {
                    products.clear();
                    int limit = Math.min(data.size(), 6);
                    products.addAll(data.subList(0, limit));
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupClickListeners() {
        viewAllButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AllProductsActivity.class);
            startActivity(intent);
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
}
