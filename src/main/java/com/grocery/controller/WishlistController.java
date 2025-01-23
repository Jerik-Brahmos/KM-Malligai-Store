package com.grocery.controller;

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

        // Convert wishlist items to include product details
        return wishlistItems.stream().map(wishlistItem -> {
            Product product = productRepository.findById(wishlistItem.getProductId()).orElse(null);
            if (product != null) {
                return new WishlistItemResponse(
                        wishlistItem.getWishlistId(),
                        wishlistItem.getProductId() ,
                        product.getName(),
                        product.getPrice(),
                        product.getImageUrl(),
                        product.getCategory()
                );
            }
            return null;
        }).filter(response -> response != null).collect(Collectors.toList());

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
