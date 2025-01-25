package com.grocery.service;

import com.grocery.model.Product;
import com.grocery.repository.OrderItemRepository;
import com.grocery.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // Get all products (excluding deleted ones) with caching
    @Cacheable(value = "products", unless = "#result == null || #result.isEmpty()")
    public List<Product> getAllProducts() {
        return productRepository.findAll().stream().filter(product -> !product.isDeleted()).toList();
    }

    // Search products by name or category with caching
    @Cacheable(value = "products", key = "#searchTerm", unless = "#result == null || #result.isEmpty()")
    public List<Product> searchProducts(String searchTerm) {
        return productRepository.searchProducts(searchTerm);
    }

    // Get a product by ID with caching
    @Cacheable(value = "product", key = "#id", unless = "#result == null || #result.isDeleted()")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id).filter(product -> !product.isDeleted());
    }

    // Create a new product and evict cache
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // Update a product and update cache
    @CachePut(value = "product", key = "#id")
    public Product updateProduct(Long id, Product product) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product updatedProduct = existingProduct.get();
            updatedProduct.setName(product.getName());
            updatedProduct.setPrice(product.getPrice());
            updatedProduct.setCategory(product.getCategory());
            updatedProduct.setGrams(product.getGrams());
            updatedProduct.setImageUrl(product.getImageUrl()); // If new image is provided
            return productRepository.save(updatedProduct);
        }
        return null;
    }

    // Soft delete a product and evict cache
    @CacheEvict(value = {"products", "product"}, key = "#id")
    public boolean softDeleteProduct(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setDeleted(true);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    // Find products by category with caching
    @Cacheable(value = "categories", key = "#categoryName", unless = "#result == null || #result.isEmpty()")
    public List<Product> findByCategory(String categoryName) {
        return productRepository.findByCategory(categoryName);
    }

    // Get product count (no caching applied here as it's not expensive)
    public long getProductCount() {
        return productRepository.count();
    }

    // Get best-selling products with caching
    @Cacheable(value = "bestSellingProducts", unless = "#result == null || #result.isEmpty()")
    public List<Product> getBestSellingProducts() {
        List<Object[]> bestSellingProductsData = orderItemRepository.findBestSellingProducts();
        List<Product> bestSellingProducts = new ArrayList<>();
        for (Object[] data : bestSellingProductsData) {
            Long productId = (Long) data[0];
            Optional<Product> productOpt = productRepository.findByProductIdAndIsDeletedFalse(productId);
            productOpt.ifPresent(bestSellingProducts::add);
        }
        return bestSellingProducts;
    }

    // Get filtered best-selling products (no caching as it's dynamic)
    public List<Product> getFilteredBestSellingProducts(String search, String category) {
        List<Object[]> bestSellingProductsData = orderItemRepository.findBestSellingProducts();
        List<Product> bestSellingProducts = new ArrayList<>();
        for (Object[] data : bestSellingProductsData) {
            Long productId = (Long) data[0];
            Optional<Product> productOpt = productRepository.findByProductIdAndIsDeletedFalse(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                if ((search == null || product.getName().toLowerCase().contains(search.toLowerCase())) &&
                        (category == null || product.getCategory().equalsIgnoreCase(category))) {
                    bestSellingProducts.add(product);
                }
            } else {
                System.err.println("Product not found for productId: " + productId);
            }
        }
        return bestSellingProducts;
    }

    // Get product categories with caching
    @Cacheable(value = "categories", unless = "#result == null || #result.isEmpty()")
    public List<String> getProductCategories() {
        return productRepository.findAllCategories();
    }

    // Display best-selling products (no caching as it's a scheduled task)
    public void displayBestSellingProducts() {
        List<Product> bestSellingProducts = getBestSellingProducts();
        if (bestSellingProducts.isEmpty()) {
            System.out.println("No best-selling products found.");
        } else {
            bestSellingProducts.forEach(product -> {
                System.out.println("Product ID: " + product.getProductId());
                System.out.println("Product Name: " + product.getName());
                System.out.println("Price: " + product.getPrice());
            });
        }
    }

    // Scheduled task to evict best-selling products cache every hour
    @Scheduled(fixedRate = 3600000) // 1 hour
    @CacheEvict(value = "bestSellingProducts", allEntries = true)
    public void scheduledBestSellingProducts() {
        displayBestSellingProducts();
    }

    // Find products by multiple categories (no caching as it's dynamic)
    public List<Product> findProductsByCategories(List<String> categories) {
        return productRepository.findByCategories(categories);
    }
}
