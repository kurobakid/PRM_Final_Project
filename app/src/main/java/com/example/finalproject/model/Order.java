package com.example.finalproject.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Order implements Serializable {
    private String id;
    private String orderId;
    private String userId;
    private String orderNumber;
    private Date date;
    private String status;
    private String paymentMethod;
    private double subtotal;
    private double tax;
    private double shipping;
    private double total;
    private Address shippingAddress;
    private List<Map<String, Object>> items;

    public Order() {
        // Default constructor for Firebase
    }

    public Order(String orderId, String status, String date, double total) {
        this.orderId = orderId;
        this.status = status;
        this.date = new Date(); // Convert string to Date
        this.total = total;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getShipping() { return shipping; }
    public void setShipping(double shipping) { this.shipping = shipping; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Address getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }

    public List<Map<String, Object>> getItems() { return items; }
    public void setItems(List<Map<String, Object>> items) { this.items = items; }
} 