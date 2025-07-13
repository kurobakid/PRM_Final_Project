package com.example.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.model.Product;
import java.util.List;

public class ConfirmAdapter extends RecyclerView.Adapter<ConfirmAdapter.ConfirmViewHolder> {
    private List<Product> items;

    public ConfirmAdapter(List<Product> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ConfirmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_confirm, parent, false);
        return new ConfirmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmViewHolder holder, int position) {
        Product product = items.get(position);
        holder.textViewName.setText(product.getName());
        holder.textViewBrand.setText(product.getBrand());
        holder.textViewPrice.setText(product.getFormattedPrice());
        // If you have quantity, display it here as well
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ConfirmViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewBrand, textViewPrice;

        public ConfirmViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewConfirmProductName);
            textViewBrand = itemView.findViewById(R.id.textViewConfirmProductBrand);
            textViewPrice = itemView.findViewById(R.id.textViewConfirmProductPrice);
        }
    }
}