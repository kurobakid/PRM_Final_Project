package com.example.finalproject.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;

public class OrderFailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_fail);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(OrderFailActivity.this, MainActivity.class);
            intent.putExtra("navigateToCart", true);
            startActivity(intent);
            finish();
        }, 2000);
    }
}