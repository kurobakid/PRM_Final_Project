package com.example.finalproject.ui.address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.finalproject.R;
import com.example.finalproject.model.Address;
import com.example.finalproject.utils.FirebaseAuthHelper;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddEditAddressFragment extends Fragment {
    private EditText etFullName, etPhone, etAddress, etCity, etState, etZipCode;
    private RadioGroup radioGroupAddressType;
    private RadioButton radioHome, radioWork, radioOther;
    private Button btnSaveAddress;
    private FirebaseFirestore db;
    private FirebaseAuthHelper authHelper;
    private Address editAddress; // null for new address, not null for editing

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_address, container, false);
        
        initViews(view);
        setupFirebase();
        setupListeners();
        
        // Check if we're editing an existing address
        if (getArguments() != null && getArguments().containsKey("address")) {
            editAddress = (Address) getArguments().getSerializable("address");
            populateFields();
        }
        
        return view;
    }
    
    private void initViews(View view) {
        etFullName = view.findViewById(R.id.etFullName);
        etPhone = view.findViewById(R.id.etPhone);
        etAddress = view.findViewById(R.id.etAddress);
        etCity = view.findViewById(R.id.etCity);
        etState = view.findViewById(R.id.etState);
        etZipCode = view.findViewById(R.id.etZipCode);
        radioGroupAddressType = view.findViewById(R.id.radioGroupAddressType);
        radioHome = view.findViewById(R.id.radioHome);
        radioWork = view.findViewById(R.id.radioWork);
        radioOther = view.findViewById(R.id.radioOther);
        btnSaveAddress = view.findViewById(R.id.btnSaveAddress);
    }
    
    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        authHelper = new FirebaseAuthHelper(requireActivity());
    }
    
    private void setupListeners() {
        btnSaveAddress.setOnClickListener(v -> saveAddress());
    }
    
    private void populateFields() {
        if (editAddress != null) {
            etFullName.setText(editAddress.getFullName());
            etPhone.setText(editAddress.getPhone());
            etAddress.setText(editAddress.getAddress());
            etCity.setText(editAddress.getCity());
            etState.setText(editAddress.getState());
            etZipCode.setText(editAddress.getZipCode());
            
            switch (editAddress.getAddressType()) {
                case "Home":
                    radioHome.setChecked(true);
                    break;
                case "Work":
                    radioWork.setChecked(true);
                    break;
                case "Other":
                    radioOther.setChecked(true);
                    break;
            }
        }
    }
    
    private void saveAddress() {
        if (!validateFields()) return;
        
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String zipCode = etZipCode.getText().toString().trim();
        
        String addressType = "Home";
        if (radioWork.isChecked()) addressType = "Work";
        else if (radioOther.isChecked()) addressType = "Other";
        
        FirebaseUser user = authHelper.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Map<String, Object> addressData = new HashMap<>();
        addressData.put("userId", user.getUid());
        addressData.put("fullName", fullName);
        addressData.put("phone", phone);
        addressData.put("address", address);
        addressData.put("city", city);
        addressData.put("state", state);
        addressData.put("zipCode", zipCode);
        addressData.put("addressType", addressType);
        
        if (editAddress != null) {
            // Update existing address
            db.collection("addresses")
                    .document(editAddress.getId())
                    .update(addressData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Address updated successfully", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigateUp();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to update address", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Add new address
            db.collection("addresses")
                    .add(addressData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Address added successfully", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigateUp();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to add address", Toast.LENGTH_SHORT).show();
                    });
        }
    }
    
    private boolean validateFields() {
        if (etFullName.getText().toString().trim().isEmpty()) {
            etFullName.setError("Full name is required");
            return false;
        }
        if (etPhone.getText().toString().trim().isEmpty()) {
            etPhone.setError("Phone number is required");
            return false;
        }
        if (etAddress.getText().toString().trim().isEmpty()) {
            etAddress.setError("Address is required");
            return false;
        }
        if (etCity.getText().toString().trim().isEmpty()) {
            etCity.setError("City is required");
            return false;
        }
        if (etState.getText().toString().trim().isEmpty()) {
            etState.setError("State is required");
            return false;
        }
        if (etZipCode.getText().toString().trim().isEmpty()) {
            etZipCode.setError("Zip code is required");
            return false;
        }
        return true;
    }
} 