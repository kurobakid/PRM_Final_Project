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
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.finalproject.R;
import com.example.finalproject.adapter.BannerAdapter;
import com.example.finalproject.adapter.ReviewAdapter;
import com.example.finalproject.model.Banner;
import com.example.finalproject.model.Product;
import com.example.finalproject.model.Review;
import com.example.finalproject.model.Order;
import com.example.finalproject.utils.FirebaseRepository;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductDetailFragment extends Fragment {
    private ViewPager2 viewPagerImages;
    private TextView textViewName, textViewBrand, textViewPrice, textViewDescription;
    private ImageView imageViewBack;
    private Button buttonAddToCart, buttonWriteReview;
    private BannerAdapter imageAdapter;
    private FirebaseRepository repository;
    private Product product;
    private String productId;
    private String currentUserId;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();

    private TextView textViewAverageRating;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        imageViewBack = view.findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(v -> requireActivity().onBackPressed());

        viewPagerImages = view.findViewById(R.id.viewPagerProductImages);
        textViewName = view.findViewById(R.id.textViewProductDetailName);
        textViewBrand = view.findViewById(R.id.textViewProductDetailBrand);
        textViewPrice = view.findViewById(R.id.textViewProductDetailPrice);
        textViewDescription = view.findViewById(R.id.textViewProductDetailDescription);
        buttonAddToCart = view.findViewById(R.id.buttonAddToCart);
        buttonWriteReview = view.findViewById(R.id.buttonWriteReview);
        recyclerViewReviews = view.findViewById(R.id.recyclerViewReviews);
        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewReviews.setAdapter(reviewAdapter);
        textViewAverageRating = view.findViewById(R.id.textViewAverageRating);

        repository = new FirebaseRepository();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        // Get productId from arguments
        productId = null;
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
        }

        if (productId != null) {
            repository.loadProduct(productId, new FirebaseRepository.SingleDataCallback<Product>() {
                @Override
                public void onSuccess(Product data) {
                    product = data;
                    bindProductDetails();
                    checkIfUserCanReview();
                }
                @Override
                public void onFailure(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
            repository.loadProductReviews(productId, new FirebaseRepository.DataCallback<Review>() {
                @Override
                public void onSuccess(List<Review> data) {
                    reviewList.clear();
                    reviewList.addAll(data);
                    reviewAdapter.notifyDataSetChanged();

                    // Calculate average rating
                    float avg = 0f;
                    if (!data.isEmpty()) {
                        int sum = 0;
                        for (Review r : data) sum += r.getRating();
                        avg = (float) sum / data.size();
                    }
                    textViewAverageRating.setText(String.format("(%.1f)", avg));
                    RatingBar ratingBar = requireView().findViewById(R.id.ratingBarProductDetail);
                    ratingBar.setRating(avg);
                }
                @Override
                public void onFailure(String error) {
                    // Optionally show a message or leave empty
                }
            });
        }

        buttonAddToCart.setOnClickListener(v -> {
            if (product == null) return;
            repository.addToCart(product, new FirebaseRepository.DataCallback<Void>() {
                @Override
                public void onSuccess(List<Void> data) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Added to Cart")
                            .setMessage("Product added to cart. Go to checkout?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                Navigation.findNavController(requireView())
                                        .navigate(R.id.action_productDetailFragment_to_cartFragment);
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                Toast.makeText(getContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
                            })
                            .show();
                }
                @Override
                public void onFailure(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        buttonWriteReview.setOnClickListener(v -> showReviewDialog());

        return view;
    }

    private void bindProductDetails() {
        if (product == null) return;
        textViewName.setText(product.getName());
        textViewBrand.setText(product.getBrand());
        textViewPrice.setText(product.getFormattedPrice());
        textViewDescription.setText(product.getDescription());

        // Images
        List<Banner> images = new ArrayList<>();
        if (product.getImageUrl() != null) {
            Banner banner = new Banner("", "", R.drawable.ic_placeholder);
            banner.setImageUrl(product.getImageUrl());
            images.add(banner);
        }
        imageAdapter = new BannerAdapter(images);
        viewPagerImages.setAdapter(imageAdapter);
    }

    private void checkIfUserCanReview() {
        if (currentUserId == null || product == null) {
            Toast.makeText(getContext(), "User or product is null", Toast.LENGTH_SHORT).show();
            return;
        }
        repository.loadUserOrders(new FirebaseRepository.DataCallback<Order>() {
            @Override
            public void onSuccess(List<Order> orders) {
                Toast.makeText(getContext(), "Orders loaded: " + orders.size(), Toast.LENGTH_SHORT).show();
                boolean hasBought = false;
                for (Order order : orders) {
                    Toast.makeText(getContext(), "Order: " + order.getStatus() + ", user: " + order.getUserId(), Toast.LENGTH_SHORT).show();
                    if ("Paid".equalsIgnoreCase(order.getStatus()) && order.getItems() != null) {
                        for (Map<String, Object> item : order.getItems()) {
                            Toast.makeText(getContext(), "Item id: " + item.get("id"), Toast.LENGTH_SHORT).show();
                            if (product.getId().equals(item.get("id"))) {
                                hasBought = true;
                                break;
                            }
                        }
                    }
                    if (hasBought) break;
                }
                if (hasBought) {
                    buttonWriteReview.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Failed to load orders: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showReviewDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_write_review, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        EditText editTextComment = dialogView.findViewById(R.id.editTextComment);

        new AlertDialog.Builder(requireContext())
                .setTitle("Write a Review")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    int rating = (int) ratingBar.getRating();
                    String comment = editTextComment.getText().toString().trim();
                    String userName = FirebaseAuth.getInstance().getCurrentUser() != null
                            ? FirebaseAuth.getInstance().getCurrentUser().getDisplayName() : "User";
                    Review review = new Review(userName, rating, comment);
                    review.setProductId(product.getId());
                    repository.addReview(review, new FirebaseRepository.DataCallback<Void>() {
                        @Override
                        public void onSuccess(List<Void> data) {
                            Toast.makeText(getContext(), "Review submitted!", Toast.LENGTH_SHORT).show();
                            // Reload reviews to update average rating
                            repository.loadProductReviews(product.getId(), new FirebaseRepository.DataCallback<Review>() {
                                @Override
                                public void onSuccess(List<Review> reviews) {
                                    if (reviews.isEmpty()) return;
                                    float sum = 0f;
                                    for (Review r : reviews) sum += r.getRating();
                                    float avg = sum / reviews.size();
                                    // Update product rating in Firestore
                                    product.setRating(avg);
                                    repository.updateProductRating(product.getId(), avg, new FirebaseRepository.DataCallback<Void>() {
                                        @Override
                                        public void onSuccess(List<Void> data) {
                                            // Optionally update UI or show a toast
                                        }
                                        @Override
                                        public void onFailure(String error) {
                                            // Optionally handle error
                                        }
                                    });
                                }
                                @Override
                                public void onFailure(String error) { }
                            });
                        }
                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(getContext(), "Failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}