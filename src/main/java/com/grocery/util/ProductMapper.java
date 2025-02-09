package com.grocery.util;

import com.grocery.dto.ProductDTO;
import com.grocery.dto.ProductVariantResponse;

import java.util.Collections;

public class ProductMapper {

    public static ProductDTO mapToProductDTO(Object[] result) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(((Number) result[0]).longValue()); // Safe cast for both BigInteger and Long
        productDTO.setName((String) result[1]);
        productDTO.setCategory((String) result[2]);
        productDTO.setImageUrl((String) result[3]);

        // Using constructor directly
        ProductVariantResponse variantDTO = new ProductVariantResponse(
                ((Number) result[4]).longValue(), // Variant ID
                (String) result[5],              // Grams
                ((Number) result[6]).doubleValue() // Price
        );

        productDTO.setVariants(Collections.singletonList(variantDTO));
        return productDTO;
    }
}
