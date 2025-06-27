package com.example.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.model.InvoiceItem;
import java.util.List;

public class InvoiceItemAdapter extends RecyclerView.Adapter<InvoiceItemAdapter.InvoiceItemViewHolder> {
    private List<InvoiceItem> invoiceItems;

    public InvoiceItemAdapter(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    @NonNull
    @Override
    public InvoiceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invoice_item, parent, false);
        return new InvoiceItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceItemViewHolder holder, int position) {
        InvoiceItem item = invoiceItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return invoiceItems.size();
    }

    static class InvoiceItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewProductName;
        private TextView textViewQuantity;
        private TextView textViewUnitPrice;
        private TextView textViewTotalPrice;

        public InvoiceItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewUnitPrice = itemView.findViewById(R.id.textViewUnitPrice);
            textViewTotalPrice = itemView.findViewById(R.id.textViewTotalPrice);
        }

        public void bind(InvoiceItem item) {
            textViewProductName.setText(item.getProductName());
            textViewQuantity.setText("Qty: " + item.getQuantity());
            textViewUnitPrice.setText(String.format("$%.2f", item.getUnitPrice()));
            textViewTotalPrice.setText(String.format("$%.2f", item.getTotalPrice()));
        }
    }
} 