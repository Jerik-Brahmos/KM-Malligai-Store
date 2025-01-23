package com.grocery.service;

import com.grocery.model.Discounts;
import com.grocery.repository.DiscountRepository;
import com.grocery.repository.DiscountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiscountsService {

    @Autowired
    private DiscountRepository discountsRepository;

    @Scheduled(fixedRate = 86400000) // Runs every 24 hours
    public void updateExpiredDiscounts() {
        LocalDate today = LocalDate.now();
        discountsRepository.findAll().forEach(discount -> {
            if (discount.getExpiryDate() != null) {
                LocalDate expiry = LocalDate.parse(discount.getExpiryDate());
                if (expiry.isBefore(today) && discount.getStatus().equalsIgnoreCase("active")) {
                    discount.setStatus("expired");
                    discountsRepository.save(discount);
                }
            }
        });
    }

    // Fetch discounts by status
    public List<Discounts> getDiscountsByStatus(String status) {
        return discountsRepository.findByStatus(status);
    }

}
