package com.grocery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@Table(name = "delivery_charge")
public class DeliveryCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryChargeId;

    @Column(nullable = false)
    private Double charge;

    @Column(nullable = false)
    private boolean isDeleted;

    //  Custom constructor without 'deliveryChargeId'
    public DeliveryCharge(Double charge, boolean isDeleted) {
        this.charge = charge;
        this.isDeleted = isDeleted;
    }

    public DeliveryCharge() {

    }

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
