package com.grocery.service;

import com.grocery.model.Product;
import com.grocery.model.ProductVariant;
import com.grocery.model.WishlistItem;
import com.grocery.repository.ProductRepository;
import com.grocery.repository.ProductVariantRepository;
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

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    // Get all wishlist items for a user
    public List<WishlistItem> getWishlistByUserId(String userId) {
        return wishlistRepository.findByUserId(userId); // Repository filters soft-deleted products
    }

    // Add an item to the wishlist
    @Transactional
    public WishlistItem addToWishlist(WishlistItem wishlistItem) {
        // Validate that the product is not soft-deleted
        Optional<Product> productOpt = productRepository.findByProductIdAndIsDeletedFalse(wishlistItem.getProductId());
        if (!productOpt.isPresent()) {
            throw new IllegalArgumentException("Cannot add wishlist item: Product is soft-deleted or not found");
        }

        // Validate that the variant (if provided) is not soft-deleted
        if (wishlistItem.getVariantId() != null) {
            Optional<ProductVariant> variantOpt = productVariantRepository.findByVariantId(wishlistItem.getVariantId());
            if (!variantOpt.isPresent() || variantOpt.get().isDeleted()) {
                throw new IllegalArgumentException("Cannot add wishlist item: Variant is soft-deleted or not found");
            }
        }

        return wishlistRepository.save(wishlistItem);
    }

    // Remove an item from the wishlist
    @Transactional
    public void removeFromWishlist(Long wishlistId) {
        Optional<WishlistItem> wishlistItem = wishlistRepository.findById(wishlistId);
        if (wishlistItem.isPresent()) {
            wishlistRepository.delete(wishlistItem.get());
        } else {
            throw new RuntimeException("Wishlist item not found");
        }
    }

    public int getWishlistCount(String userId) {
        return wishlistRepository.countByUserId(userId); // Repository filters soft-deleted products
    }

    @Transactional
    public boolean updateVariant(String userId, Long productId, Long variantId) {
        Optional<WishlistItem> wishlistItem = wishlistRepository.findByUserIdAndProductId(userId, productId);

        if (wishlistItem.isPresent()) {
            // Validate that the variant is not soft-deleted
            Optional<ProductVariant> variantOpt = productVariantRepository.findByVariantId(variantId);
            if (!variantOpt.isPresent() || variantOpt.get().isDeleted()) {
                throw new IllegalArgumentException("Cannot update wishlist item: Variant is soft-deleted or not found");
            }

            WishlistItem item = wishlistItem.get();
            item.setVariantId(variantId);
            wishlistRepository.save(item);
            return true;
        }
        return false;
    }
}