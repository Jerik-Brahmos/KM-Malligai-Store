package com.grocery.dto;

public class WishlistItemResponse {

    private Long wishlistId;  // Add wishlistId field
    private Long productId;
    private String name;
    private double price;
    private String imageUrl;
    private String category;

    // Constructors, getters, and setters
    public WishlistItemResponse(Long wishlistId, Long productId, String name, double price, String imageUrl, String category) {
        this.wishlistId = wishlistId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
