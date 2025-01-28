package com.grocery.service;

import com.grocery.dto.CartItemResponse;
import com.grocery.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    public int getCartCount(String userId) {
        return cartRepository.countByUserId(userId);
    }

    @Cacheable(value = "cartItems", key = "#userId")
    public List<CartItemResponse> getCartItemsWithCache(String userId) {
        // Calls the repository method that uses the JOIN query
        return cartRepository.findCartItemsWithProductDetailsByUserId(userId);
    }
}
