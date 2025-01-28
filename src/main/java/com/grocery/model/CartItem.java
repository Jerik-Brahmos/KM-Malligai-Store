    package com.grocery.model;

    import jakarta.persistence.*;

    @Entity
    public class CartItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long cartId;  // Unique identifier for the cart item

        private String userId; // User ID who owns the cart item

        private Long productId;
        private int quantity;// Product ID, linking the cart item to the product

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "productId", referencedColumnName = "productId", insertable = false, updatable = false)
        private Product product;  // Many CartItems can belong to one Product (but not the other way around)

        // Getters and Setters
        public Long getCartId() {
            return cartId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void setCartId(Long cartId) {
            this.cartId = cartId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }
    }
