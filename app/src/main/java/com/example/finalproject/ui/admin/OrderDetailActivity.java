package com.example.finalproject.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.adapter.OrderItemAdapter;
import com.example.finalproject.model.Order;
import com.example.finalproject.model.Product;
import com.example.finalproject.ui.product.ProductDetailFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class  OrderDetailActivity extends AppCompatActivity {

    private TextView textOrderId, textStatus, textTotal, textDate, textPayment, textShipping;
    private RecyclerView recyclerOrderItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        textOrderId = findViewById(R.id.textOrderId);
        textStatus = findViewById(R.id.textStatus);
        textTotal = findViewById(R.id.textTotal);
        textDate = findViewById(R.id.textDate);
        textPayment = findViewById(R.id.textPaymentMethod);
        textShipping = findViewById(R.id.textShippingAddress);
        recyclerOrderItems = findViewById(R.id.recyclerOrderItems);

        Order order = (Order) getIntent().getSerializableExtra("order");

        if (order != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            textOrderId.setText(order.getOrderId());
            textStatus.setText(order.getStatus());
            textTotal.setText("$" + order.getTotal());
            textDate.setText(sdf.format(order.getDate()));
            textPayment.setText(order.getPaymentMethod());
            recyclerOrderItems.setLayoutManager(new LinearLayoutManager(this));


            List<Product> productList = new ArrayList<>();
            for (Map<String, Object> itemMap : order.getItems()) {
                Product product = new Product();
                product.setId((String) itemMap.get("id"));
                product.setName((String) itemMap.get("name"));
                product.setBrand((String) itemMap.get("brand"));
                product.setPrice(itemMap.get("price") != null ? Double.parseDouble(itemMap.get("price").toString()) : 0.0);
                product.setQuantity(itemMap.get("quantity") != null ? Integer.parseInt(itemMap.get("quantity").toString()) : 1);
                product.setImageUrl((String) itemMap.get("imageUrl"));
                product.setCartDocId((String) itemMap.get("cartDocId"));
                product.setDescription((String) itemMap.get("description"));
                product.setStockQuantity(itemMap.get("stockQuantity") != null ? Integer.parseInt(itemMap.get("stockQuantity").toString()) : 0);
                product.setRating(itemMap.get("rating") != null ? Double.parseDouble(itemMap.get("rating").toString()) : 0.0);
                product.setImageResource(itemMap.get("imageResource") != null ? Integer.parseInt(itemMap.get("imageResource").toString()) : 0);
                productList.add(product);
            }
            recyclerOrderItems.setAdapter(new OrderItemAdapter(productList));
            OrderItemAdapter adapter = new OrderItemAdapter(productList);
            adapter.setOnItemClickListener(product -> {
                // In OrderDetailActivity.java, inside product click listener
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("navigateToDetail", true);
                intent.putExtra("productId", product.getId());
                startActivity(intent);
            });
            recyclerOrderItems.setAdapter(adapter);

            if (order.getShippingAddress() != null) {
                textShipping.setText(order.getShippingAddress().getAddress().toString());
            } else {
                textShipping.setText("No shipping address");
            }
        }
    }
}
