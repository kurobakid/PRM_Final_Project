package com.example.finalproject.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.adapter.ProductAdapter;
import com.example.finalproject.model.Product;
import com.example.finalproject.utils.FirebaseRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class AllProductsActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ChipGroup categoryChipGroup;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private FirebaseRepository repository;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> displayedProducts = new ArrayList<>();
    private int pageSize = 10;
    private int currentPage = 0;
    private boolean isLoading = false;
    private String selectedCategory = "all";
    private String currentQuery = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        repository = new FirebaseRepository();

        searchEditText = findViewById(R.id.editTextSearchAllProducts);
        categoryChipGroup = findViewById(R.id.chipGroupCategoriesAllProducts);
        recyclerView = findViewById(R.id.recyclerViewAllProducts);

        productAdapter = new ProductAdapter(displayedProducts);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(productAdapter);

        loadAllProducts();
        setupListeners();
        setupPagination();
    }

    private void loadAllProducts() {
        repository.loadProducts(new FirebaseRepository.DataCallback<Product>() {
            @Override
            public void onSuccess(List<Product> data) {
                allProducts.clear();
                allProducts.addAll(data);
                loadCategoriesFromProducts(data);
                applyFiltersAndPagination();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AllProductsActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategoriesFromProducts(List<Product> products) {
        categoryChipGroup.removeAllViews();
        Chip allChip = new Chip(this);
        allChip.setText("All");
        allChip.setCheckable(true);
        allChip.setChecked(true);
        allChip.setOnClickListener(v -> {
            selectedCategory = "all";
            applyFiltersAndPagination();
        });
        categoryChipGroup.addView(allChip);

        List<String> categories = new ArrayList<>();
        for (Product p : products) {
            if (p.getBrand() != null && !categories.contains(p.getBrand())) {
                categories.add(p.getBrand());
            }
        }

        for (String category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                selectedCategory = category;
                applyFiltersAndPagination();
            });
            categoryChipGroup.addView(chip);
        }
    }

    private void setupListeners() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                currentQuery = s.toString().toLowerCase();
                applyFiltersAndPagination();
            }
        });
    }

    private void applyFiltersAndPagination() {
        currentPage = 0;
        displayedProducts.clear();

        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            boolean matchesCategory = selectedCategory.equals("all") || p.getBrand().equalsIgnoreCase(selectedCategory);
            boolean matchesSearch = p.getName().toLowerCase().contains(currentQuery)
                    || (p.getDescription() != null && p.getDescription().toLowerCase().contains(currentQuery));
            if (matchesCategory && matchesSearch) {
                filtered.add(p);
            }
        }

        loadNextPage(filtered);
    }

    private void loadNextPage(List<Product> filteredProducts) {
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, filteredProducts.size());

        if (start < end) {
            displayedProducts.addAll(filteredProducts.subList(start, end));
            productAdapter.notifyDataSetChanged();
            currentPage++;
        }
        isLoading = false;
    }

    private void setupPagination() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager) rv.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        isLoading = true;
                        applyFiltersAndPagination();
                    }
                }
            }
        });
    }
}
