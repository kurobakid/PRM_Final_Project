package com.example.finalproject.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.finalproject.R;
import com.example.finalproject.adapter.BannerAdapter;
import com.example.finalproject.adapter.ReviewAdapter;
import com.example.finalproject.model.Banner;
import com.example.finalproject.model.Product;
import com.example.finalproject.model.Review;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailFragment extends Fragment {
    private ViewPager2 viewPagerImages;
    private TextView textViewName, textViewBrand, textViewPrice, textViewDescription;
    private RatingBar ratingBar;
    private ImageView imageViewWishlist;
    private Button buttonAddToCart, buttonBuyNow;
    private RecyclerView recyclerViewReviews;
    private BannerAdapter imageAdapter;
    private ReviewAdapter reviewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        viewPagerImages = view.findViewById(R.id.viewPagerProductImages);
        textViewName = view.findViewById(R.id.textViewProductDetailName);
        textViewBrand = view.findViewById(R.id.textViewProductDetailBrand);
        textViewPrice = view.findViewById(R.id.textViewProductDetailPrice);
        textViewDescription = view.findViewById(R.id.textViewProductDetailDescription);
        ratingBar = view.findViewById(R.id.ratingBarProductDetail);
        imageViewWishlist = view.findViewById(R.id.imageViewProductDetailWishlist);
        buttonAddToCart = view.findViewById(R.id.buttonAddToCart);
        buttonBuyNow = view.findViewById(R.id.buttonBuyNow);
        recyclerViewReviews = view.findViewById(R.id.recyclerViewReviews);

        // Mock product data
        Product product = new Product("iPhone 15 Pro", "Apple", 999.99, 4.5, R.drawable.ic_launcher_foreground);
        textViewName.setText(product.getName());
        textViewBrand.setText(product.getBrand());
        textViewPrice.setText(product.getFormattedPrice());
        textViewDescription.setText("The latest iPhone with advanced features and stunning design.");
        ratingBar.setRating((float) product.getRating());

        // Mock images
        List<Banner> images = new ArrayList<>();
        images.add(new Banner("", "", R.drawable.ic_launcher_foreground));
        images.add(new Banner("", "", R.drawable.ic_launcher_background));
        imageAdapter = new BannerAdapter(images);
        viewPagerImages.setAdapter(imageAdapter);

        // Wishlist click
        imageViewWishlist.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Added to wishlist!", Toast.LENGTH_SHORT).show();
        });
        buttonAddToCart.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
        });
        buttonBuyNow.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Proceed to buy!", Toast.LENGTH_SHORT).show();
        });

        // Mock reviews
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review("Alice", 5, "Amazing phone!"));
        reviews.add(new Review("Bob", 4, "Great performance."));
        reviewAdapter = new ReviewAdapter(reviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewReviews.setAdapter(reviewAdapter);

        return view;
    }
} 