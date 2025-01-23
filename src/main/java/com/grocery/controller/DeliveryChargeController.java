package com.grocery.controller;

import com.grocery.model.DeliveryCharge;
import com.grocery.service.DeliveryChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery-charge")
public class DeliveryChargeController {

    @Autowired
    private DeliveryChargeService deliveryChargeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDeliveryCharge() {
        try {
            // Fetch the latest delivery charge that is not deleted
            DeliveryCharge deliveryCharge = deliveryChargeService.getDeliveryChargeEntity();

            if (deliveryCharge != null) {
                // Return both deliveryChargeId and charge in a map
                Map<String, Object> response = new HashMap<>();
                response.put("deliveryChargeId", deliveryCharge.getDeliveryChargeId());
                response.put("charge", deliveryCharge.getCharge());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).body(null); // No delivery charge found
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Return error if delivery charge not found
        }
    }


    @PostMapping
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
