package com.grocery.service;

import com.grocery.model.Product;
import com.grocery.repository.OrderItemRepository;
import com.grocery.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Get all products (excluding deleted ones)
    public List<Product> getAllProducts() {
        return productRepository.findAll().stream().filter(product -> !product.isDeleted()).toList();
    }

    // Search products by name, or category
    public List<Product> searchProducts(String searchTerm) {
        return productRepository.searchProducts(searchTerm);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id).filter(product -> !product.isDeleted());
    }


    // Create a new product
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // Update a product
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




    // In ProductService.java
    public boolean softDeleteProduct(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setDeleted(true);  // Assuming you added an 'isDeleted' field to the Product model
            productRepository.save(product);  // Save the updated product with the 'isDeleted' flag set
            return true;
        }
        return false;
    }


    // Find products by category (excluding deleted ones)
    public List<Product> findByCategory(String categoryName) {
        return productRepository.findByCategory(categoryName);
    }

    public long getProductCount() {
        return productRepository.count();
    }

    public List<Product> getBestSellingProducts() {
        List<Object[]> bestSellingProductsData = orderItemRepository.findBestSellingProducts();

        List<Product> bestSellingProducts = new ArrayList<>();

        for (Object[] data : bestSellingProductsData) {
            Long productId = (Long) data[0];
            // Get the product details from the product repository
            Optional<Product> productOpt = productRepository.findByProductIdAndIsDeletedFalse(productId);

            // If product exists and is not deleted, add it to the list
            productOpt.ifPresent(bestSellingProducts::add);
        }

        return bestSellingProducts;
    }
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

    public List<String> getProductCategories() {
        return productRepository.findAllCategories();
    }

    public void displayBestSellingProducts() {
        List<Product> bestSellingProducts = getBestSellingProducts();

        if (bestSellingProducts.isEmpty()) {
            System.out.println("No best-selling products found.");
        } else {
            bestSellingProducts.forEach(product -> {
                System.out.println("Product ID: " + product.getProductId());
                System.out.println("Product Name: " + product.getName());
                System.out.println("Price: " + product.getPrice());
                // You can log or perform any other operations on the product details here.
            });
        }
    }

    public List<Product> findProductsByCategories(List<String> categories) {
        return productRepository.findByCategories(categories);
    }




    @Scheduled(fixedRate = 3600000) // 3600000 ms = 1 hour
    public void scheduledBestSellingProducts() {
        displayBestSellingProducts();
    }

}
