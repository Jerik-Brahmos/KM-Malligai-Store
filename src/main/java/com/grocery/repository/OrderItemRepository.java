package com.grocery.repository;


import com.grocery.dto.ProductDTO;
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


    @Query("""
    SELECT new com.grocery.dto.ProductDTO(p.name, p.price, p.category, p.grams, p.imageUrl) 
    FROM OrderItems oi
    JOIN Product p ON oi.productId = p.productId
    WHERE p.isDeleted = false
    GROUP BY p.productId
    ORDER BY SUM(oi.quantity) DESC
""")
    List<ProductDTO> findBestSellingProductDetails();





}
