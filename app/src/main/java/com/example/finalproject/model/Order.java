package com.example.finalproject.model;

public class Order {
    private String orderId;
    private String status;
    private String date;
    private double total;

    public Order(String orderId, String status, String date, double total) {
        this.orderId = orderId;
        this.status = status;
        this.date = date;
        this.total = total;
    }

    public String getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
    public double getTotal() { return total; }
} 