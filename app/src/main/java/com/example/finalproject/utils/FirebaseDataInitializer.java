package com.example.finalproject.utils;

import android.util.Log;
import com.example.finalproject.model.Category;
import com.example.finalproject.model.Product;
import com.example.finalproject.model.Banner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDataInitializer {
    private static final String TAG = "FirebaseDataInitializer";
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public FirebaseDataInitializer() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public void initializeSampleData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Cannot initialize data: User not authenticated");
            return;
        }
        
        Log.d(TAG, "Initializing sample data for authenticated user: " + currentUser.getEmail());
        initializeCategories();
        initializeProducts();
        initializeBanners();
    }

    public void initializeSampleDataIfNeeded() {
        // Check if data already exists before initializing
        db.collection("categories")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d(TAG, "No categories found, initializing sample data");
                        initializeSampleData();
                    } else {
                        Log.d(TAG, "Sample data already exists, skipping initialization");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error checking existing data", e);
                });
    }

    private void initializeCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        
        String[] categoryNames = {"Phones", "Laptops", "Tablets", "Accessories", "Smartwatches", "Headphones"};
        
        for (String name : categoryNames) {
            Map<String, Object> category = new HashMap<>();
            category.put("name", name);
            category.put("iconResource", "ic_launcher_foreground"); // Default icon
            category.put("createdBy", auth.getCurrentUser().getUid());
            category.put("createdAt", System.currentTimeMillis());
            categories.add(category);
        }

        for (Map<String, Object> category : categories) {
            db.collection("categories")
                    .add(category)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Category added with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding category", e);
                    });
        }
    }

    private void initializeProducts() {
        List<Map<String, Object>> products = new ArrayList<>();
        
        // Sample products
        addProduct(products, "iPhone 15 Pro", "Apple", 999.99, 4.5, "phones");
        addProduct(products, "Samsung Galaxy S24", "Samsung", 899.99, 4.3, "phones");
        addProduct(products, "MacBook Pro M3", "Apple", 1999.99, 4.8, "laptops");
        addProduct(products, "Dell XPS 13", "Dell", 1299.99, 4.4, "laptops");
        addProduct(products, "iPad Pro", "Apple", 799.99, 4.6, "tablets");
        addProduct(products, "AirPods Pro", "Apple", 249.99, 4.7, "accessories");
        addProduct(products, "Sony WH-1000XM5", "Sony", 349.99, 4.9, "headphones");
        addProduct(products, "Apple Watch Series 9", "Apple", 399.99, 4.6, "smartwatches");

        for (Map<String, Object> product : products) {
            db.collection("products")
                    .add(product)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Product added with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding product", e);
                    });
        }
    }

    private void addProduct(List<Map<String, Object>> products, String name, String brand, 
                          double price, double rating, String category) {
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("brand", brand);
        product.put("price", price);
        product.put("rating", rating);
        product.put("category", category);
        product.put("imageUrl", "https://example.com/" + name.toLowerCase().replace(" ", "_") + ".jpg");
        product.put("description", "High-quality " + name + " from " + brand + ".");
        product.put("inStock", true);
        product.put("quantity", 100);
        product.put("createdBy", auth.getCurrentUser().getUid());
        product.put("createdAt", System.currentTimeMillis());
        products.add(product);
    }

    private void initializeBanners() {
        List<Map<String, Object>> banners = new ArrayList<>();
        
        addBanner(banners, "Special Offer", "Get 20% off on all smartphones", "banner1.jpg");
        addBanner(banners, "New Arrivals", "Latest laptops and tablets", "banner2.jpg");
        addBanner(banners, "Accessories", "Premium accessories at best prices", "banner3.jpg");

        for (Map<String, Object> banner : banners) {
            db.collection("banners")
                    .add(banner)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Banner added with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding banner", e);
                    });
        }
    }

    private void addBanner(List<Map<String, Object>> banners, String title, 
                          String description, String imageUrl) {
        Map<String, Object> banner = new HashMap<>();
        banner.put("title", title);
        banner.put("description", description);
        banner.put("imageUrl", imageUrl);
        banner.put("active", true);
        banner.put("createdBy", auth.getCurrentUser().getUid());
        banner.put("createdAt", System.currentTimeMillis());
        banners.add(banner);
    }

    public void clearAllData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Cannot clear data: User not authenticated");
            return;
        }
        
        // Clear all collections (use with caution!)
        String[] collections = {"categories", "products", "banners", "orders", "addresses", "notifications"};
        
        for (String collection : collections) {
            db.collection(collection)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (var document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                        Log.d(TAG, "Cleared collection: " + collection);
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error clearing collection: " + collection, e);
                    });
        }
    }
} 