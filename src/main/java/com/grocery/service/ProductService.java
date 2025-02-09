package com.grocery.service;

import com.grocery.dto.ProductDTO;
import com.grocery.dto.ProductVariantResponse;
import com.grocery.model.Product;
import com.grocery.model.ProductVariant;
import com.grocery.repository.OrderItemRepository;
import com.grocery.repository.ProductRepository;
import com.grocery.repository.ProductVariantRepository;
import com.grocery.util.DTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    // Get all products (excluding deleted ones) with caching
    @Transactional(readOnly = true)
    @Cacheable(value = "products", unless = "#result == null || #result.isEmpty()")
    public List<ProductDTO> getAllProductsWithVariants() {
        List<Product> products = productRepository.findAllByIsDeletedFalse();

        // Convert to DTOs
        return products.stream()
                .map(DTOConverter::convertToProductDTO)
                .collect(Collectors.toList());
    }



    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#searchTerm + '-' + #page + '-' + #size",
            unless = "#result == null || #result.isEmpty()")
    public Page<ProductDTO> searchProductsWithVariants(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (!searchTerm.endsWith("*")) {
            searchTerm = searchTerm + "*";
        }

        Page<Object[]> results = productRepository.searchProductsWithVariants(searchTerm, pageable);

        return results.map(this::mapToProductDTO);
    }

    private ProductDTO mapToProductDTO(Object[] result) {
        Long productId = ((Number) result[0]).longValue();
        String name = (String) result[1];
        String category = (String) result[2];
        String imageUrl = (String) result[3];

        // Create a ProductDTO object
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(productId);
        productDTO.setName(name);
        productDTO.setCategory(category);
        productDTO.setImageUrl(imageUrl);

        // Extract variant data
        Long variantId = ((Number) result[4]).longValue();
        String grams = (String) result[5];
        double price = ((Number) result[6]).doubleValue();

        // Create a list of variants
        List<ProductVariantResponse> variants = new ArrayList<>();
        variants.add(new ProductVariantResponse(variantId, grams, price));

        productDTO.setVariants(variants);

        return productDTO;
    }



    // Get a product by ID with caching
    @Transactional(readOnly = true)  // Ensure read-only transaction for performance optimization
    @Cacheable(value = "product", key = "#id", unless = "#result == null || #result.isDeleted()")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findByIdAndNotDeleted(id); // Fetch directly with filtering logic
    }


    // Create a new product and evict cache
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product createProduct(String name, String category, String imageUrl, List<String> gramsList, List<Double> priceList) {
        // Create and save product
        Product product = new Product(name, category, imageUrl);
        Product savedProduct = productRepository.save(product);

        // Create and save variants
        List<ProductVariant> variants = new ArrayList<>();
        for (int i = 0; i < gramsList.size(); i++) {
            ProductVariant variant = new ProductVariant(savedProduct, gramsList.get(i), priceList.get(i));
            variants.add(variant);
        }

        productVariantRepository.saveAll(variants);
        savedProduct.setVariants(variants);

        return savedProduct;
    }

    // Update a product and update cache
    @CachePut(value = "product", key = "#id")
    public Product updateProduct(Long id, String name, String category, String imageUrl, List<String> grams, List<Double> prices) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Retain existing image URL if no new image is uploaded
        if (imageUrl == null) {
            imageUrl = existingProduct.getImageUrl();
        }

        // Update product fields
        existingProduct.setName(name);
        existingProduct.setCategory(category);
        existingProduct.setImageUrl(imageUrl);

        // Delete old variants
        productVariantRepository.deleteByProduct(existingProduct);

        // Add new variants
        List<ProductVariant> variants = new ArrayList<>();
        for (int i = 0; i < grams.size(); i++) {
            ProductVariant variant = new ProductVariant(existingProduct, grams.get(i), prices.get(i));
            variants.add(variant);
        }

        // Save updated product and variants
        productVariantRepository.saveAll(variants);
        existingProduct.setVariants(variants);

        return productRepository.save(existingProduct);
    }

    // Soft delete a product and evict cache
    @CacheEvict(value = {"products", "product"}, allEntries = true)
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
    @Async
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#categoryName + '-' + #limit", unless = "#result == null || #result.isEmpty()")
    public List<ProductDTO> findByCategoryWithVariants(String categoryName, int limit) {
        List<Product> products;

        if (limit > 0) {
            Pageable pageable = PageRequest.of(0, limit);
            products = productRepository.findTopByCategoryWithVariants(categoryName, pageable);
        } else {
            products = productRepository.findByCategoryWithVariants(categoryName);
        }

        return products.stream()
                .map(DTOConverter::convertToProductDTO)
                .collect(Collectors.toList());
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
            bestSellingProducts.forEach(product -> {
                System.out.println("Product ID: " + product.getProductId());
                System.out.println("Product Name: " + product.getName());
//                System.out.println("Price: " + product.getPrice());
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
    @Cacheable(value = "productsByCategory", key = "#categories.toString()", unless = "#result == null || #result.isEmpty()")
    public List<ProductDTO> findProductsByCategories(List<String> categories) {
        List<Product> products = productRepository.findByCategoriesWithVariants(categories);
        System.out.println("from service"+products);
        // Convert to DTOs using DTOConverter
        return products.stream()
                .map(DTOConverter::convertToProductDTO)
                .collect(Collectors.toList());
    }


}
