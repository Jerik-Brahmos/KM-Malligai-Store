package com.grocery.dto;

public class CartItemResponse {

    private Long cartId;
    private String productName;
    private double productPrice;
    private String productImageUrl;
    private int quantity;
    private Long productId;
    private String productGram;

    public CartItemResponse(Long cartId, String productName, double productPrice, String productImageUrl, Long productId, int quantity, String productGram) {
        this.cartId = cartId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
        this.quantity = quantity;
        this.productId = productId;
        this.productGram = productGram;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // Getters and setters
    public Long getCartId() {
        return cartId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductGram() {
        return productGram;
    }

    public void setProductGram(String productGram) {
        this.productGram = productGram;
    }
}
