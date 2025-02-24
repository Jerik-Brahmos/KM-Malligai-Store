package com.grocery.repository;


import com.grocery.model.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItems, Long> {

    public List<OrderItems> findByOrder_OrderId(Long orderId);

    @Query("SELECT oi.productId, SUM(oi.quantity) FROM OrderItems oi WHERE oi.productId IS NOT NULL GROUP BY oi.productId ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findBestSellingProducts();

    @Query("SELECT oi.variantId, SUM(oi.quantity) FROM OrderItems oi WHERE oi.variantId IS NOT NULL GROUP BY oi.variantId ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findBestSellingVariant();








}
