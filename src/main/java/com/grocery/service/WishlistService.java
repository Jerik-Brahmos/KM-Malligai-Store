package com.grocery.service;

import com.grocery.model.WishlistItem;
import com.grocery.repository.WishlistRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    // Get all wishlist items for a user
    @Cacheable(value = "wishlistItems", key = "#userId")
    public List<WishlistItem> getWishlistByUserId(String userId) {
        return wishlistRepository.findByUserId(userId);
    }

    // Add an item to the wishlist
    public WishlistItem addToWishlist(WishlistItem wishlistItem) {
        return wishlistRepository.save(wishlistItem);
    }

    // Remove an item from the wishlist
    public void removeFromWishlist(Long wishlistId) {
        Optional<WishlistItem> wishlistItem = wishlistRepository.findById(wishlistId);
        if (wishlistItem.isPresent()) {
            wishlistRepository.delete(wishlistItem.get());
        } else {
            throw new RuntimeException("Wishlist item not found");
        }
    }

    public int getWishlistCount(String userId) {
        return wishlistRepository.countByUserId(userId);
    }
}
