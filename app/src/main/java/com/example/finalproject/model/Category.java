package com.example.finalproject.model;

public class Category {
    private String id;
    private String name;
    private int iconResource;
    private String iconResourceName;

    // Default constructor required for Firebase
    public Category() {
    }

    public Category(String name, int iconResource) {
        this.name = name;
        this.iconResource = iconResource;
    }

    public Category(String id, String name, int iconResource) {
        this.id = id;
        this.name = name;
        this.iconResource = iconResource;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getIconResource() { return iconResource; }
    public void setIconResource(int iconResource) { this.iconResource = iconResource; }

    public String getIconResourceName() { return iconResourceName; }
    public void setIconResourceName(String iconResourceName) { this.iconResourceName = iconResourceName; }
} 