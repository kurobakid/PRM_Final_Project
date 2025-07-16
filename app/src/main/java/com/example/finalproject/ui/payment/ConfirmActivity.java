package com.example.finalproject.ui.payment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.adapter.ConfirmAdapter;
import com.example.finalproject.model.Address;
import com.example.finalproject.model.Order;
import com.example.finalproject.model.Product;
import com.example.finalproject.ui.address.AddressBookFragment;
import com.example.finalproject.ui.orders.OrderFailActivity;
import com.example.finalproject.ui.orders.OrderSuccessActivity;
import com.example.finalproject.ui.orders.OrdersFragment;
import com.example.finalproject.ui.payment.zalo.Api.CreateOrder;
import com.example.finalproject.utils.FirebaseAuthHelper;
import com.example.finalproject.utils.FirebaseRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ConfirmActivity extends AppCompatActivity {
    private RecyclerView recyclerViewConfirm;
    private TextView textViewTotal;
    private Button buttonConfirm;
    private List<Product> cartItems = new ArrayList<>();
    private TextView textViewTotalVND;
    private Double totalDB;
    private Double totalDBVND;
    private String totalST;
    private String totalSTVND;
    private FirebaseRepository repo = new FirebaseRepository();

    private FirebaseAuth auth;
    private Address shipAddress;
    private FirebaseFirestore db;
    private FirebaseAuthHelper authHelper;
    private TextView textViewSelectedAddress;
    private Spinner spinnerAddresses;
    private List<Address> addressList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
//        repo.getUserAddress(new FirebaseRepository.SingleDataCallback<Address>() {
//            @Override
//            public void onSuccess(Address address) {
//                shipAddress = address;
//            }
//            @Override
//            public void onFailure(String error) {
//                // Handle error
//            }
//        });
        authHelper = new FirebaseAuthHelper(this);
        db = FirebaseFirestore.getInstance();
        textViewSelectedAddress = findViewById(R.id.textViewSelectedAddress);
        loadUserAddresses();
        spinnerAddresses = findViewById(R.id.spinnerAddresses); // Add Spinner to your layout


        recyclerViewConfirm = findViewById(R.id.recyclerViewConfirm);
        textViewTotal = findViewById(R.id.textViewTotal);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        textViewTotalVND = findViewById(R.id.textViewTotalVND);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(553, Environment.SANDBOX);

        cartItems = (ArrayList<Product>) getIntent().getSerializableExtra("cartItems");
        totalDB = getIntent().getDoubleExtra("total", 0);
        totalDBVND = totalDB*rates();
        textViewTotalVND.setText(String.format("Total (VND): %.2fVND", totalDBVND));
        totalST = String.format("%.0f", totalDB);
        totalSTVND = String.format("%.0f", totalDBVND);

        ConfirmAdapter confirmAdapter = new ConfirmAdapter(cartItems);
        recyclerViewConfirm.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewConfirm.setAdapter(confirmAdapter);

        textViewTotal.setText(String.format("Total: $%.2f", totalDB));
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateOrder orderApi = new CreateOrder();
                try {
                    JSONObject data = orderApi.createOrder(totalSTVND);
                    String code = data.getString("returncode");
                    if (code.equals("1")) {
                        String token = data.getString("zptranstoken");
                        ZaloPaySDK.getInstance().payOrder(ConfirmActivity.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                Order order = new Order();
                                List<Map<String, Object>> orderItems = new ArrayList<>();
                                for (Product product : cartItems) {
                                    Map<String, Object> itemMap = new HashMap<>();
                                    itemMap.put("id", product.getId());
                                    itemMap.put("name", product.getName());
                                    itemMap.put("brand", product.getBrand());
                                    itemMap.put("price", product.getPrice());
                                    itemMap.put("quantity", product.getQuantity());
                                    itemMap.put("imageUrl", product.getImageUrl());
                                    itemMap.put("cartDocId", product.getCartDocId());
                                    itemMap.put("description", product.getDescription());
                                    itemMap.put("stockQuantity", product.getStockQuantity());
                                    itemMap.put("rating", product.getRating());
                                    itemMap.put("imageResource", product.getImageResource());
                                    orderItems.add(itemMap);
                                }
                                order.setItems(orderItems);
                                order.setStatus("Paid");
                                order.setTotal(totalDB);
                                order.setPaymentMethod("ZaloPay");
                                order.setShipping(100000.0);
                                order.setSubtotal(totalDBVND);
                                order.setTax(0.0);
                                order.setShippingAddress(shipAddress);
                                repo.createOrder(order, new FirebaseRepository.SingleDataCallback<String>() {
                                    @Override
                                    public void onSuccess(String orderId) {
                                        Intent intent = new Intent(ConfirmActivity.this, OrderSuccessActivity.class);
                                        startActivity(intent);
                                        finish();
                                        repo.clearUserCart(new FirebaseRepository.SingleDataCallback<Void>() {
                                            @Override
                                            public void onSuccess(Void data) {
                                            }
                                            @Override
                                            public void onFailure(String error) {
                                                // Handle error
                                            }
                                        });
                                    }
                                    @Override
                                    public void onFailure(String error) {
                                        // Handle error
                                    }
                                });
                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {
                                Order order = new Order();
                                List<Map<String, Object>> orderItems = new ArrayList<>();
                                for (Product product : cartItems) {
                                    Map<String, Object> itemMap = new HashMap<>();
                                    itemMap.put("id", product.getId());
                                    itemMap.put("name", product.getName());
                                    itemMap.put("brand", product.getBrand());
                                    itemMap.put("price", product.getPrice());
                                    itemMap.put("quantity", product.getQuantity());
                                    itemMap.put("imageUrl", product.getImageUrl());
                                    itemMap.put("cartDocId", product.getCartDocId());
                                    itemMap.put("description", product.getDescription());
                                    itemMap.put("stockQuantity", product.getStockQuantity());
                                    itemMap.put("rating", product.getRating());
                                    itemMap.put("imageResource", product.getImageResource());
                                    orderItems.add(itemMap);
                                }
                                order.setItems(orderItems);
                                order.setTotal(totalDB);
                                order.setStatus("Canceled");
                                order.setPaymentMethod("ZaloPay");
                                order.setShipping(100000.0);
                                order.setSubtotal(totalDBVND);
                                order.setTax(0.0);
                                order.setShippingAddress(shipAddress);
                                repo.createOrder(order, new FirebaseRepository.SingleDataCallback<String>() {
                                    @Override
                                    public void onSuccess(String orderId) {
                                        Intent intent = new Intent(ConfirmActivity.this, OrderFailActivity.class);
                                        startActivity(intent);
                                        finish();
//                                        repo.clearUserCart(new FirebaseRepository.SingleDataCallback<Void>() {
//                                            @Override
//                                            public void onSuccess(Void data) {
//                                            }
//                                            @Override
//                                            public void onFailure(String error) {
//                                                // Handle error
//                                            }
//                                        });
                                    }
                                    @Override
                                    public void onFailure(String error) {
                                        // Handle error
                                    }
                                });
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                Order order = new Order();
                                List<Map<String, Object>> orderItems = new ArrayList<>();
                                for (Product product : cartItems) {
                                    Map<String, Object> itemMap = new HashMap<>();
                                    itemMap.put("id", product.getId());
                                    itemMap.put("name", product.getName());
                                    itemMap.put("brand", product.getBrand());
                                    itemMap.put("price", product.getPrice());
                                    itemMap.put("quantity", product.getQuantity());
                                    itemMap.put("imageUrl", product.getImageUrl());
                                    itemMap.put("cartDocId", product.getCartDocId());
                                    itemMap.put("description", product.getDescription());
                                    itemMap.put("stockQuantity", product.getStockQuantity());
                                    itemMap.put("rating", product.getRating());
                                    itemMap.put("imageResource", product.getImageResource());
                                    orderItems.add(itemMap);
                                }
                                order.setItems(orderItems);
                                order.setTotal(totalDB);
                                order.setStatus("Canceled");
                                order.setPaymentMethod("ZaloPay");
                                order.setShipping(100000.0);
                                order.setSubtotal(totalDBVND);
                                order.setTax(0.0);
                                order.setShippingAddress(shipAddress);
                                repo.createOrder(order, new FirebaseRepository.SingleDataCallback<String>() {
                                    @Override
                                    public void onSuccess(String orderId) {
                                        Intent intent = new Intent(ConfirmActivity.this, OrderFailActivity.class);
                                        startActivity(intent);
                                        finish();
//                                        repo.clearUserCart(new FirebaseRepository.SingleDataCallback<Void>() {
//                                            @Override
//                                            public void onSuccess(Void data) {
//                                            }
//                                            @Override
//                                            public void onFailure(String error) {
//                                                // Handle error
//                                            }
//                                        });
                                    }
                                    @Override
                                    public void onFailure(String error) {
                                        // Handle error
                                    }
                                });
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    protected Double rates(Void... voids) {
        try {
            URL url = new URL("https://api.exchangerate.host/latest?base=USD&symbols=VND");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            Scanner sc = new Scanner(url.openStream());
            StringBuilder inline = new StringBuilder();
            while (sc.hasNext()) {
                inline.append(sc.nextLine());
            }
            sc.close();
            JSONObject data = new JSONObject(inline.toString());
            return data.getJSONObject("rates").getDouble("VND");
        } catch (Exception e) {
            e.printStackTrace();
            return 26000.0; // fallback
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
    private void loadUserAddresses() {
        FirebaseUser user = authHelper.getCurrentUser();
        if (user == null) return;

        db.collection("addresses")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    addressList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Address address = document.toObject(Address.class);
                        address.setId(document.getId());
                        addressList.add(address);
                    }
                    if (addressList.isEmpty()) {
                        showNoAddressDialog();
                    } else {
                        setupAddressSpinner();
                    }
                })
                .addOnFailureListener(e -> {
                    showNoAddressDialog();
                });
    }

    // Add this method to show the dialog and redirect
    private void showNoAddressDialog() {
        buttonConfirm.setEnabled(false);
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("No Address Found")
                .setMessage("You need to add a shipping address before confirming your order. Go to Address Book now?")
                .setCancelable(false)
                .setPositiveButton("Go to Address Book", (dialog, which) -> {
                    // Redirect to AddressBookActivity or Fragment
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("navigateToAddressBook", true);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Optionally finish or just dismiss
                    finish();
                })
                .show();
    }
    private void setupAddressSpinner() {
        ArrayAdapter<Address> adapter = new ArrayAdapter<Address>(this,
                android.R.layout.simple_spinner_item, addressList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView label = (TextView) super.getView(position, convertView, parent);
                Address address = getItem(position);
                label.setText(address.getAddress());
                return label;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView label = (TextView) super.getDropDownView(position, convertView, parent);
                Address address = getItem(position);
                label.setText(address.getAddress());
                return label;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddresses.setAdapter(adapter);

        spinnerAddresses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shipAddress = addressList.get(position);
                updateAddressDisplay();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateAddressDisplay() {
        if (shipAddress != null) {
            String addressText = String.format("%s\n%s\n%s, %s %s\n%s",
                    shipAddress.getFullName(),
                    shipAddress.getAddress(),
                    shipAddress.getCity(),
                    shipAddress.getState(),
                    shipAddress.getZipCode(),
                    shipAddress.getPhone());
            textViewSelectedAddress.setText(addressText);
        }
    }
}