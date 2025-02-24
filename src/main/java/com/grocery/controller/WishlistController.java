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

        // Fetch product and variant details
        Map<Long, Product> productMap = productRepository.findByProductIdIn(new ArrayList<>(productIds))
                .stream().collect(Collectors.toMap(Product::getProductId, product -> product));

        Map<Long, ProductVariant> variantMap = productVariantRepository.findByVariantIdIn(new ArrayList<>(variantIds))
                .stream().collect(Collectors.toMap(ProductVariant::getVariantId, variant -> variant));

        return wishlistItems.stream()
                .map(wishlistItem -> {
                    Product product = productMap.get(wishlistItem.getProductId());
                    if (product == null) return null;

                    List<ProductVariantResponse> variants = product.getVariants().stream()
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
    public WishlistItem addToWishlist(@RequestBody WishlistItem wishlistItem) {
        WishlistItem savedItem = wishlistService.addToWishlist(wishlistItem);
        return savedItem;
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

        boolean updated = wishlistService.updateVariant(userId, productId, variantId);

        if (updated) {
            return ResponseEntity.ok("Variant updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wishlist item not found");
        }
    }



}
