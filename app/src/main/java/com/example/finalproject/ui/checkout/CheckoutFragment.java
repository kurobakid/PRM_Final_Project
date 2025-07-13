package com.example.finalproject.ui.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.finalproject.model.Address;
import com.example.finalproject.model.Order;
import com.example.finalproject.model.Product;
import com.example.finalproject.utils.FirebaseAuthHelper;
import com.example.finalproject.utils.FirebaseRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CheckoutFragment extends Fragment {
    private RecyclerView recyclerViewCheckoutItems;
    private CartAdapter checkoutAdapter;
    private List<Product> checkoutItems;
    private TextView textViewSubtotal, textViewTax, textViewShipping, textViewTotal;
    private TextView textViewSelectedAddress;
    private RadioGroup radioGroupPaymentMethod;
    private RadioButton radioCreditCard, radioPayPal, radioCashOnDelivery;
    private Button buttonPlaceOrder;
    private FirebaseFirestore db;
    private FirebaseAuthHelper authHelper;
    private Address selectedAddress;
    private double subtotal = 0;
    private double tax = 0;
    private double shipping = 15.99;
    private FirebaseRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        
        db = FirebaseFirestore.getInstance();
        authHelper = new FirebaseAuthHelper(requireActivity());
        repository = new FirebaseRepository();
        checkoutItems = new ArrayList<>();
        
        initViews(view);
        setupRecyclerView();
        setupListeners();
        
        loadCheckoutItems();
        loadUserAddresses();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerViewCheckoutItems = view.findViewById(R.id.recyclerViewCheckoutItems);
        textViewSubtotal = view.findViewById(R.id.textViewCheckoutSubtotal);
        textViewTax = view.findViewById(R.id.textViewCheckoutTax);
        textViewShipping = view.findViewById(R.id.textViewCheckoutShipping);
        textViewTotal = view.findViewById(R.id.textViewCheckoutTotal);
        textViewSelectedAddress = view.findViewById(R.id.textViewSelectedAddress);
        radioGroupPaymentMethod = view.findViewById(R.id.radioGroupPaymentMethod);
        radioCreditCard = view.findViewById(R.id.radioCreditCard);
        radioPayPal = view.findViewById(R.id.radioPayPal);
        radioCashOnDelivery = view.findViewById(R.id.radioCashOnDelivery);
        buttonPlaceOrder = view.findViewById(R.id.buttonPlaceOrder);
    }
    
    private void setupRecyclerView() {
        checkoutAdapter = new CartAdapter(checkoutItems, new CartAdapter.OnCartChangeListener() {
            @Override
            public void onQuantityChanged(int position, Product product, int newQuantity) {
                updateQuantity(product, newQuantity - product.getQuantity());
            }
            @Override
            public void onItemRemoved(int position, Product product) {
                removeItem(product);
            }
        });
        
        recyclerViewCheckoutItems.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCheckoutItems.setAdapter(checkoutAdapter);
    }
    
    private void setupListeners() {
        textViewSelectedAddress.setOnClickListener(v -> {
            // Navigate to address selection
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_checkoutFragment_to_addressBookFragment);
        });
        
        buttonPlaceOrder.setOnClickListener(v -> {
            if (validateCheckout()) {
                placeOrder();
            }
        });
    }
    
    private void loadCheckoutItems() {
        // Load real cart items from Firestore
        repository.loadUserCart(new FirebaseRepository.DataCallback<Product>() {
            @Override
            public void onSuccess(List<Product> data) {
                checkoutItems.clear();
                checkoutItems.addAll(data);
                checkoutAdapter.notifyDataSetChanged();
                calculateTotals();
            }
            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Failed to load cart for checkout", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadUserAddresses() {
        FirebaseUser user = authHelper.getCurrentUser();
        if (user == null) return;
        
        db.collection("addresses")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Select the first address as default
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                        selectedAddress = document.toObject(Address.class);
                        if (selectedAddress != null) {
                            selectedAddress.setId(document.getId());
                            updateAddressDisplay();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Create sample address for demo
                    createSampleAddress();
                });
    }
    
    private void createSampleAddress() {
        selectedAddress = new Address();
        selectedAddress.setId("sample");
        selectedAddress.setFullName("John Doe");
        selectedAddress.setAddress("123 Main Street, Apt 4B");
        selectedAddress.setCity("New York");
        selectedAddress.setState("NY");
        selectedAddress.setZipCode("10001");
        selectedAddress.setPhone("+1-555-123-4567");
        updateAddressDisplay();
    }
    
    private void updateAddressDisplay() {
        if (selectedAddress != null) {
            String addressText = String.format("%s\n%s\n%s, %s %s\n%s",
                    selectedAddress.getFullName(),
                    selectedAddress.getAddress(),
                    selectedAddress.getCity(),
                    selectedAddress.getState(),
                    selectedAddress.getZipCode(),
                    selectedAddress.getPhone());
            textViewSelectedAddress.setText(addressText);
        }
    }
    
    private void updateQuantity(Product product, int change) {
        int newQuantity = product.getQuantity() + change;
        if (newQuantity > 0) {
            product.setQuantity(newQuantity);
            checkoutAdapter.notifyDataSetChanged();
            calculateTotals();
        }
    }
    
    private void removeItem(Product product) {
        checkoutItems.remove(product);
        checkoutAdapter.notifyDataSetChanged();
        calculateTotals();
    }
    
    private void calculateTotals() {
        subtotal = 0;
        for (Product product : checkoutItems) {
            subtotal += product.getPrice() * product.getQuantity();
        }
        
        tax = subtotal * 0.08; // 8% tax
        double total = subtotal + tax + shipping;
        
        textViewSubtotal.setText(String.format("$%.2f", subtotal));
        textViewTax.setText(String.format("$%.2f", tax));
        textViewShipping.setText(String.format("$%.2f", shipping));
        textViewTotal.setText(String.format("$%.2f", total));
    }
    
    private boolean validateCheckout() {
        if (selectedAddress == null) {
            Toast.makeText(getContext(), "Please select a shipping address", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (checkoutItems.isEmpty()) {
            Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (radioGroupPaymentMethod.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Please select a payment method", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private void placeOrder() {
        FirebaseUser user = authHelper.getCurrentUser();
        if (user == null) return;
        
        String paymentMethod = "Credit Card";
        if (radioPayPal.isChecked()) paymentMethod = "PayPal";
        else if (radioCashOnDelivery.isChecked()) paymentMethod = "Cash on Delivery";
        
        // Create order
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setUserId(user.getUid());
        order.setOrderNumber("ORD-" + System.currentTimeMillis());
        order.setDate(new Date());
        order.setStatus("Pending");
        order.setPaymentMethod(paymentMethod);
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setShipping(shipping);
        order.setTotal(subtotal + tax + shipping);
        order.setShippingAddress(selectedAddress);
        
        // Convert products to order items
        List<Map<String, Object>> orderItems = new ArrayList<>();
        for (Product product : checkoutItems) {
            Map<String, Object> item = new HashMap<>();
            item.put("productId", product.getId());
            item.put("name", product.getName());
            item.put("price", product.getPrice());
            item.put("quantity", product.getQuantity());
            item.put("imageUrl", product.getImageUrl());
            orderItems.add(item);
        }
        order.setItems(orderItems);
        
        // Save order to Firebase
        db.collection("orders")
                .document(order.getId())
                .set(order)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_LONG).show();
                    
                    // Navigate to order confirmation
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", order.getId());
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_checkoutFragment_to_orderConfirmationFragment, bundle);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to place order", Toast.LENGTH_SHORT).show();
                });
    }
} 