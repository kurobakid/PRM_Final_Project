package com.example.finalproject.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;

public class OrderSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(OrderSuccessActivity.this, MainActivity.class);
            intent.putExtra("navigateToOrders", true);
            startActivity(intent);
            finish();
        }, 2000);
    }
}