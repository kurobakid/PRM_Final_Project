package com.example.finalproject.ui.invoice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
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
import com.google.firebase.firestore.DocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvoiceFragment extends Fragment {
    private TextView textViewInvoiceNumber;
    private TextView textViewInvoiceDate;
    private TextView textViewDueDate;
    private TextView textViewBillingAddress;
    private TextView textViewShippingAddress;
    private TextView textViewPaymentMethod;
    private TextView textViewSubtotal;
    private TextView textViewTax;
    private TextView textViewShipping;
    private TextView textViewTotal;
    private TextView textViewStatus;
    private RecyclerView recyclerViewItems;
    private InvoiceItemAdapter invoiceItemAdapter;
    private List<InvoiceItem> invoiceItems;
    private FirebaseFirestore db;
    private FirebaseAuthHelper authHelper;
    private String orderId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoice, container, false);
        
        // Get order ID from arguments
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
        }
        
        db = FirebaseFirestore.getInstance();
        authHelper = new FirebaseAuthHelper(requireActivity());
        invoiceItems = new ArrayList<>();
        
        initializeViews(view);
        setupRecyclerView();
        loadInvoice();
        
        return view;
    }
    
    private void initializeViews(View view) {
        textViewInvoiceNumber = view.findViewById(R.id.textViewInvoiceNumber);
        textViewInvoiceDate = view.findViewById(R.id.textViewInvoiceDate);
        textViewDueDate = view.findViewById(R.id.textViewDueDate);
        textViewBillingAddress = view.findViewById(R.id.textViewBillingAddress);
        textViewShippingAddress = view.findViewById(R.id.textViewShippingAddress);
        textViewPaymentMethod = view.findViewById(R.id.textViewPaymentMethod);
        textViewSubtotal = view.findViewById(R.id.textViewSubtotal);
        textViewTax = view.findViewById(R.id.textViewTax);
        textViewShipping = view.findViewById(R.id.textViewShipping);
        textViewTotal = view.findViewById(R.id.textViewTotal);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        recyclerViewItems = view.findViewById(R.id.recyclerViewInvoiceItems);
    }
    
    private void setupRecyclerView() {
        invoiceItemAdapter = new InvoiceItemAdapter(invoiceItems);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewItems.setAdapter(invoiceItemAdapter);
    }
    
    private void loadInvoice() {
        if (orderId == null) {
            Toast.makeText(getContext(), "Order ID not found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        db.collection("invoices")
                .whereEqualTo("orderId", orderId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Invoice invoice = document.toObject(Invoice.class);
                        if (invoice != null) {
                            displayInvoice(invoice);
                        }
                    } else {
                        Toast.makeText(getContext(), "Invoice not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load invoice", Toast.LENGTH_SHORT).show();
                });
    }
    
    private void displayInvoice(Invoice invoice) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        
        textViewInvoiceNumber.setText("Invoice #" + invoice.getId());
        textViewInvoiceDate.setText("Date: " + sdf.format(invoice.getInvoiceDate()));
        textViewDueDate.setText("Due: " + sdf.format(invoice.getDueDate()));
        textViewBillingAddress.setText("Billing: " + invoice.getBillingAddress());
        textViewShippingAddress.setText("Shipping: " + invoice.getShippingAddress());
        textViewPaymentMethod.setText("Payment: " + invoice.getPaymentMethod());
        textViewSubtotal.setText(String.format("$%.2f", invoice.getSubtotal()));
        textViewTax.setText(String.format("$%.2f", invoice.getTax()));
        textViewShipping.setText(String.format("$%.2f", invoice.getShipping()));
        textViewTotal.setText(String.format("$%.2f", invoice.getTotal()));
        textViewStatus.setText(invoice.getStatus().toUpperCase());
        
        // Set status color
        switch (invoice.getStatus().toLowerCase()) {
            case "paid":
                textViewStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "pending":
                textViewStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "overdue":
                textViewStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                break;
        }
        
        // Load invoice items
        if (invoice.getItems() != null) {
            invoiceItems.clear();
            invoiceItems.addAll(invoice.getItems());
            invoiceItemAdapter.notifyDataSetChanged();
        }
    }
} 