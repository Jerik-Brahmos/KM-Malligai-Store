package com.grocery.service;

import com.grocery.exception.ResourceNotFoundException;
import com.grocery.model.DeliveryCharge;
import com.grocery.repository.DeliveryChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeliveryChargeService {

    @Autowired
    private DeliveryChargeRepository deliveryChargeRepository;

    public DeliveryCharge getDeliveryChargeEntity() {
        return deliveryChargeRepository.findTopByIsDeletedFalse()
                .orElse(new DeliveryCharge(0.0, false)); // ✅ Uses @RequiredArgsConstructor fields
    }



    public void updateDeliveryCharge(Double newCharge) {
        Optional<DeliveryCharge> currentChargeOptional = deliveryChargeRepository.findTopByIsDeletedFalse();

        currentChargeOptional.ifPresent(currentCharge -> {
            currentCharge.setDeleted(true); // Mark as deleted
            deliveryChargeRepository.save(currentCharge);
        });

        // Create a new charge with isDeleted as false
        DeliveryCharge deliveryCharge = new DeliveryCharge();
        deliveryCharge.setCharge(newCharge);
        deliveryCharge.setDeleted(false); // New charge is active
        deliveryChargeRepository.save(deliveryCharge);
    }

}
