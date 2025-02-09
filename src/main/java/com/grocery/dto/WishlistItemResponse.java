package com.grocery.dto;

import java.util.List;

public class WishlistItemResponse {

    private Long wishlistId;
    private Long productId;
    private String name;
    private String imageUrl;
    private String category;
    private List<ProductVariantResponse> variants; // Multiple grams & prices

    // Constructor
    public WishlistItemResponse(Long wishlistId, Long productId, String name, String imageUrl, String category, List<ProductVariantResponse> variants) {
        this.wishlistId = wishlistId;
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.category = category;
        this.variants = variants;
    }

    // Getters and Setters
    public Long getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(Long wishlistId) {
        this.wishlistId = wishlistId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<ProductVariantResponse> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantResponse> variants) {
        this.variants = variants;
    }
}
