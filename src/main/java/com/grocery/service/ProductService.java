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
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private CacheManager cacheManager;

    // Get all products (excluding deleted ones) with caching
    @Cacheable(value = "products", unless = "#result == null || #result.isEmpty()")
    public List<ProductDTO> getAllProductsWithVariants() {
        List<Product> products = productRepository.findAllByIsDeletedFalse();
        List<ProductDTO> productDTOs = products.stream()
                .map(DTOConverter::convertToProductDTO)
                .collect(Collectors.toList());

        if (productDTOs.isEmpty()) {
            cacheManager.getCache("products").clear();
        }

        return productDTOs;
    }



    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#searchTerm + '-' + #page + '-' + #size",
            unless = "#result == null || #result.isEmpty()")
    public Page<ProductDTO> searchProductsWithVariants(String searchTerm, int page, int size) {
        if (!searchTerm.endsWith("*")) {
            searchTerm = searchTerm + "*";
        }

        List<Object[]> results = productRepository.searchProductsWithVariants(searchTerm);

        // Group products manually to avoid duplicate entries
        Map<Long, ProductDTO> productMap = new HashMap<>();

        for (Object[] row : results) {
            Long productId = ((Number) row[0]).longValue();
            String name = (String) row[1];
            String category = (String) row[2];
            String imageUrl = (String) row[3];
            Long variantId = row[4] != null ? ((Number) row[4]).longValue() : null;
            String grams = row[5] != null ? (String) row[5] : null;
            Double price = row[6] != null ? ((Number) row[6]).doubleValue() : null;

            // Create a new product entry if it doesn't exist
            productMap.putIfAbsent(productId, new ProductDTO(productId, name, category, imageUrl, new ArrayList<>()));

            // Add variant to the product if it exists
            if (variantId != null) {
                productMap.get(productId).getVariants().add(new ProductVariantResponse(variantId, grams, price));
            }
        }

        // Convert map to list and apply pagination
        List<ProductDTO> productDTOList = new ArrayList<>(productMap.values());

        int start = Math.min(page * size, productDTOList.size());
        int end = Math.min(start + size, productDTOList.size());

        return new PageImpl<>(productDTOList.subList(start, end), PageRequest.of(page, size), productDTOList.size());
    }



    // Get a product by ID with caching
    @Transactional(readOnly = true)
    @Cacheable(value = "product", key = "#id", unless = "#result == null || #result.isDeleted()")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findByIdAndNotDeleted(id);
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
            variant.setDeleted(false);
            variants.add(variant);
        }

        productVariantRepository.saveAll(variants);
        savedProduct.setVariants(variants);

        return savedProduct;
    }

    // Update a product and update cache
    @Transactional
    @CachePut(value = "product", key = "#id")
    @CacheEvict(value = "products", allEntries = true) // Add this
    public Product updateProduct(Long id, String name, String category, String imageUrl,
                                 List<ProductVariantResponse> variants) {
        // Existing updateProduct code...
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (imageUrl == null) {
            imageUrl = existingProduct.getImageUrl();
        }

        existingProduct.setName(name);
        existingProduct.setCategory(category);
        existingProduct.setImageUrl(imageUrl);

        List<ProductVariant> currentVariants = existingProduct.getVariants();
        List<ProductVariantResponse> newVariants = variants != null ? variants : new ArrayList<>();

        Map<String, ProductVariantResponse> incomingVariantMap = newVariants.stream()
                .collect(Collectors.toMap(
                        ProductVariantResponse::getGrams,
                        variant -> variant,
                        (v1, v2) -> v1
                ));

        Iterator<ProductVariant> iterator = currentVariants.iterator();
        while (iterator.hasNext()) {
            ProductVariant existingVariant = iterator.next();
            ProductVariantResponse matchingIncoming = incomingVariantMap.get(existingVariant.getGrams());
            if (matchingIncoming == null) {
                existingVariant.setDeleted(true);
            } else {
                existingVariant.setPrice(matchingIncoming.getPrice());
                existingVariant.setDeleted(false);
                incomingVariantMap.remove(existingVariant.getGrams());
            }
        }

        for (ProductVariantResponse newVariant : incomingVariantMap.values()) {
            ProductVariant variant = new ProductVariant();
            variant.setProduct(existingProduct);
            variant.setGrams(newVariant.getGrams());
            variant.setPrice(newVariant.getPrice());
            variant.setDeleted(false);
            currentVariants.add(variant);
        }

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
    public List<ProductDTO> getFilteredBestSellingProducts(String search, String category) {
        List<Object[]> bestSellingProductsData = orderItemRepository.findBestSellingProducts();
        List<ProductDTO> bestSellingProducts = new ArrayList<>();

        for (Object[] data : bestSellingProductsData) {
            Long productId = (Long) data[0];
            Optional<Product> productOpt = productRepository.findByProductIdAndIsDeletedFalse(productId);

            if (productOpt.isPresent()) {
                Product product = productOpt.get();

                // Check filters (search and category)
                if ((search == null || product.getName().toLowerCase().contains(search.toLowerCase())) &&
                        (category == null || product.getCategory().equalsIgnoreCase(category))) {

                    // Fetch Variants
                    List<ProductVariantResponse> variants = product.getVariants().stream()
                            .filter(variant -> !variant.isDeleted())
                            .map(variant -> new ProductVariantResponse(variant.getVariantId(), variant.getGrams(), variant.getPrice()))
                            .collect(Collectors.toList());

                    // Convert to DTO
                    ProductDTO productDTO = new ProductDTO(
                            product.getProductId(),
                            product.getName(),
                            product.getCategory(),
                            product.getImageUrl(),
                            variants
                    );

                    bestSellingProducts.add(productDTO);
                }
            } else {
                System.err.println("Product not found for productId: " + productId);
            }
        }

        return bestSellingProducts;
    }

    public List<ProductDTO> getBestSellingProductVariant(String search, String category) {
        List<Object[]> bestSellingVariantData = orderItemRepository.findBestSellingVariant();
        List<ProductDTO> bestSellingProducts = new ArrayList<>();

        for (Object[] data : bestSellingVariantData) {
            Long variantId = (Long) data[0];
            Optional<ProductVariant> variantOpt = productVariantRepository.findByVariantId(variantId);

            if (variantOpt.isPresent()) {
                ProductVariant variant = variantOpt.get();
                if (variant.isDeleted()) {
                    continue;
                }
                Product product = variant.getProduct();

                // Apply filters (search and category)
                if ((search == null || product.getName().toLowerCase().contains(search.toLowerCase())) &&
                        (category == null || product.getCategory().equalsIgnoreCase(category))) {

                    // Only return the best-selling variant
                    List<ProductVariantResponse> variants = Collections.singletonList(
                            new ProductVariantResponse(variant.getVariantId(), variant.getGrams(), variant.getPrice())
                    );

                    // Convert to DTO
                    ProductDTO productDTO = new ProductDTO(
                            product.getProductId(),
                            product.getName(),
                            product.getCategory(),
                            product.getImageUrl(),
                            variants
                    );

                    bestSellingProducts.add(productDTO);
                }
            } else {
                System.err.println("Variant not found for variantId: " + variantId);
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
