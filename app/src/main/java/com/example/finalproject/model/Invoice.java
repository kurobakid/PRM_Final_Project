package com.example.finalproject.model;

import java.util.Date;
import java.util.List;

public class Invoice {
    private String id;
    private String orderId;
    private Date invoiceDate;
    private Date dueDate;
    private String billingAddress;
    private String shippingAddress;
    private String paymentMethod;
    private double subtotal;
    private double tax;
    private double shipping;
    private double total;
    private String status;
    private List<InvoiceItem> items;

    public Invoice() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public Date getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(Date invoiceDate) { this.invoiceDate = invoiceDate; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
} 