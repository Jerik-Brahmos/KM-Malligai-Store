package com.grocery.controller;

import com.grocery.dto.ProductVariantResponse;
import com.grocery.dto.WishlistItemResponse;
import com.grocery.model.ProductVariant;
import com.grocery.model.WishlistItem;
import com.grocery.model.Product;
import com.grocery.repository.ProductRepository;
import com.grocery.repository.ProductVariantRepository;
import com.grocery.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    // Get all wishlist items for a user with product details
    @GetMapping("/{userId}")
    public List<WishlistItemResponse> getWishlist(@PathVariable String userId) {
        List<WishlistItem> wishlistItems = wishlistService.getWishlistByUserId(userId);

        if (wishlistItems.isEmpty()) {
            return Collections.emptyList();
        }

        // DEBUG: Print wishlist items and their variant IDs
        for (WishlistItem item : wishlistItems) {
            System.out.println("WishlistItem ID: " + item.getWishlistId() + ", Variant ID: " + item.getVariantId());
        }

        // Extract product and variant IDs
        Set<Long> productIds = new HashSet<>();
        Set<Long> variantIds = new HashSet<>();

        for (WishlistItem item : wishlistItems) {
            productIds.add(item.getProductId());
            if (item.getVariantId() != null) { // Avoid null values
                variantIds.add(item.getVariantId());
            }
        }

        // Fetch product and variant details, excluding soft-deleted ones
        Map<Long, Product> productMap = productRepository.findByProductIdIn(new ArrayList<>(productIds))
                .stream()
                .filter(product -> !product.isDeleted()) // Filter out soft-deleted products
                .collect(Collectors.toMap(Product::getProductId, product -> product));

        Map<Long, ProductVariant> variantMap = productVariantRepository.findByVariantIdIn(new ArrayList<>(variantIds))
                .stream()
                .filter(variant -> !variant.isDeleted()) // Filter out soft-deleted variants
                .collect(Collectors.toMap(ProductVariant::getVariantId, variant -> variant));

        return wishlistItems.stream()
                .map(wishlistItem -> {
                    Product product = productMap.get(wishlistItem.getProductId());
                    if (product == null) return null; // Skip if product is soft-deleted or not found

                    // Filter out soft-deleted variants from the product's variant list
                    List<ProductVariantResponse> variants = product.getVariants().stream()
                            .filter(variant -> !variant.isDeleted()) // Ensure only active variants
                            .map(variant -> new ProductVariantResponse(
                                    variant.getVariantId(),
                                    variant.getGrams(),
                                    variant.getPrice()))
                            .collect(Collectors.toList());

                    System.out.println("Selected Variant for Wishlist ID " + wishlistItem.getWishlistId() + ": " + wishlistItem.getVariantId());

                    return new WishlistItemResponse(
                            wishlistItem.getWishlistId(),
                            wishlistItem.getProductId(),
                            product.getName(),
                            product.getImageUrl(),
                            product.getCategory(),
                            variants,
                            wishlistItem.getVariantId() // Ensure this is not null
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Add an item to the wishlist
    @PostMapping
    public ResponseEntity<WishlistItem> addToWishlist(@RequestBody WishlistItem wishlistItem) {
        // Validate that the product and variant (if provided) are not soft-deleted
        Optional<Product> productOpt = productRepository.findByProductIdAndIsDeletedFalse(wishlistItem.getProductId());
        if (!productOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Product is soft-deleted or not found
        }

        if (wishlistItem.getVariantId() != null) {
            Optional<ProductVariant> variantOpt = productVariantRepository.findByVariantId(wishlistItem.getVariantId());
            if (!variantOpt.isPresent() || variantOpt.get().isDeleted()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Variant is soft-deleted or not found
            }
        }

        WishlistItem savedItem = wishlistService.addToWishlist(wishlistItem);
        return ResponseEntity.ok(savedItem);
    }

    // Remove an item from the wishlist
    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long wishlistId) {
        try {
            wishlistService.removeFromWishlist(wishlistId);
            return ResponseEntity.ok("Item removed from wishlist");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing item from wishlist");
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getWishlistCount(@RequestParam String userId) {
        try {
            int count = wishlistService.getWishlistCount(userId);
            Map<String, Integer> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update-variant")
    public ResponseEntity<String> updateWishlistVariant(
            @RequestParam String userId,
            @RequestBody Map<String, Long> requestBody) {
        Long productId = requestBody.get("productId");
        Long variantId = requestBody.get("variantId");

        if (productId == null || variantId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing productId or variantId");
        }

        // Validate that the product and variant are not soft-deleted
        Optional<Product> productOpt = productRepository.findByProductIdAndIsDeletedFalse(productId);
        if (!productOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product is soft-deleted or not found");
        }

        Optional<ProductVariant> variantOpt = productVariantRepository.findByVariantId(variantId);
        if (!variantOpt.isPresent() || variantOpt.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Variant is soft-deleted or not found");
        }

        boolean updated = wishlistService.updateVariant(userId, productId, variantId);
        if (updated) {
            return ResponseEntity.ok("Variant updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wishlist item not found");
        }
    }
}