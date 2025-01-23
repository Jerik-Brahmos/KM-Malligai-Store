package com.grocery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery_charge")
public class DeliveryCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryChargeId;  // Renamed id to deliveryChargeId

    @Column(nullable = false)
    private Double charge;

    @Column(nullable = false)
    private boolean isDeleted;

    public Long getDeliveryChargeId() {
        return deliveryChargeId;
    }

    public void setDeliveryChargeId(Long deliveryChargeId) {
        this.deliveryChargeId = deliveryChargeId;
    }

    public Double getCharge() {
        return charge;
    }

    public void setCharge(Double charge) {
        this.charge = charge;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
