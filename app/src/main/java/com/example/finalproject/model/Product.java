package com.example.finalproject.model;

import java.io.Serializable;

public class Product implements Serializable{
    private String id;
    private String name;
    private String brand;
    private double price;
    private double rating;
    private int imageResource;
    private String description;
    private int stockQuantity;

    public Product(String name, String brand, double price, double rating, int imageResource) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.rating = rating;
        this.imageResource = imageResource;
    }

    public Product(String id, String name, String brand, double price, double rating, int imageResource, String description, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.rating = rating;
        this.imageResource = imageResource;
        this.description = description;
        this.stockQuantity = stockQuantity;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getImageResource() { return imageResource; }
    public void setImageResource(int imageResource) { this.imageResource = imageResource; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }
} 