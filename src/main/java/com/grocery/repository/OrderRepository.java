package com.grocery.repository;

import com.grocery.dto.OrderDetails;
import com.grocery.model.OrderItems;
import com.grocery.model.Orders;
import com.grocery.model.User;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    // Custom query to search orders by customer name or order ID
//    List<Orders> findByNameContainingOrOrderIdContaining(String name, String orderId);

    // Custom query to fetch orders by status
    List<Orders> findByStatus(String status);


    @Query("SELECT o, oi FROM Orders o JOIN o.orderItems oi WHERE o.orderId IS NOT NULL")
    List<Object[]> findOrderWithItems();

    // Custom query to find orders by userId
    public List<Orders> findByUserId(User user);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Orders o WHERE o.status = 'delivered'")
    Double sumRevenue();


    @Query("SELECT o FROM Orders o WHERE DATE(o.orderDate) = DATE(:date)")
    List<Orders> findByOrderDate(Date date);

    @Query("SELECT o FROM Orders o WHERE DATE(o.orderDate) = CURRENT_DATE")
    List<Orders> findTodaysOrders();





}
