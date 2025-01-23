package com.grocery.controller;

import com.grocery.model.ShippingAddress;
import com.grocery.model.User;
import com.grocery.repository.ShippingAddressRepository;
import com.grocery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CheckoutController {

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint to save a user's address
     */
    @PostMapping("/users/{userId}/address")
    public ResponseEntity<String> saveAddress(@PathVariable String userId, @RequestBody ShippingAddress address) {
        try {
            System.out.println("Received request for userId: " + userId); // Log userId

            // Fetch the user and set it to the address
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            address.setUser(user); // Set the user object (User entity) to address
            address.setUserId(userId); // Set userId for the address

            shippingAddressRepository.save(address); // Save address

            return ResponseEntity.ok("Address saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving address.");
        }
    }


    /**
     * Endpoint to update an existing address
     */
    @PutMapping("/users/address/{addressId}")
    public ResponseEntity<String> updateAddress(@PathVariable Integer addressId, @RequestBody ShippingAddress updatedAddress) {
        try {
            Optional<ShippingAddress> existingAddress = shippingAddressRepository.findById(addressId);
            if (existingAddress.isPresent()) {
                ShippingAddress address = existingAddress.get();
                address.setName(updatedAddress.getName());
                address.setAddress(updatedAddress.getAddress());
                address.setCity_district(updatedAddress.getCity_district());
                address.setState(updatedAddress.getState());
                address.setPincode(updatedAddress.getPincode());
                address.setAltPhoneNumber(updatedAddress.getAltPhoneNumber());
                address.setPhoneNumber(updatedAddress.getPhoneNumber());
                address.setLandmark(updatedAddress.getLandmark());
                shippingAddressRepository.save(address);
                return ResponseEntity.ok("Address updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Address not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating address.");
        }
    }

    /**
     * Endpoint to delete an address
     */
    @DeleteMapping("/users/address/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Integer addressId) {
        try {
            Optional<ShippingAddress> address = shippingAddressRepository.findById(addressId);
            if (address.isPresent()) {
                ShippingAddress shippingAddress = address.get();
                shippingAddress.setDeleted(true); // Soft delete the address
                shippingAddressRepository.save(shippingAddress); // Save the updated address
                return ResponseEntity.ok("Address deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Address not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting address.");
        }
    }

    /**
     * Endpoint to fetch all addresses of a user
     */
    @GetMapping("/users/{userId}/addresses")
    public ResponseEntity<List<ShippingAddress>> getUserAddresses(@PathVariable String userId) {
        try {
            List<ShippingAddress> addresses = shippingAddressRepository.findByUserUserIdAndIsDeletedFalse(userId);
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
