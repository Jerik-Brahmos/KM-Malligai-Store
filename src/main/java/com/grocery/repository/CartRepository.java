package com.grocery.repository;

import com.grocery.dto.CartItemResponse;
import com.grocery.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(String userId);
    CartItem findByUserIdAndProductId(String userId, Long productId); // Custom query

    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.userId = :userId")
    int countByUserId(@Param("userId") String userId);

    @Query("SELECT new com.grocery.dto.CartItemResponse(c.cartId, p.name, p.price, p.imageUrl, p.productId, c.quantity, p.grams) " +
            "FROM CartItem c JOIN Product p ON c.productId = p.productId WHERE c.userId = :userId")
    List<CartItemResponse> findCartItemsWithProductDetailsByUserId(@Param("userId") String userId);

}
