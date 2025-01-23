package com.grocery.repository;

import com.grocery.model.Discounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discounts, Integer> {
    Optional<Discounts> findByCode(String code);

    @Query("SELECT d FROM Discounts d WHERE " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(d.discountId AS string) LIKE %:searchTerm% OR " +  // Fixing the discountId search
            "LOWER(d.expiryDate) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.status) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Discounts> searchDiscounts(String searchTerm);

    // New method to filter discounts by status
    @Query("SELECT d FROM Discounts d WHERE LOWER(d.status) = LOWER(:status)")
    List<Discounts> findByStatus(String status);

}
