package com.example.finalproject.ui.address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.adapter.AddressAdapter;
import com.example.finalproject.model.Address;
import com.example.finalproject.utils.FirebaseAuthHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AddressBookFragment extends Fragment {
    private RecyclerView recyclerViewAddresses;
    private FloatingActionButton fabAddAddress;
    private AddressAdapter addressAdapter;
    private List<Address> addressList;
    private FirebaseFirestore db;
    private FirebaseAuthHelper authHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_book, container, false);
        
        recyclerViewAddresses = view.findViewById(R.id.recyclerViewAddresses);
        fabAddAddress = view.findViewById(R.id.fabAddAddress);
        
        db = FirebaseFirestore.getInstance();
        authHelper = new FirebaseAuthHelper(requireActivity());
        addressList = new ArrayList<>();
        
        setupRecyclerView();
        loadAddresses();
        
        fabAddAddress.setOnClickListener(v -> {
            // Navigate to AddEditAddressFragment for new address
            Bundle bundle = new Bundle();
            // No address passed means we're adding a new one
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_addressBookFragment_to_addEditAddressFragment, bundle);
        });
        
        return view;
    }
    
    private void setupRecyclerView() {
        addressAdapter = new AddressAdapter(addressList, address -> {
            // Handle address selection
            Toast.makeText(getContext(), "Selected: " + address.getFullName(), Toast.LENGTH_SHORT).show();
        }, address -> {
            // Handle address edit
            Bundle bundle = new Bundle();
            bundle.putSerializable("address", address);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_addressBookFragment_to_addEditAddressFragment, bundle);
        }, address -> {
            // Handle address delete
            deleteAddress(address);
        });
        
        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAddresses.setAdapter(addressAdapter);
    }
    
    private void loadAddresses() {
        FirebaseUser user = authHelper.getCurrentUser();
        if (user == null) return;
        
        db.collection("addresses")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    addressList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Address address = document.toObject(Address.class);
                        if (address != null) {
                            address.setId(document.getId());
                            addressList.add(address);
                        }
                    }
                    addressAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load addresses", Toast.LENGTH_SHORT).show();
                });
    }
    
    private void deleteAddress(Address address) {
        db.collection("addresses")
                .document(address.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    addressList.remove(address);
                    addressAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Address deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete address", Toast.LENGTH_SHORT).show();
                });
    }
} 