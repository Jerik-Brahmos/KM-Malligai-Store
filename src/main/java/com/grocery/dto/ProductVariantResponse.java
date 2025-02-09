package com.grocery.dto;

public class ProductVariantResponse {
    private Long variantId;
    private String grams;
    private double price;

    public ProductVariantResponse() {
    }

    public ProductVariantResponse(Long variantId, String grams, double price) {
        this.variantId = variantId;
        this.grams = grams;
        this.price = price;
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
