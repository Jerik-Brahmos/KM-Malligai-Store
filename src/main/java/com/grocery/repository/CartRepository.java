package com.grocery.repository;

import com.grocery.dto.CartItemResponse;
import com.grocery.model.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(String userId);
    CartItem findByUserIdAndProductId(String userId, Long productId); // Custom query

    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.userId = :userId")
    int countByUserId(@Param("userId") String userId);

    @EntityGraph(value = "CartItem.product", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT c FROM CartItem c WHERE c.userId = :userId")
    List<CartItem> findCartItemsWithProductsByUserId(@Param("userId") String userId);




}
