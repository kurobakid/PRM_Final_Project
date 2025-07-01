package com.example.finalproject.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalproject.R;
import com.example.finalproject.utils.FirebaseDataInitializer;

public class AdminActivity extends AppCompatActivity {
    private Button buttonInitializeData;
    private Button buttonClearData;
    private TextView textViewStatus;
    private FirebaseDataInitializer dataInitializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dataInitializer = new FirebaseDataInitializer();
        
        buttonInitializeData = findViewById(R.id.buttonInitializeData);
        buttonClearData = findViewById(R.id.buttonClearData);
        textViewStatus = findViewById(R.id.textViewStatus);

        buttonInitializeData.setOnClickListener(v -> {
            initializeSampleData();
        });

        buttonClearData.setOnClickListener(v -> {
            clearAllData();
        });
    }

    private void initializeSampleData() {
        buttonInitializeData.setEnabled(false);
        textViewStatus.setText("Initializing sample data...");
        
        dataInitializer.initializeSampleDataIfNeeded();
        
        Toast.makeText(this, "Sample data initialization started. Check logs for progress.", Toast.LENGTH_LONG).show();
        
        // Re-enable button after a delay
        buttonInitializeData.postDelayed(() -> {
            buttonInitializeData.setEnabled(true);
            textViewStatus.setText("Sample data initialization completed. Check Firebase Console.");
        }, 3000);
    }

    private void clearAllData() {
        buttonClearData.setEnabled(false);
        textViewStatus.setText("Clearing all data...");
        
        dataInitializer.clearAllData();
        
        Toast.makeText(this, "Data clearing started. Check logs for progress.", Toast.LENGTH_LONG).show();
        
        // Re-enable button after a delay
        buttonClearData.postDelayed(() -> {
            buttonClearData.setEnabled(true);
            textViewStatus.setText("Data clearing completed. Check Firebase Console.");
        }, 3000);
    }
} 