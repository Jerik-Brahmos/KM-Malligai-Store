package com.grocery.repository;

import com.grocery.model.Discounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountsRepository extends JpaRepository<Discounts, Integer> {
}
