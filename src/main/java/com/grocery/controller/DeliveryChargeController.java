package com.grocery.controller;

import com.grocery.model.DeliveryCharge;
import com.grocery.service.DeliveryChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController  //  Use @RestController instead of @RequestMapping
@RequestMapping("/api")
public class DeliveryChargeController {

    @Autowired
    private DeliveryChargeService deliveryChargeService;

    @GetMapping("/delivery-charge") // Ensure correct endpoint
    public ResponseEntity<Map<String, Object>> getDeliveryCharge() {
        try {
            DeliveryCharge deliveryCharge = deliveryChargeService.getDeliveryChargeEntity();

            Map<String, Object> response = new HashMap<>();
            response.put("deliveryChargeId", deliveryCharge.getDeliveryChargeId());
            response.put("charge", deliveryCharge.getCharge());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }





    @PostMapping("/delivery-charge")
    public ResponseEntity<String> updateDeliveryCharge(@RequestBody DeliveryCharge deliveryCharge) {
        if (deliveryCharge.getCharge() < 0) {
            return ResponseEntity.badRequest().body("Invalid delivery charge value.");
        }

        // Update delivery charge
        try {
            deliveryChargeService.updateDeliveryCharge(deliveryCharge.getCharge());
            return ResponseEntity.ok("Delivery charge updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating delivery charge.");
        }
    }
}
