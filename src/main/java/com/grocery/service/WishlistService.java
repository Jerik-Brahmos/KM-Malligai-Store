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

    public boolean updateVariant(String userId, Long productId, Long variantId) {
        Optional<WishlistItem> wishlistItem = wishlistRepository.findByUserIdAndProductId(userId, productId);

        if (wishlistItem.isPresent()) {
            WishlistItem item = wishlistItem.get();
            item.setVariantId(variantId);
            wishlistRepository.save(item);
            return true;
        }
        return false;
    }
}
