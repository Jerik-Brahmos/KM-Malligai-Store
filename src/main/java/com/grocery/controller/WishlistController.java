package com.grocery.controller;

import com.grocery.dto.ProductVariantResponse;
import com.grocery.dto.WishlistItemResponse;
import com.grocery.model.WishlistItem;
import com.grocery.model.Product;
import com.grocery.repository.ProductRepository;
import com.grocery.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private ProductRepository productRepository; // Inject ProductRepository to fetch product details

    // Get all wishlist items for a user with product details
    @GetMapping("/{userId}")
    public List<WishlistItemResponse> getWishlist(@PathVariable String userId) {
        List<WishlistItem> wishlistItems = wishlistService.getWishlistByUserId(userId);

        // Extract product IDs from wishlist items
        List<Long> productIds = wishlistItems.stream()
                .map(WishlistItem::getProductId)
                .distinct()
                .collect(Collectors.toList());

        // Fetch all products in a single query
        Map<Long, Product> productMap = productRepository.findByProductIdIn(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getProductId, product -> product));

        // Convert wishlist items to response
        return wishlistItems.stream()
                .map(wishlistItem -> {
                    Product product = productMap.get(wishlistItem.getProductId());
                    if (product == null) {
                        return null;
                    }

                    // Fetch product variants (grams & prices)
                    List<ProductVariantResponse> variants = product.getVariants().stream()
                            .map(variant -> new ProductVariantResponse(variant.getVariantId(), variant.getGrams(), variant.getPrice()))
                            .collect(Collectors.toList());

                    return new WishlistItemResponse(
                            wishlistItem.getWishlistId(),
                            wishlistItem.getProductId(),
                            product.getName(),
                            product.getImageUrl(),
                            product.getCategory(),
                            variants  // Multiple grams & prices
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


}
