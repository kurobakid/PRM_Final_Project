package com.example.finalproject.ui.payment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.adapter.ConfirmAdapter;
import com.example.finalproject.model.Product;
import com.example.finalproject.ui.orders.OrdersFragment;
import com.example.finalproject.ui.payment.zalo.Api.CreateOrder;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ConfirmActivity extends AppCompatActivity {
    private RecyclerView recyclerViewConfirm;
    private TextView textViewTotal;
    private Button buttonConfirm;
    private List<Product> cartItems = new ArrayList<>();
    private TextView textViewTotalVND;
    private Double totalDB;
    private Double totalDBVND;
    private String totalST;
    private String totalSTVND;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        recyclerViewConfirm = findViewById(R.id.recyclerViewConfirm);
        textViewTotal = findViewById(R.id.textViewTotal);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        textViewTotalVND = findViewById(R.id.textViewTotalVND);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(553, Environment.SANDBOX);

        cartItems = (ArrayList<Product>) getIntent().getSerializableExtra("cartItems");
        totalDB = getIntent().getDoubleExtra("total", 0);
        totalDBVND = totalDB*rates();
        textViewTotalVND.setText(String.format("Total: $%.2f", totalDBVND));
        totalST = String.format("%.0f", totalDB);
        totalSTVND = String.format("%.0f", totalDBVND);

        ConfirmAdapter confirmAdapter = new ConfirmAdapter(cartItems);
        recyclerViewConfirm.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewConfirm.setAdapter(confirmAdapter);

        textViewTotal.setText(String.format("Total: $%.2f", totalDB));
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateOrder orderApi = new CreateOrder();
                try {
                    JSONObject data = orderApi.createOrder(totalSTVND);
                    String code = data.getString("returncode");
                    if (code.equals("1")) {
                        String token = data.getString("zptranstoken");
                        ZaloPaySDK.getInstance().payOrder(ConfirmActivity.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                Intent intent = new Intent(ConfirmActivity.this, MainActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {

                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {

                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    protected Double rates(Void... voids) {
        try {
            URL url = new URL("https://api.exchangerate.host/latest?base=USD&symbols=VND");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            Scanner sc = new Scanner(url.openStream());
            StringBuilder inline = new StringBuilder();
            while (sc.hasNext()) {
                inline.append(sc.nextLine());
            }
            sc.close();
            JSONObject data = new JSONObject(inline.toString());
            return data.getJSONObject("rates").getDouble("VND");
        } catch (Exception e) {
            e.printStackTrace();
            return 26000.0; // fallback
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}