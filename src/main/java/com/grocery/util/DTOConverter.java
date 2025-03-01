package com.grocery.util;

import com.grocery.dto.ProductDTO;
import com.grocery.dto.ProductVariantResponse;
import com.grocery.model.Product;
import com.grocery.model.ProductVariant;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DTOConverter {

    public static ProductDTO convertToProductDTO(Product product) {
        return new ProductDTO(
                product.getProductId(),
                product.getName(),
                product.getCategory(),
                product.getImageUrl(),
                (product.getVariants() != null) ? product.getVariants().stream()
                        .filter(variant -> !variant.isDeleted()) // Filter out soft-deleted variants
                        .map(DTOConverter::convertToProductVariantResponse)
                        .collect(Collectors.toList())
                        : Collections.emptyList() // Prevents null list issue
        );
    }

    private static ProductVariantResponse convertToProductVariantResponse(ProductVariant variant) {
        return new ProductVariantResponse(
                variant.getVariantId(),
                variant.getGrams(),
                variant.getPrice()
        );
    }
}