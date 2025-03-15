package com.grocery.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.dto.ProductDTO;
import com.grocery.dto.ProductVariantResponse;
import com.grocery.model.Product;
import com.grocery.repository.ProductRepository;
import com.grocery.service.ImageService;
import com.grocery.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://grocery-shop-ee0ac.web.app", "https://grocery-shop-ee0ac.firebaseapp.com"})
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository; // Inject the repository bean

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ObjectMapper objectMapper;

    // Get all products
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> productDTOs = productService.getAllProductsWithVariants();
        return ResponseEntity.ok(productDTOs);
    }


    @GetMapping("/{categoryName}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(
            @PathVariable String categoryName,
            @RequestParam(value = "limit", defaultValue = "0") int limit) {

        List<ProductDTO> productDTOs = productService.findByCategoryWithVariants(categoryName, limit);
        return ResponseEntity.ok(productDTOs);
    }



    @GetMapping("/edit/products/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        try {
            Optional<Product> productOptional = productService.getProductById(id);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                ProductDTO productDTO = new ProductDTO(
                        product.getProductId(),
                        product.getName(),
                        product.getCategory(),
                        product.getImageUrl(),
                        product.getVariants().stream()
                                .filter(variant -> !variant.isDeleted()) // Filter out soft-deleted variants
                                .map(variant -> new ProductVariantResponse(
                                        variant.getVariantId(),
                                        variant.getGrams(),
                                        variant.getPrice()
                                ))
                                .collect(Collectors.toList())
                );
                return ResponseEntity.ok(productDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProductDTO> productDTOs = productService.searchProductsWithVariants(searchTerm, page, size);
        return ResponseEntity.ok(productDTOs);
    }


    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("grams") List<String> gramsList,
            @RequestParam("prices") List<Double> priceList) throws IOException {

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = imageService.uploadImage(file.getBytes(), file.getOriginalFilename());
        }

        Product createdProduct = productService.createProduct(name, category, imageUrl, gramsList, priceList);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam String variants) throws IOException {

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = imageService.uploadImage(file.getBytes(), file.getOriginalFilename());
        }

        // Parse variants JSON into a list of ProductVariantResponse
        List<ProductVariantResponse> variantList = objectMapper.readValue(variants,
                new TypeReference<List<ProductVariantResponse>>() {});

        // Update the product with new details
        Product updatedProduct = productService.updateProduct(id, name, category, imageUrl, variantList);
        return ResponseEntity.ok(updatedProduct);
    }


    // Soft delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            boolean deleted = productService.softDeleteProduct(id);
            if (deleted) {
                return ResponseEntity.ok("Product marked as deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found or already deleted");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting product");
        }
    }

    @PostMapping("/addproduct")
    public ResponseEntity<Product> addProduct(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("grams") List<String> gramsList, // Changed from "variants" to "grams"
            @RequestParam("prices") List<Double> priceList
    ) {
        try {
            // Upload image
            String imageUrl = imageService.uploadImage(file.getBytes(), file.getOriginalFilename());

            // Create product and variants
            Product savedProduct = productService.createProduct(name, category, imageUrl, gramsList, priceList);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @RequestMapping("/checkCookies")
    public ResponseEntity<String> checkCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println("Cookie received: " + cookie.getName() + "=" + cookie.getValue());
            }
        }
        return ResponseEntity.ok("Check server logs for cookies.");
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getProductCount() {
        long count = productService.getProductCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/best-selling")
    public ResponseEntity<List<ProductDTO>> getBestSellingProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {

        List<ProductDTO> bestSellingProducts = productService.getFilteredBestSellingProducts(search, category);

        if (bestSellingProducts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bestSellingProducts);
    }

    @GetMapping("/best-selling-variant")
    public ResponseEntity<List<ProductDTO>> getBestSellingProductVariant(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {

        List<ProductDTO> bestSellingVariants = productService.getBestSellingProductVariant(search, category);

        if (bestSellingVariants.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bestSellingVariants);
    }



    @GetMapping("/products/categories")
    public ResponseEntity<List<String>> getProductCategories() {
        List<String> categories = productService.getProductCategories();

        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/filterbycategory")
    public ResponseEntity<List<ProductDTO>> getProductsByCategories(@RequestParam(required = false) List<String> categories) {
        System.out.println("Selected Categories: " + categories); // Log the categories received

        List<ProductDTO> productDTOs;

        if (categories != null && !categories.isEmpty()) {
            productDTOs = productService.findProductsByCategories(categories);
        } else {
            productDTOs = productService.getAllProductsWithVariants(); // If no categories, return all products
        }

        return ResponseEntity.ok(productDTOs);
    }




}
