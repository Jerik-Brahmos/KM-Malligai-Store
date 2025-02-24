package com.grocery.dto;

import java.util.List;

public class CartItemResponse {

    private Long cartId;
    private String productName;
    private String productImageUrl;
    private Long productId;
    private int quantity;
    private Long variantId;
    private String grams;
    private double price;

    public CartItemResponse(Long cartId, String productName, String productImageUrl,
                            Long productId, int quantity, Long variantId,
                            String grams, Double price) {
        this.cartId = cartId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.productId = productId;
        this.quantity = quantity;
        this.variantId = variantId;
        this.grams = grams;
        this.price = price;
    }


    // Getters and Setters
    public Long getCartId() {
        return cartId;
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

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public String getGrams() {
        return grams;
    }

    public void setGrams(String grams) {
        this.grams = grams;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
