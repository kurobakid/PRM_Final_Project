package com.example.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.model.Address;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<Address> addressList;
    private OnAddressClickListener onAddressClickListener;
    private OnAddressEditClickListener onAddressEditClickListener;
    private OnAddressDeleteClickListener onAddressDeleteClickListener;

    public interface OnAddressClickListener {
        void onAddressClick(Address address);
    }

    public interface OnAddressEditClickListener {
        void onAddressEditClick(Address address);
    }

    public interface OnAddressDeleteClickListener {
        void onAddressDeleteClick(Address address);
    }

    public AddressAdapter(List<Address> addressList, OnAddressClickListener onAddressClickListener,
                         OnAddressEditClickListener onAddressEditClickListener,
                         OnAddressDeleteClickListener onAddressDeleteClickListener) {
        this.addressList = addressList;
        this.onAddressClickListener = onAddressClickListener;
        this.onAddressEditClickListener = onAddressEditClickListener;
        this.onAddressDeleteClickListener = onAddressDeleteClickListener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.bind(address);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewPhone;
        private TextView textViewAddress;
        private TextView textViewType;
        private TextView textViewDefault;
        private ImageButton buttonEdit;
        private ImageButton buttonDelete;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewAddressName);
            textViewPhone = itemView.findViewById(R.id.textViewAddressPhone);
            textViewAddress = itemView.findViewById(R.id.textViewAddressFull);
            textViewType = itemView.findViewById(R.id.textViewAddressType);
            textViewDefault = itemView.findViewById(R.id.textViewAddressDefault);
            buttonEdit = itemView.findViewById(R.id.buttonEditAddress);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteAddress);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onAddressClickListener != null) {
                    onAddressClickListener.onAddressClick(addressList.get(position));
                }
            });

            buttonEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onAddressEditClickListener != null) {
                    onAddressEditClickListener.onAddressEditClick(addressList.get(position));
                }
            });

            buttonDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onAddressDeleteClickListener != null) {
                    onAddressDeleteClickListener.onAddressDeleteClick(addressList.get(position));
                }
            });
        }

        public void bind(Address address) {
            textViewName.setText(address.getFullName());
            textViewPhone.setText(address.getPhoneNumber());
            textViewAddress.setText(address.getFullAddress());
            textViewType.setText(address.getAddressType());
            
            if (address.isDefault()) {
                textViewDefault.setVisibility(View.VISIBLE);
            } else {
                textViewDefault.setVisibility(View.GONE);
            }
        }
    }
} 