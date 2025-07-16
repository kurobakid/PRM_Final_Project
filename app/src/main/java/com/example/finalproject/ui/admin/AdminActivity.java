package com.example.finalproject.ui.admin;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.finalproject.R;

public class AdminActivity extends AppCompatActivity {

    private Button btnProducts, btnPayments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin); // Layout mới

        // Sidebar buttons
        btnProducts = findViewById(R.id.buttonMenuProducts);
        btnPayments = findViewById(R.id.buttonMenuPayments);

        // Load default fragment
        replaceFragment(new ProductsFragment());

        // Button listeners
        btnProducts.setOnClickListener(v -> replaceFragment(new ProductsFragment()));
        // btnCategories.setOnClickListener(v -> replaceFragment(new CategoriesFragment()));
         btnPayments.setOnClickListener(v -> replaceFragment(new PaymentsFragment()));
        // btnUsers.setOnClickListener(v -> replaceFragment(new UsersFragment()));
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adminContentFrame, fragment)
                .commit();
    }
}
