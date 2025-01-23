package com.grocery.repository;

import com.grocery.model.DeliveryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryChargeRepository extends JpaRepository<DeliveryCharge, Long> {
    DeliveryCharge findTopByIsDeletedFalse(); // Fetch the latest non-deleted delivery charge
}
