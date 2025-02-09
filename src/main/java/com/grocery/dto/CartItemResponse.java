package com.grocery.dto;

import java.util.List;

public class CartItemResponse {

    private Long cartId;
    private String productName;
    private String productImageUrl;
    private Long productId;
    private int quantity;
    private List<ProductVariantResponse> variants; // Multiple grams & prices

    public CartItemResponse(Long cartId, String productName, String productImageUrl, Long productId, int quantity, List<ProductVariantResponse> variants) {
        this.cartId = cartId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.productId = productId;
        this.quantity = quantity;
        this.variants = variants;
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

    public List<ProductVariantResponse> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantResponse> variants) {
        this.variants = variants;
    }
}
