package com.grocery.controller;

import com.grocery.model.Discounts;
import com.grocery.repository.DiscountRepository;
import com.grocery.service.DiscountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private DiscountsService discountService;


    // Search discounts by code, id, expiryDate, or status
    @GetMapping("/search")
    public ResponseEntity<List<Discounts>> searchDiscounts(@RequestParam String searchTerm) {
        List<Discounts> discounts = discountRepository.searchDiscounts(searchTerm);
        return ResponseEntity.ok(discounts);
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getDiscountDetails(@PathVariable String code) {
        Optional<Discounts> discount = discountRepository.findByCode(code);
        if (discount.isPresent()) {
            return ResponseEntity.ok(discount.get());
        } else {
            return ResponseEntity.status(404).body("Promo code not found.");
        }
    }

    @GetMapping("/discounts")
    public ResponseEntity<List<Discounts>> getAllDiscounts() {
        List<Discounts> discounts = discountRepository.findAll();
        return ResponseEntity.ok(discounts);
    }

    // Edit discount details
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscount(@PathVariable int id, @RequestBody Discounts discountDetails) {
        Optional<Discounts> existingDiscount = discountRepository.findById(id);
        if (existingDiscount.isPresent()) {
            Discounts discount = existingDiscount.get();
            discount.setCode(discountDetails.getCode());
            discount.setDiscountPercentage(discountDetails.getDiscountPercentage());
            discount.setExpiryDate(discountDetails.getExpiryDate());
            discount.setFixedDiscount(discountDetails.getFixedDiscount());
            discount.setStatus(discountDetails.getStatus());
            discountRepository.save(discount);
            return ResponseEntity.ok(discount);
        } else {
            return ResponseEntity.status(404).body("Discount not found.");
        }
    }

    // Delete discount by id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiscount(@PathVariable int id) {
        Optional<Discounts> existingDiscount = discountRepository.findById(id);
        if (existingDiscount.isPresent()) {
            discountRepository.deleteById(id);
            return ResponseEntity.ok("Discount deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Discount not found.");
        }
    }

    @PostMapping("/add_discount")
    public ResponseEntity<?> addDiscount(@RequestBody Discounts discount) {
        Discounts savedDiscount = discountRepository.save(discount);
        return ResponseEntity.ok(savedDiscount);
    }

    // Fetch discounts by status
    @GetMapping("/filter-by-status")
    public List<Discounts> getDiscountsByStatus(@RequestParam String status) {
        return discountService.getDiscountsByStatus(status);

    }


}
