package com.example.finalproject.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.adapter.AdminProductAdapter;
import com.example.finalproject.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminProductAdapter adapter;
    private List<Product> productList;
    private FirebaseFirestore db;

    public ProductsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productList = new ArrayList<>();
        adapter = new AdminProductAdapter(productList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadProducts();

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddProduct);
        fabAdd.setOnClickListener(v -> {
            AddEditProductDialogFragment dialog = new AddEditProductDialogFragment();
            dialog.show(getChildFragmentManager(), "AddEditProductDialog");
        });

        adapter.setOnAdminProductClickListener(new AdminProductAdapter.OnAdminProductClickListener() {
            @Override
            public void onEditClick(Product product) {
                AddEditProductDialogFragment dialog = AddEditProductDialogFragment.newInstance(product.getId());
                dialog.show(getChildFragmentManager(), "EditProductDialog");
            }

            @Override
            public void onDeleteLongClick(Product product) {
                showDeleteConfirmation(product);
            }
        });

        return view;
    }

    private void loadProducts() {
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear();
                    for (var doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        product.setId(doc.getId());
                        productList.add(product);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load products: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void showDeleteConfirmation(Product product) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete " + product.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteProduct(product))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProduct(Product product) {
        db.collection("products").document(product.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    productList.remove(product);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Product deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}