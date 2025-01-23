package com.grocery.repository;

import com.grocery.model.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Integer> {

    // Find all non-deleted addresses for a specific user
    List<ShippingAddress> findByUserUserIdAndIsDeletedFalse(String userId);

    // Find a specific address by userId and shippingId
    Optional<ShippingAddress> findByUserUserIdAndShippingIdAndIsDeletedFalse(String userId, Integer shippingId);

    // Custom query to find addresses by city or district (only non-deleted)
    @Query("SELECT s FROM ShippingAddress s WHERE s.city_district = ?1 AND s.isDeleted = false")
    List<ShippingAddress> findByCityDistrict(String cityDistrict);

    // Method to perform soft deletion (mark the address as deleted)
    void deleteByShippingIdAndIsDeletedFalse(Integer shippingId); // Soft delete by setting flag to true
}
