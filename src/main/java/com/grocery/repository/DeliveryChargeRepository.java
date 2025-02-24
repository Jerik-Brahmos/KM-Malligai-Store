package com.grocery.repository;

import com.grocery.model.DeliveryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryChargeRepository extends JpaRepository<DeliveryCharge, Long> {
    Optional<DeliveryCharge> findTopByIsDeletedFalse();}
