package com.grocery.controller;

import com.grocery.dto.OrderDetails;
import com.grocery.model.Orders;
import com.grocery.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://grocery-shop-ee0ac.web.app", "https://grocery-shop-ee0ac.firebaseapp.com"})
@RequestMapping("/api/orders")
public class OrdersController {

    @Autowired
    private OrdersService orderService;

    // Endpoint to place an order
    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderDetails orderDetails) {
        try {
            if (orderDetails.getUser() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User details are missing.");
            }

            boolean isOrderPlaced = orderService.placeOrder(orderDetails);
            if (isOrderPlaced) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Order placed successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to place order.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error placing order: " + e.getMessage());
        }
    }

    // Endpoint to get all orders
    @GetMapping
    public ResponseEntity<List<OrderDetails>> getAllOrders() {
        try {
            List<OrderDetails> orderDetails = orderService.getAllOrderDetails();
            return ResponseEntity.ok(orderDetails);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDetails>> getOrdersByUserId(@PathVariable String userId) {
        try {
            List<OrderDetails> orderDetails = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orderDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint to search orders by customer name or order ID
//    @GetMapping("/search/{searchTerm}")
//    public ResponseEntity<List<Orders>> searchOrders(@PathVariable String searchTerm) {
//        try {
//            List<Orders> orders = orderService.searchOrders(searchTerm);
//            return ResponseEntity.ok(orders);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    // Endpoint to get orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Orders>> getOrdersByStatus(@PathVariable String status) {
        try {
            List<Orders> orders = orderService.getOrdersByStatus(status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint to update the order status
    @PutMapping("/{id}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long id, @RequestBody Orders order) {
        try {
            boolean isUpdated = orderService.updateOrderStatus(id, order.getStatus());
            if (isUpdated) {
                return ResponseEntity.ok("Order status updated successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update order status.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating order status: " + e.getMessage());
        }
    }

    // Endpoint to delete an order
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        try {
            boolean isDeleted = orderService.deleteOrder(id);
            if (isDeleted) {
                return ResponseEntity.ok("Order deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete order.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting order: " + e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getOrderCount() {
        long count = orderService.getOrderCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        double revenue = orderService.calculateTotalRevenue();
        return ResponseEntity.ok(revenue);
    }


}
