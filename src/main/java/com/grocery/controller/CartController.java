package com.grocery.controller;

import com.grocery.dto.CartItemResponse;
import com.grocery.dto.ProductVariantResponse;
import com.grocery.exception.ResourceNotFoundException;
import com.grocery.model.CartItem;
import com.grocery.model.Product;
import com.grocery.model.ProductVariant;
import com.grocery.repository.CartRepository;
import com.grocery.repository.ProductRepository;
import com.grocery.repository.ProductVariantRepository;
import com.grocery.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private ProductVariantRepository productVariantRepository;

    // Add or update a product in the cart
    @PostMapping
    public ResponseEntity<CartItem> addToCart(@RequestBody CartItem cartItem) {
        CartItem existingItem = cartRepository.findByUserIdAndProductIdAndVariantId(
                cartItem.getUserId(), cartItem.getProductId(), cartItem.getVariantId()
        );

        if (existingItem != null) {
            // If the product with the same variant exists, update the quantity
            existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
            cartRepository.save(existingItem);
            return ResponseEntity.ok(existingItem);
        } else {
            // Validate the product exists
            productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + cartItem.getProductId()));

            // Validate the variant exists
            productVariantRepository.findById(cartItem.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found with ID: " + cartItem.getVariantId()));

            cartRepository.save(cartItem);
            return ResponseEntity.ok(cartItem);
        }
    }


    // Get all cart items for a user
    @GetMapping("/{userId}")
    public List<CartItemResponse> getCartItems(@PathVariable String userId) {
        // Fetch all cart items for the user
        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);

        // Extract product and variant IDs
        List<Long> productIds = cartItems.stream().map(CartItem::getProductId).distinct().collect(Collectors.toList());
        List<Long> variantIds = cartItems.stream().map(CartItem::getVariantId).distinct().collect(Collectors.toList());

        // Fetch all products and variants in a single query
        Map<Long, Product> productMap = productRepository.findByProductIdIn(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getProductId, product -> product));

        Map<Long, ProductVariant> variantMap = productVariantRepository.findByVariantIdIn(variantIds)
                .stream()
                .collect(Collectors.toMap(ProductVariant::getVariantId, variant -> variant));

        // Convert to response
        return cartItems.stream()
                .map(cartItem -> {
                    Product product = productMap.get(cartItem.getProductId());
                    ProductVariant variant = variantMap.get(cartItem.getVariantId());

                    if (product == null) {
                        throw new ResourceNotFoundException("Product not found with ID: " + cartItem.getProductId());
                    }
                    if (variant == null) {
                        throw new ResourceNotFoundException("Variant not found with ID: " + cartItem.getVariantId());
                    }

                    return new CartItemResponse(
                            cartItem.getCartId(),
                            product.getName(),
                            product.getImageUrl(),
                            cartItem.getProductId(),
                            cartItem.getQuantity(),
                            cartItem.getVariantId(),
                            variant.getGrams(),
                            variant.getPrice()
                    );
                }).collect(Collectors.toList());
    }



    // Update quantity of a specific cart item
    @CacheEvict(value = "products", allEntries = true)
    @Transactional // Ensure transaction completes before cache eviction
    @PutMapping("/{cartId}")
    public ResponseEntity<CartItem> updateCartQuantity(@PathVariable Long cartId, @RequestParam int quantity) {
        CartItem cartItem = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartId));

        cartItem.setQuantity(quantity);
        CartItem updatedCartItem = cartRepository.save(cartItem);

        return ResponseEntity.ok(updatedCartItem); // Return updated cart item
    }

    // Remove item from the cart by cart ID
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> removeFromCartById(@PathVariable Long cartId) {
        CartItem cartItem = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartId));
        cartRepository.delete(cartItem);
        return ResponseEntity.ok().build(); // Return 200 OK if deletion is successful
    }



    // Get cart item by userId and productId
    @GetMapping("/{userId}/{productId}/{variantId}")
    public ResponseEntity<CartItem> getCartItem(
            @PathVariable String userId,
            @PathVariable Long productId,
            @PathVariable Long variantId) {

        CartItem cartItem = cartRepository.findByUserIdAndProductIdAndVariantId(userId, productId, variantId);

        if (cartItem != null) {
            return ResponseEntity.ok(cartItem);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null); // Return a 404 response instead of throwing an exception
    }


    // Remove a product variant from the cart
    @DeleteMapping("/{userId}/{productId}/{variantId}")
    public ResponseEntity<String> deleteByUserIdAndProductIdAndVariantId(
            @PathVariable String userId,
            @PathVariable Long productId,
            @PathVariable Long variantId) {

        CartItem cartItem = cartRepository.findByUserIdAndProductIdAndVariantId(userId, productId, variantId);

        if (cartItem != null) {
            cartRepository.delete(cartItem);
            return ResponseEntity.ok("Product variant removed from cart.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Cart item not found for user ID: " + userId + ", product ID: " + productId + ", variant ID: " + variantId);
    }

    // Update cart item quantity by userId, productId, and variantId
    @PutMapping("/{userId}/{productId}/{variantId}")
    public ResponseEntity<String> updateCartQuantity(
            @PathVariable String userId,
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @RequestBody CartItem cartItem) {

        CartItem existingCartItem = cartRepository.findByUserIdAndProductIdAndVariantId(userId, productId, variantId);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(cartItem.getQuantity());
            cartRepository.save(existingCartItem);
            return ResponseEntity.ok("Cart quantity updated.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Cart item not found for user ID: " + userId + ", product ID: " + productId + ", variant ID: " + variantId);
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

    @GetMapping("/quantity/{userId}/{variantId}")
    public int getCartQuantityByVariant(@PathVariable String userId, @PathVariable Long variantId) {
        return cartService.getCartQuantityByVariant(userId, variantId);
    }

}
