package com.grocery.service;

import com.grocery.dto.OrderDetails;
import com.grocery.model.DeliveryCharge;
import com.grocery.model.OrderItems;
import com.grocery.model.Orders;
import com.grocery.model.User;
import com.grocery.repository.DeliveryChargeRepository;
import com.grocery.repository.OrderItemRepository;
import com.grocery.repository.OrderRepository;
import com.grocery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdersService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrdersService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Autowired
    private DeliveryChargeRepository deliveryChargeRepository;

    @Autowired
    private UserRepository userRepository;

    // Place an order and save it to the database
    public boolean placeOrder(OrderDetails orderDetails) {
        try {
            DeliveryCharge deliveryCharge = deliveryChargeRepository.findById(orderDetails.getDeliveryChargeId())
                    .orElseThrow(() -> new RuntimeException("Delivery charge not found"));

            Orders order = new Orders();
            order.setUserId(orderDetails.getUser());
            order.setShippingAddress(orderDetails.getShippingAddress());
            order.setTotalAmount(orderDetails.getTotalAmount());
            order.setStatus("placed");
            order.setDeliveryCharge(deliveryCharge);

            Orders savedOrder = orderRepository.save(order);

            for (OrderDetails.OrderItem item : orderDetails.getItems()) {
                OrderItems orderItem = new OrderItems();
                orderItem.setOrder(savedOrder);
                orderItem.setProductId(item.getProductId());
                orderItem.setVariantId(item.getVariantId()); // 🔹 Storing variantId
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPricePerItem(item.getPrice());

                orderItemRepository.save(orderItem);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<OrderDetails> getAllOrderDetails() {
        // Fetch all orders with their associated order items
        List<Object[]> result = orderRepository.findOrderWithItems();

        if (result == null || result.isEmpty()) {
            throw new RuntimeException("No orders found in the database.");
        }

        List<OrderDetails> orderDetailsList = new ArrayList<>();

        for (Object[] row : result) {
            try {
                Orders order = (Orders) row[0];  // Order at index 0
                OrderItems item = (OrderItems) row[1];  // Order item at index 1

                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setOrderId(order.getOrderId());
                orderDetails.setStatus(order.getStatus());
                orderDetails.setCreatedAt(order.getOrderDate());
                orderDetails.setTotalAmount(order.getTotalAmount());
                orderDetails.setUser(order.getUserId());
                orderDetails.setShippingAddress(order.getShippingAddress());

                // Collect the order items
                List<OrderDetails.OrderItem> orderItems = new ArrayList<>();
                OrderDetails.OrderItem orderItem = new OrderDetails.OrderItem();

                orderItem.setProductId(item.getProductId());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getPricePerItem());
                orderItem.setProductName(item.getProduct().getName());
                orderItem.setImageUrl(item.getProduct().getImageUrl());  // Set image URL

                // ✅ Fetch and set the variant details
                if (item.getProductVariant() != null) {
                    orderItem.setVariantId(item.getProductVariant().getVariantId());
                    orderItem.setGrams(item.getProductVariant().getGrams());
                } else {
                    orderItem.setVariantId(0);  // Default value if no variant exists
                    orderItem.setGrams("N/A");
                }

                orderItems.add(orderItem);
                orderDetails.setItems(orderItems);

                orderDetailsList.add(orderDetails);

            } catch (Exception e) {
                e.printStackTrace();  // Log detailed error
            }
        }

        return orderDetailsList;
    }





    // Search orders by customer name or order ID
//    public List<Orders> searchOrders(String searchTerm) {
//        return orderRepository.findByCustomerNameContainingOrOrderIdContaining(searchTerm, searchTerm);
//    }

    // Get orders by their status
    public List<Orders> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    // Update order status
    public boolean updateOrderStatus(Long id, String status) {
        Optional<Orders> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Orders order = optionalOrder.get();
            order.setStatus(status);
            orderRepository.save(order);  // Save the updated order
            return true;
        }
        return false; // Return false if the order is not found
    }

    // Delete an order
    public boolean deleteOrder(Long id) {
        Optional<Orders> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            orderRepository.delete(optionalOrder.get()); // Delete the order
            return true;
        }
        return false; // Return false if the order is not found
    }

    public List<OrderDetails> getOrdersByUserId(String userId) {
        // Fetch the User from the userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch orders by User object
        List<Orders> orders = orderRepository.findByUserId(user);

        if (orders == null || orders.isEmpty()) {
            throw new RuntimeException("No orders found for this user.");
        }

        List<OrderDetails> orderDetailsList = new ArrayList<>();

        for (Orders order : orders) {
            try {
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setOrderId(order.getOrderId());
                orderDetails.setStatus(order.getStatus());
                orderDetails.setCreatedAt(order.getOrderDate());
                orderDetails.setTotalAmount(order.getTotalAmount());
                orderDetails.setUserId(order.getUserId().getUserId());  // Assuming the User entity has getUserId() method
                orderDetails.setShippingAddress(order.getShippingAddress());

                // Fetch associated order items (ensure the Order has a relationship to OrderItems)
                List<OrderDetails.OrderItem> orderItems = new ArrayList<>();
                for (OrderItems item : order.getOrderItems()) {  // Assuming order has a List<OrderItems> field
                    OrderDetails.OrderItem orderItem = new OrderDetails.OrderItem();
                    orderItem.setProductId(item.getProductId());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(item.getPricePerItem());
                    orderItem.setProductName(item.getProduct().getName());  // Assuming Product has getName() method
                    orderItem.setImageUrl(item.getProduct().getImageUrl());  // Assuming Product has getImageUrl() method

                    orderItems.add(orderItem);
                }

                // Set items in OrderDetails
                orderDetails.setItems(orderItems);

                // Add the populated OrderDetails to the list
                orderDetailsList.add(orderDetails);

            } catch (Exception e) {
                e.printStackTrace();  // Log error in case of issues while processing
            }
        }

        return orderDetailsList;
    }

    public long getOrderCount() {
        return orderRepository.count();
    }

    public double calculateTotalRevenue() {
        return orderRepository.sumRevenue();
    }

    public List<Orders> getTodaysOrders() {
        return orderRepository.findTodaysOrders();
    }

    public String generateCsvReport(List<Orders> orders) {
        StringWriter writer = new StringWriter();
        writer.append("Order ID, User, Order Date, Total Amount, Status, Product, Quantity\n");

        double totalRevenue = 0.0;

        // Group orders by Order ID
        Map<Long, List<Orders>> groupedOrders = orders.stream()
                .collect(Collectors.groupingBy(Orders::getOrderId));

        for (Map.Entry<Long, List<Orders>> entry : groupedOrders.entrySet()) {
            List<Orders> orderGroup = entry.getValue();
            Orders firstOrder = orderGroup.get(0); // Take first order as reference

            // Calculate total amount for this order ID
            double groupTotalAmount = orderGroup.stream()
                    .mapToDouble(Orders::getTotalAmount)
                    .sum();

            // Format order date
            String orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(firstOrder.getOrderDate());

            // Use the most recent status
            String status = orderGroup.stream()
                    .max(Comparator.comparing(Orders::getOrderDate))
                    .map(Orders::getStatus)
                    .orElse(firstOrder.getStatus());

            // Get all order items for this order ID
            List<OrderItems> allItems = orderGroup.stream()
                    .flatMap(order -> order.getOrderItems().stream())
                    .collect(Collectors.toList());

            // Write rows for each product
            for (int i = 0; i < allItems.size(); i++) {
                OrderItems item = allItems.get(i);
                String product = item.getProduct().getName() + " (" +
                        (item.getProductVariant() != null ? item.getProductVariant().getGrams() : "N/A") + ")";
                String quantity = "x" + item.getQuantity();

                if (i == 0) {
                    // First row for this Order ID - include all details
                    writer.append(firstOrder.getOrderId().toString()).append(",")
                            .append(firstOrder.getUserId().getDisplayName()).append(",")
                            .append(orderDate).append(",")
                            .append(String.valueOf(groupTotalAmount)).append(",")
                            .append(status).append(",");
                } else {
                    // Subsequent rows - leave first columns empty
                    writer.append(",,,,");
                }

                // Always append product and quantity
                writer.append(product).append(",")
                        .append(quantity).append("\n");
            }

            totalRevenue += groupTotalAmount;
        }

        writer.append("\nTotal Revenue: , , , , ").append(String.valueOf(totalRevenue)).append("\n");
        return writer.toString();
    }

}
