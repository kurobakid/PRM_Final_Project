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
import com.example.finalproject.utils.FirebaseRepository;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailFragment extends Fragment {
    private ViewPager2 viewPagerImages;
    private TextView textViewName, textViewBrand, textViewPrice, textViewDescription;
    private ImageView imageViewBack;
    private Button buttonAddToCart;
    private BannerAdapter imageAdapter;
    private FirebaseRepository repository;
    private Product product;

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

        repository = new FirebaseRepository();

        // Get productId from arguments
        String productId = null;
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
        }

        if (productId != null) {
            repository.loadProduct(productId, new FirebaseRepository.SingleDataCallback<Product>() {
                @Override
                public void onSuccess(Product data) {
                    product = data;
                    bindProductDetails();
                }
                @Override
                public void onFailure(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
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
}