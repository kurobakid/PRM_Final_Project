package com.example.finalproject;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class ElectroShopApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
    }
} 