package com.example.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {
    
    private static final OnboardingPage[] ONBOARDING_PAGES = {
        new OnboardingPage(
            R.drawable.onboarding_welcome,
            "Welcome to ElectroShop",
            "Your one-stop destination for all electronic devices and gadgets"
        ),
        new OnboardingPage(
            R.drawable.onboarding_products,
            "Browse Products",
            "Discover thousands of high-quality electronic products at competitive prices"
        ),
        new OnboardingPage(
            R.drawable.onboarding_secure,
            "Secure Shopping",
            "Shop with confidence with our secure payment system and buyer protection"
        ),
        new OnboardingPage(
            R.drawable.onboarding_delivery,
            "Fast Delivery",
            "Get your products delivered quickly and safely to your doorstep"
        )
    };

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        OnboardingPage page = ONBOARDING_PAGES[position];
        holder.bind(page);
    }

    @Override
    public int getItemCount() {
        return ONBOARDING_PAGES.length;
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textViewTitle;
        private TextView textViewDescription;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewOnboarding);
            textViewTitle = itemView.findViewById(R.id.textViewOnboardingTitle);
            textViewDescription = itemView.findViewById(R.id.textViewOnboardingDescription);
        }

        public void bind(OnboardingPage page) {
            imageView.setImageResource(page.getImageResId());
            textViewTitle.setText(page.getTitle());
            textViewDescription.setText(page.getDescription());
        }
    }

    public static class OnboardingPage {
        private int imageResId;
        private String title;
        private String description;

        public OnboardingPage(int imageResId, String title, String description) {
            this.imageResId = imageResId;
            this.title = title;
            this.description = description;
        }

        public int getImageResId() { return imageResId; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }
} 