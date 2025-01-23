package com.grocery.controller;

import com.grocery.dto.CartItemResponse;
import com.grocery.exception.ResourceNotFoundException;
import com.grocery.model.CartItem;
import com.grocery.model.Product;
import com.grocery.repository.CartRepository;
import com.grocery.repository.ProductRepository;
import com.grocery.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    // Add or update a product in the cart
    @PostMapping
    public ResponseEntity<CartItem> addToCart(@RequestBody CartItem cartItem) {
        CartItem existingItem = cartRepository.findByUserIdAndProductId(cartItem.getUserId(), cartItem.getProductId());
        if (existingItem != null) {
            // If the product already exists in the cart, update quantity
            existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
            cartRepository.save(existingItem);
            return ResponseEntity.ok(existingItem); // Return the updated cart item
        } else {
            // Validate the product exists before adding
            productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + cartItem.getProductId()));
            cartRepository.save(cartItem);
            return ResponseEntity.ok(cartItem);
        }
    }

    // Get all cart items for a user
    @GetMapping("/{userId}")
    public List<CartItemResponse> getCartItems(@PathVariable String userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream().map(cartItem -> {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + cartItem.getProductId()));
            return new CartItemResponse(
                    cartItem.getCartId(),
                    product.getName(),
                    product.getPrice(),
                    product.getImageUrl(),
                    product.getProductId(),
                    cartItem.getQuantity(),
                    product.getGrams()
            );
        }).collect(Collectors.toList());
    }

    // Update quantity of a specific cart item
    @PutMapping("/{cartId}")
    public ResponseEntity<CartItem> updateCartQuantity(@PathVariable Long cartId, @RequestParam int quantity) {
        CartItem cartItem = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartId));
        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
        return ResponseEntity.ok(cartItem); // Return the updated cart item
    }

    // Remove item from the cart by cart ID
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> removeFromCartById(@PathVariable Long cartId) {
        CartItem cartItem = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartId));
        cartRepository.delete(cartItem);
        return ResponseEntity.ok().build(); // Return 200 OK if deletion is successful
    }

    // Remove a product from the cart by userId and productId
    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<String> deleteByUserIdAndProductId(@PathVariable String userId, @PathVariable Long productId) {
        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId, productId);
        if (cartItem != null) {
            cartRepository.delete(cartItem);
            return ResponseEntity.ok("Product removed from cart.");
        }
        throw new ResourceNotFoundException("Cart item not found for user ID: " + userId + " and product ID: " + productId);
    }

    // Get cart item by userId and productId
    @GetMapping("/{userId}/{productId}")
    public ResponseEntity<CartItem> getCartItem(@PathVariable String userId, @PathVariable Long productId) {
        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId, productId);
        if (cartItem != null) {
            return ResponseEntity.ok(cartItem);
        }
        throw new ResourceNotFoundException("Cart item not found for user ID: " + userId + " and product ID: " + productId);
    }

    // Update cart item quantity by userId and productId
    @PutMapping("/{userId}/{productId}")
    public ResponseEntity<String> updateCartQuantityByUserAndProduct(@PathVariable String userId, @PathVariable Long productId, @RequestBody CartItem cartItem) {
        CartItem existingCartItem = cartRepository.findByUserIdAndProductId(userId, productId);
        if (existingCartItem != null) {
            // Update the quantity if the product exists in the cart
            existingCartItem.setQuantity(cartItem.getQuantity());
            cartRepository.save(existingCartItem);
            return ResponseEntity.ok("Cart quantity updated.");
        }
        throw new ResourceNotFoundException("Cart item not found for user ID: " + userId + " and product ID: " + productId);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCartCount(@RequestParam String userId) {
        try {
            // Call the non-static method on the injected instance
            int count = cartService.getCartCount(userId);
            Map<String, Integer> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
