package com.grocery.service;

import com.grocery.model.CartItem;
import com.grocery.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Async
    public int getCartCount(String userId) {
        return cartRepository.countByUserId(userId);
    }

    @Cacheable(value = "cartItems", key = "#userId")
    public List<CartItem> getCartItemsByUserId(String userId) {
        return cartRepository.findByUserId(userId);
    }
}
