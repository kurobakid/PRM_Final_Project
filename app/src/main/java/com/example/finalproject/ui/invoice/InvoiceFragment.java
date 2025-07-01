package com.example.finalproject.ui.invoice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.adapter.InvoiceItemAdapter;
import com.example.finalproject.model.Invoice;
import com.example.finalproject.model.InvoiceItem;
import com.example.finalproject.utils.FirebaseAuthHelper;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvoiceFragment extends Fragment {
    private TextView textViewInvoiceNumber, textViewDate, textViewStatus;
    private TextView textViewSubtotal, textViewTax, textViewShipping, textViewTotal;
    private TextView textViewShippingAddress, textViewBillingAddress;
    private RecyclerView recyclerViewInvoiceItems;
    private InvoiceItemAdapter invoiceItemAdapter;
    private List<InvoiceItem> invoiceItemList;
    private FirebaseFirestore db;
    private FirebaseAuthHelper authHelper;
    private String orderId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoice, container, false);
        
        initViews(view);
        setupRecyclerView();
        
        db = FirebaseFirestore.getInstance();
        authHelper = new FirebaseAuthHelper(requireActivity());
        invoiceItemList = new ArrayList<>();
        
        // Get order ID from arguments
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
        }
        
        if (orderId != null) {
            loadInvoice(orderId);
        } else {
            // Load sample invoice for demo
            loadSampleInvoice();
        }
        
        return view;
    }
    
    private void initViews(View view) {
        textViewInvoiceNumber = view.findViewById(R.id.textViewInvoiceNumber);
        textViewDate = view.findViewById(R.id.textViewInvoiceDate);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        textViewSubtotal = view.findViewById(R.id.textViewSubtotal);
        textViewTax = view.findViewById(R.id.textViewTax);
        textViewShipping = view.findViewById(R.id.textViewShipping);
        textViewTotal = view.findViewById(R.id.textViewTotal);
        textViewShippingAddress = view.findViewById(R.id.textViewShippingAddress);
        textViewBillingAddress = view.findViewById(R.id.textViewBillingAddress);
        recyclerViewInvoiceItems = view.findViewById(R.id.recyclerViewInvoiceItems);
    }
    
    private void setupRecyclerView() {
        invoiceItemAdapter = new InvoiceItemAdapter(invoiceItemList);
        recyclerViewInvoiceItems.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewInvoiceItems.setAdapter(invoiceItemAdapter);
    }
    
    private void loadInvoice(String orderId) {
        FirebaseUser user = authHelper.getCurrentUser();
        if (user == null) return;
        
        db.collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Invoice invoice = documentSnapshot.toObject(Invoice.class);
                        if (invoice != null) {
                            displayInvoice(invoice);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Load sample data on failure
                    loadSampleInvoice();
                });
    }
    
    private void loadSampleInvoice() {
        // Create sample invoice data
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        
        textViewInvoiceNumber.setText("INV-2024-001");
        textViewDate.setText(sdf.format(new Date()));
        textViewStatus.setText("Paid");
        textViewStatus.setBackgroundResource(R.drawable.invoice_status_background);
        
        // Sample invoice items
        invoiceItemList.clear();
        invoiceItemList.add(new InvoiceItem("1", "iPhone 15 Pro", 1, 999.99, 999.99, "https://example.com/iphone.jpg"));
        invoiceItemList.add(new InvoiceItem("2", "Samsung Galaxy S24", 1, 899.99, 899.99, "https://example.com/samsung.jpg"));
        invoiceItemAdapter.notifyDataSetChanged();
        
        // Calculate totals
        double subtotal = 0;
        for (InvoiceItem item : invoiceItemList) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        
        double tax = subtotal * 0.08; // 8% tax
        double shipping = 15.99;
        double total = subtotal + tax + shipping;
        
        textViewSubtotal.setText(String.format("$%.2f", subtotal));
        textViewTax.setText(String.format("$%.2f", tax));
        textViewShipping.setText(String.format("$%.2f", shipping));
        textViewTotal.setText(String.format("$%.2f", total));
        
        // Sample addresses
        textViewShippingAddress.setText("John Doe\n123 Main Street\nApt 4B\nNew York, NY 10001");
        textViewBillingAddress.setText("John Doe\n123 Main Street\nApt 4B\nNew York, NY 10001");
    }
    
    private void displayInvoice(Invoice invoice) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        
        textViewInvoiceNumber.setText(invoice.getInvoiceNumber());
        textViewDate.setText(sdf.format(invoice.getDate()));
        textViewStatus.setText(invoice.getStatus());
        
        // Load invoice items
        invoiceItemList.clear();
        invoiceItemList.addAll(invoice.getItems());
        invoiceItemAdapter.notifyDataSetChanged();
        
        // Display totals
        textViewSubtotal.setText(String.format("$%.2f", invoice.getSubtotal()));
        textViewTax.setText(String.format("$%.2f", invoice.getTax()));
        textViewShipping.setText(String.format("$%.2f", invoice.getShipping()));
        textViewTotal.setText(String.format("$%.2f", invoice.getTotal()));
        
        // Display addresses
        textViewShippingAddress.setText(invoice.getShippingAddress());
        textViewBillingAddress.setText(invoice.getBillingAddress());
    }
} 