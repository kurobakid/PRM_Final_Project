package com.example.finalproject.ui.admin;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.finalproject.R;
import com.example.finalproject.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddEditProductDialogFragment extends DialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editName, editBrand, editPrice, editStock, editDescription;
    private Button buttonSave;
    private ImageView imageViewProduct;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private StorageReference storageRef;

    private Uri imageUri;
    private String productId;

    public static AddEditProductDialogFragment newInstance(String productId) {
        AddEditProductDialogFragment fragment = new AddEditProductDialogFragment();
        Bundle args = new Bundle();
        args.putString("productId", productId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_product, null);

        editName = view.findViewById(R.id.editProductName);
        editBrand = view.findViewById(R.id.editProductBrand);
        editPrice = view.findViewById(R.id.editProductPrice);
        editStock = view.findViewById(R.id.editProductStock);
        editDescription = view.findViewById(R.id.editProductDescription);
        buttonSave = view.findViewById(R.id.buttonSaveProduct);
        imageViewProduct = view.findViewById(R.id.imageViewProduct);
        progressBar = new ProgressBar(getContext());
        progressBar.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("product-images");

        if (getArguments() != null) {
            productId = getArguments().getString("productId");
            loadProductForEdit(productId);
        }

        imageViewProduct.setOnClickListener(v -> openFileChooser());
        buttonSave.setOnClickListener(v -> saveProduct());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);
        return builder.create();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imageViewProduct);
        }
    }

    private void loadProductForEdit(String id) {
        db.collection("products").document(id)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            editName.setText(product.getName());
                            editBrand.setText(product.getBrand());
                            editPrice.setText(String.valueOf(product.getPrice()));
                            editStock.setText(String.valueOf(product.getStockQuantity()));
                            editDescription.setText(product.getDescription());

                            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                                Glide.with(this).load(product.getImageUrl()).into(imageViewProduct);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load product", Toast.LENGTH_SHORT).show());
    }

    private void saveProduct() {
        String name = editName.getText().toString().trim();
        String brand = editBrand.getText().toString().trim();
        String priceStr = editPrice.getText().toString().trim();
        String stockStr = editStock.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(brand) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(stockStr)) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int stock = Integer.parseInt(stockStr);

        progressBar.setVisibility(View.VISIBLE);
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(productId + "/" + System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return fileRef.getDownloadUrl();
                    })
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            String imageUrl = task.getResult().toString();
                            saveProductToFirestore(name, brand, price, stock, description, imageUrl);
                        } else {
                            Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            saveProductToFirestore(name, brand, price, stock, description, null);
        }
    }

    private void saveProductToFirestore(String name, String brand, double price, int stock, String description, String imageUrl) {
        Product product = new Product();
        product.setName(name);
        product.setBrand(brand);
        product.setPrice(price);
        product.setStockQuantity(stock);
        product.setDescription(description);
        if (imageUrl != null) product.setImageUrl(imageUrl);

        if (productId != null) {
            db.collection("products").document(productId)
                    .set(product)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Product updated", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show());
        } else {
            db.collection("products")
                    .add(product)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(getContext(), "Product added", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Add failed", Toast.LENGTH_SHORT).show());
        }
    }
}
