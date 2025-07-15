package com.example.finalproject.ui.admin;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalproject.R;
import com.example.finalproject.model.Order;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class  OrderDetailActivity extends AppCompatActivity {

    private TextView textOrderId, textStatus, textTotal, textDate, textPayment, textShipping;

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

        Order order = (Order) getIntent().getSerializableExtra("order");

        if (order != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            textOrderId.setText(order.getOrderId());
            textStatus.setText(order.getStatus());
            textTotal.setText("$" + order.getTotal());
            textDate.setText(sdf.format(order.getDate()));
            textPayment.setText(order.getPaymentMethod());

            if (order.getShippingAddress() != null) {
                textShipping.setText(order.getShippingAddress().getAddress().toString());
            } else {
                textShipping.setText("No shipping address");
            }
        }
    }
}
