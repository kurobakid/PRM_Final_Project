package com.example.finalproject.ui.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalproject.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddEditProductActivity extends AppCompatActivity {

    private EditText editName, editBrand, editPrice, editStock, editDescription;
    private Button buttonSave;
    private FirebaseFirestore db;
    private String productId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_edit_product);

        editName = findViewById(R.id.editProductName);
        editBrand = findViewById(R.id.editProductBrand);
        editPrice = findViewById(R.id.editProductPrice);
        editStock = findViewById(R.id.editProductStock);
        editDescription = findViewById(R.id.editProductDescription);
        buttonSave = findViewById(R.id.buttonSaveProduct);

        db = FirebaseFirestore.getInstance();

        // Check if edit mode
        if (getIntent().hasExtra("productId")) {
            productId = getIntent().getStringExtra("productId");
            loadProductData(productId);
        }

        buttonSave.setOnClickListener(v -> saveProduct());
    }

    private void loadProductData(String productId) {
        db.collection("products").document(productId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        editName.setText(doc.getString("name"));
                        editBrand.setText(doc.getString("brand"));
                        editPrice.setText(String.valueOf(doc.getDouble("price")));
                        editStock.setText(String.valueOf(doc.getLong("stockQuantity")));
                        editDescription.setText(doc.getString("description"));
                    }
                });
    }

    private void saveProduct() {
        String name = editName.getText().toString().trim();
        String brand = editBrand.getText().toString().trim();
        double price = Double.parseDouble(editPrice.getText().toString().trim());
        int stock = Integer.parseInt(editStock.getText().toString().trim());
        String desc = editDescription.getText().toString().trim();

        var data = new HashMap<String, Object>();
        data.put("name", name);
        data.put("brand", brand);
        data.put("price", price);
        data.put("stockQuantity", stock);
        data.put("description", desc);

        if (productId == null) {
            // Add new product
            db.collection("products").add(data)
                    .addOnSuccessListener(ref -> finish());
        } else {
            // Update product
            db.collection("products").document(productId).update(data)
                    .addOnSuccessListener(aVoid -> finish());
        }
    }
}
