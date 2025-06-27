package com.example.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.model.Product;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {
    private List<Product> wishlistItems;
    private OnWishlistActionListener listener;

    public interface OnWishlistActionListener {
        void onAddToCart(Product product);
        void onRemoveFromWishlist(Product product, int position);
    }

    public WishlistAdapter(List<Product> wishlistItems, OnWishlistActionListener listener) {
        this.wishlistItems = wishlistItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        holder.bind(wishlistItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    class WishlistViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewProduct;
        private TextView textViewName, textViewBrand, textViewPrice;
        private RatingBar ratingBar;
        private Button buttonAddToCart, buttonRemove;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewWishlistProduct);
            textViewName = itemView.findViewById(R.id.textViewWishlistProductName);
            textViewBrand = itemView.findViewById(R.id.textViewWishlistProductBrand);
            textViewPrice = itemView.findViewById(R.id.textViewWishlistProductPrice);
            ratingBar = itemView.findViewById(R.id.ratingBarWishlistProduct);
            buttonAddToCart = itemView.findViewById(R.id.buttonWishlistAddToCart);
            buttonRemove = itemView.findViewById(R.id.buttonWishlistRemove);
        }

        public void bind(Product product, int position) {
            imageViewProduct.setImageResource(product.getImageResource());
            textViewName.setText(product.getName());
            textViewBrand.setText(product.getBrand());
            textViewPrice.setText(product.getFormattedPrice());
            ratingBar.setRating((float) product.getRating());

            buttonAddToCart.setOnClickListener(v -> {
                if (listener != null) listener.onAddToCart(product);
            });

            buttonRemove.setOnClickListener(v -> {
                if (listener != null) listener.onRemoveFromWishlist(product, position);
            });
        }
    }
} 