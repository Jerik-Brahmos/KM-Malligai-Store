package com.grocery.repository;

import com.grocery.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(String userId);

    CartItem findByUserIdAndProductIdAndVariantId(String userId, Long productId, Long variantId);

    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.userId = :userId")
    int countByUserId(@Param("userId") String userId);

    @Query("SELECT c.quantity FROM CartItem c WHERE c.userId = :userId AND c.variantId = :variantId")
    Optional<Integer> getCartQuantityByUserIdAndVariantId(String userId, Long variantId);
}
