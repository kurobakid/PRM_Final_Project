package com.example.finalproject.model;

public class Review {
    private String userName;
    private int rating;
    private String comment;

    public Review(String userName, int rating, String comment) {
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
    }

    public String getUserName() { return userName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
} 