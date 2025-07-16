package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.finalproject.ui.orders.OrdersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase if not already initialized
        FirebaseApp.initializeApp(this);

        // Enable App Check with Play Integrity
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        // Setup Navigation component using NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();

        // Setup Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNav, navController);
        handleNavigationIntent(getIntent());
    }
    // In MainActivity.java, update handleNavigationIntent:
    private void handleNavigationIntent(Intent intent) {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();

        if (intent.getBooleanExtra("navigateToOrders", false)) {
            BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
            bottomNav.setSelectedItemId(R.id.nav_orders);
        }
        if (intent.getBooleanExtra("navigateToCart", false)) {
            BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
            bottomNav.setSelectedItemId(R.id.nav_cart);
        }
        if (intent.getBooleanExtra("navigateToAddressBook", false)) {
            navController.navigate(R.id.nav_address_book);
        }
        if (intent.getBooleanExtra("navigateToDetail", false)) {
            Bundle args = new Bundle();
            args.putString("productId", intent.getStringExtra("productId"));
            navController.navigate(R.id.nav_product_detail, args);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNavigationIntent(intent);
    }
}