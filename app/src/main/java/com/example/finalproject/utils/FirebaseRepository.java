package com.example.finalproject.utils;

import android.util.Log;
import com.example.finalproject.model.Banner;
import com.example.finalproject.model.Category;
import com.example.finalproject.model.Product;
import com.example.finalproject.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FirebaseRepository {
    private static final String TAG = "FirebaseRepository";
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public FirebaseRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    // Data loading interfaces
    public interface DataCallback<T> {
        void onSuccess(List<T> data);
        void onFailure(String error);
    }

    public interface SingleDataCallback<T> {
        void onSuccess(T data);
        void onFailure(String error);
    }

    // Categories
    public void loadCategories(DataCallback<Category> callback) {
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Category> categories = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Category category = new Category();
                            category.setId(document.getId());
                            category.setName(document.getString("name"));
                            // For now, use default icon
                            category.setIconResource(android.R.drawable.ic_menu_gallery);
                            categories.add(category);
                        } catch (Exception e) {
                            Log.w(TAG, "Error parsing category document: " + document.getId(), e);
                        }
                    }
                    callback.onSuccess(categories);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error loading categories", e);
                    callback.onFailure("Failed to load categories: " + e.getMessage());
                });
    }

    // Products
    public void loadProducts(DataCallback<Product> callback) {
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Product product = document.toObject(Product.class);
                            product.setId(document.getId());
                            // Set default image if not present
                            if (product.getImageResource() == 0) {
                                product.setImageResource(android.R.drawable.ic_menu_gallery);
                            }
                            products.add(product);
                        } catch (Exception e) {
                            Log.w(TAG, "Error parsing product document: " + document.getId(), e);
                        }
                    }
                    callback.onSuccess(products);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error loading products", e);
                    callback.onFailure("Failed to load products: " + e.getMessage());
                });
    }

    // Products by category
    public void loadProductsByCategory(String category, DataCallback<Product> callback) {
        db.collection("products")
                .whereEqualTo("category", category.toLowerCase())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Product product = document.toObject(Product.class);
                            product.setId(document.getId());
                            products.add(product);
                        } catch (Exception e) {
                            Log.w(TAG, "Error parsing product document: " + document.getId(), e);
                        }
                    }
                    callback.onSuccess(products);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error loading products by category", e);
                    callback.onFailure("Failed to load products: " + e.getMessage());
                });
    }

    // Single product
    public void loadProduct(String productId, SingleDataCallback<Product> callback) {
        db.collection("products")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            Product product = documentSnapshot.toObject(Product.class);
                            product.setId(documentSnapshot.getId());
                            callback.onSuccess(product);
                        } catch (Exception e) {
                            Log.w(TAG, "Error parsing product document", e);
                            callback.onFailure("Error parsing product data");
                        }
                    } else {
                        callback.onFailure("Product not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error loading product", e);
                    callback.onFailure("Failed to load product: " + e.getMessage());
                });
    }

    // Banners
    public void loadBanners(DataCallback<Banner> callback) {
        db.collection("banners")
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Banner> banners = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Banner banner = new Banner();
                            banner.setId(document.getId());
                            banner.setTitle(document.getString("title"));
                            banner.setDescription(document.getString("description"));
                            // Set default image for now
                            banner.setImageResource(android.R.drawable.ic_menu_gallery);
                            banners.add(banner);
                        } catch (Exception e) {
                            Log.w(TAG, "Error parsing banner document: " + document.getId(), e);
                        }
                    }
                    callback.onSuccess(banners);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error loading banners", e);
                    callback.onFailure("Failed to load banners: " + e.getMessage());
                });
    }

    // Search products
    public void searchProducts(String query, DataCallback<Product> callback) {
        String searchQuery = query.toLowerCase();
        
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Product product = document.toObject(Product.class);
                            product.setId(document.getId());
                            
                            // Simple text search
                            if (product.getName() != null && product.getName().toLowerCase().contains(searchQuery) ||
                                product.getBrand() != null && product.getBrand().toLowerCase().contains(searchQuery) ||
                                (product.getDescription() != null && product.getDescription().toLowerCase().contains(searchQuery))) {
                                
                                if (product.getImageResource() == 0) {
                                    product.setImageResource(android.R.drawable.ic_menu_gallery);
                                }
                                products.add(product);
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Error parsing product document: " + document.getId(), e);
                        }
                    }
                    callback.onSuccess(products);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error searching products", e);
                    callback.onFailure("Failed to search products: " + e.getMessage());
                });
    }

    // User's cart
    public void loadUserCart(DataCallback<Product> callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("User not authenticated");
            return;
        }

        db.collection("cart")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> productIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String productId = document.getString("productId");
                        if (productId != null) {
                            productIds.add(productId);
                        }
                    }
                    
                    if (productIds.isEmpty()) {
                        callback.onSuccess(new ArrayList<>());
                        return;
                    }
                    
                    // Load actual products
                    db.collection("products")
                            .whereIn("__name__", productIds)
                            .get()
                            .addOnSuccessListener(productSnapshots -> {
                                List<Product> products = new ArrayList<>();
                                for (QueryDocumentSnapshot document : productSnapshots) {
                                    try {
                                        Product product = document.toObject(Product.class);
                                        product.setId(document.getId());
                                        if (product.getImageResource() == 0) {
                                            product.setImageResource(android.R.drawable.ic_menu_gallery);
                                        }
                                        // TODO: Load quantity from cart document
                                        product.setQuantity(1); // Default for now
                                        products.add(product);
                                    } catch (Exception e) {
                                        Log.w(TAG, "Error parsing product document: " + document.getId(), e);
                                    }
                                }
                                callback.onSuccess(products);
                            })
                            .addOnFailureListener(e -> callback.onFailure("Failed to load cart products: " + e.getMessage()));
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error loading cart", e);
                    callback.onFailure("Failed to load cart: " + e.getMessage());
                });
    }

    // User's wishlist
    public void loadUserWishlist(DataCallback<Product> callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("User not authenticated");
            return;
        }

        db.collection("wishlist")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> productIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String productId = document.getString("productId");
                        if (productId != null) {
                            productIds.add(productId);
                        }
                    }
                    
                    if (productIds.isEmpty()) {
                        callback.onSuccess(new ArrayList<>());
                        return;
                    }
                    
                    // Load actual products
                    db.collection("products")
                            .whereIn("__name__", productIds)
                            .get()
                            .addOnSuccessListener(productSnapshots -> {
                                List<Product> products = new ArrayList<>();
                                for (QueryDocumentSnapshot document : productSnapshots) {
                                    try {
                                        Product product = document.toObject(Product.class);
                                        product.setId(document.getId());
                                        if (product.getImageResource() == 0) {
                                            product.setImageResource(android.R.drawable.ic_menu_gallery);
                                        }
                                        products.add(product);
                                    } catch (Exception e) {
                                        Log.w(TAG, "Error parsing product document: " + document.getId(), e);
                                    }
                                }
                                callback.onSuccess(products);
                            })
                            .addOnFailureListener(e -> callback.onFailure("Failed to load wishlist products: " + e.getMessage()));
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error loading wishlist", e);
                    callback.onFailure("Failed to load wishlist: " + e.getMessage());
                });
    }

    // Orders for current user
    public void loadUserOrders(DataCallback<Order> callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("User not authenticated");
            return;
        }

        db.collection("orders")
                .whereEqualTo("userId", currentUser.getUid())
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Order> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Order order = document.toObject(Order.class);
                            order.setId(document.getId());
                            orders.add(order);
                        } catch (Exception e) {
                            Log.w(TAG, "Error parsing order document: " + document.getId(), e);
                        }
                    }
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error loading orders", e);
                    callback.onFailure("Failed to load orders: " + e.getMessage());
                });
    }
} 