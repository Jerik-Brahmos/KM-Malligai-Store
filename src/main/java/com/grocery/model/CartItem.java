    package com.grocery.model;

    import jakarta.persistence.Entity;
    import jakarta.persistence.Id;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.ManyToOne;
    import jakarta.persistence.JoinColumn;

    @Entity
    public class CartItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long cartId;  // Unique identifier for the cart item

        private String userId; // User ID who owns the cart item

        private Long productId;  // Product ID, linking the cart item to the product

        private Long variantId;  // Variant ID for the selected product variation

        private int quantity;  // Quantity of the product in the cart

        @ManyToOne
        @JoinColumn(name = "productId", referencedColumnName = "productId", insertable = false, updatable = false)
        private Product product;  // Many CartItems can belong to one Product

        @ManyToOne
        @JoinColumn(name = "variantId", referencedColumnName = "variantId", insertable = false, updatable = false)
        private ProductVariant productVariant;

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

        public Long getVariantId() {
            return variantId;
        }

        public void setVariantId(Long variantId) {
            this.variantId = variantId;
        }

        public ProductVariant getProductVariant() {
            return productVariant;
        }

        public void setProductVariant(ProductVariant productVariant) {
            this.productVariant = productVariant;
        }
    }
