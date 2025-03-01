package com.grocery.repository;

import com.grocery.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {

    // Fetch cart items by user ID, excluding items with soft-deleted variants
    @Query("SELECT c FROM CartItem c JOIN c.productVariant pv WHERE c.userId = :userId AND pv.isDeleted = false")
    List<CartItem> findByUserId(@Param("userId") String userId);

    // Fetch a specific cart item, ensuring the variant is not soft-deleted
    @Query("SELECT c FROM CartItem c JOIN c.productVariant pv WHERE c.userId = :userId AND c.productId = :productId AND c.variantId = :variantId AND pv.isDeleted = false")
    CartItem findByUserIdAndProductIdAndVariantId(@Param("userId") String userId, @Param("productId") Long productId, @Param("variantId") Long variantId);

    // Count cart items for a user, excluding those with soft-deleted variants
    @Query("SELECT COUNT(c) FROM CartItem c JOIN c.productVariant pv WHERE c.userId = :userId AND pv.isDeleted = false")
    int countByUserId(@Param("userId") String userId);

    // Get quantity for a specific variant, ensuring it’s not soft-deleted
    @Query("SELECT c.quantity FROM CartItem c JOIN c.productVariant pv WHERE c.userId = :userId AND c.variantId = :variantId AND pv.isDeleted = false")
    Optional<Integer> getCartQuantityByUserIdAndVariantId(@Param("userId") String userId, @Param("variantId") Long variantId);
}