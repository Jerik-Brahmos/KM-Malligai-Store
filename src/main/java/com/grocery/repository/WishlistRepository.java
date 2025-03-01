package com.grocery.repository;

import com.grocery.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {

    // Fetch wishlist items by user ID, excluding items with soft-deleted products
    @Query("SELECT w FROM WishlistItem w JOIN w.product p WHERE w.userId = :userId AND p.isDeleted = false")
    List<WishlistItem> findByUserId(@Param("userId") String userId);

    // Count wishlist items for a user, excluding those with soft-deleted products
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(w) FROM WishlistItem w JOIN w.product p WHERE w.userId = :userId AND p.isDeleted = false")
    int countByUserId(@Param("userId") String userId);

    // Fetch a wishlist item by user ID and product ID, ensuring the product is not soft-deleted
    @Query("SELECT w FROM WishlistItem w JOIN w.product p WHERE w.userId = :userId AND w.productId = :productId AND p.isDeleted = false")
    Optional<WishlistItem> findByUserIdAndProductId(@Param("userId") String userId, @Param("productId") Long productId);
}