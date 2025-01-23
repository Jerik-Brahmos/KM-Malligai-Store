package com.grocery.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate; // Will store the current date when the order is placed
    private Double totalAmount;
    private String status;

    @ManyToOne
    @JoinColumn(name = "shipping_id")
    private ShippingAddress shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItems> orderItems;

    @ManyToOne
    @JoinColumn(name = "delivery_charge_id")  // Join with the delivery charge ID
    private DeliveryCharge deliveryCharge;

    @PrePersist
    private void prePersist() {
        if (orderDate == null) {
            orderDate = new Date(); // Set the order date to the current date before saving
        }
    }

    public DeliveryCharge getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(DeliveryCharge deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public List<OrderItems> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItems> orderItems) {
        this.orderItems = orderItems;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}
