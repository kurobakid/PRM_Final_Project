package com.example.finalproject.model;

import java.io.Serializable;

public class Address implements Serializable {
    private String id;
    private String userId;
    private String fullName;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private boolean isDefault;
    private String addressType; // home, work, other
    // For compatibility with UI code
    private String phone;
    private String address;
    private String zipCode;

    public Address() {
        // Required empty constructor for Firebase
    }

    public Address(String id, String userId, String fullName, String phoneNumber, 
                   String addressLine1, String addressLine2, String city, 
                   String state, String postalCode, String country, 
                   boolean isDefault, String addressType) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.isDefault = isDefault;
        this.addressType = addressType;
        // For compatibility
        this.phone = phoneNumber;
        this.address = addressLine1;
        this.zipCode = postalCode;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; this.phone = phoneNumber; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; this.address = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; this.zipCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }

    // Compatibility methods for UI code
    public String getPhone() { return phone != null ? phone : phoneNumber; }
    public void setPhone(String phone) { this.phone = phone; this.phoneNumber = phone; }

    public String getAddress() { return address != null ? address : addressLine1; }
    public void setAddress(String address) { this.address = address; this.addressLine1 = address; }

    public String getZipCode() { return zipCode != null ? zipCode : postalCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; this.postalCode = zipCode; }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(getAddress());
        if (addressLine2 != null && !addressLine2.isEmpty()) {
            sb.append(", ").append(addressLine2);
        }
        sb.append(", ").append(city);
        if (state != null && !state.isEmpty()) {
            sb.append(", ").append(state);
        }
        sb.append(" ").append(getZipCode());
        if (country != null && !country.isEmpty()) {
            sb.append(", ").append(country);
        }
        return sb.toString();
    }
} 