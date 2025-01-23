package com.grocery.service;

import com.grocery.model.DeliveryCharge;
import com.grocery.repository.DeliveryChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class DeliveryChargeService {

    @Autowired
    private DeliveryChargeRepository deliveryChargeRepository;

    public DeliveryCharge getDeliveryChargeEntity() {
        // Fetch the latest delivery charge that is not deleted
        return deliveryChargeRepository.findTopByIsDeletedFalse();
    }


    public void updateDeliveryCharge(Double newCharge) {
        // Mark the previous charge as deleted
        DeliveryCharge currentCharge = deliveryChargeRepository.findTopByIsDeletedFalse();
        if (currentCharge != null) {
            currentCharge.setDeleted(true); // Mark as deleted
            deliveryChargeRepository.save(currentCharge);
        }

        // Create a new charge with isDeleted as false
        DeliveryCharge deliveryCharge = new DeliveryCharge();
        deliveryCharge.setCharge(newCharge);
        deliveryCharge.setDeleted(false); // New charge is active
        deliveryChargeRepository.save(deliveryCharge);
    }
}
