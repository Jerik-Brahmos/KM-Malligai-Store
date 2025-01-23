package com.grocery.dto;

import com.grocery.model.DeliveryCharge;
import com.grocery.model.OrderItems;
import com.grocery.model.User;
import com.grocery.model.ShippingAddress;

import java.util.Date;
import java.util.List;

public class OrderDetails {

    private long orderId; // Unique order identifier
    private User user; // User details (userId, name, etc.)
    private ShippingAddress shippingAddress; // Shipping address
    private List<OrderItem> items; // List of items in the order
    private double totalAmount; // Total order amount
    private String status; // Order status (e.g., placed, shipped)
    private Date createdAt; // Date when the order was created (optional)
    private String userId;  // Store userId as a String
    private long deliveryChargeId;

    // Getters and Setters
    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public long getDeliveryChargeId() {
        return deliveryChargeId;
    }

    public void setDeliveryChargeId(long deliveryChargeId) {
        this.deliveryChargeId = deliveryChargeId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    // OrderItem inner class (can be modified as required)
    public static class OrderItem {
        private long productId;
        private String productName; // Product name for admin view
        private int quantity;
        private double price;
        private String imageUrl; // New property for product image URL


        // Getters and Setters
        public long getProductId() {
            return productId;
        }

        public void setProductId(long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
