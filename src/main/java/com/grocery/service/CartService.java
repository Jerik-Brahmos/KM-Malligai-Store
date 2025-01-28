package com.grocery.service;

import com.grocery.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    public int getCartCount(String userId) {
        return cartRepository.countByUserId(userId);
    }
}
