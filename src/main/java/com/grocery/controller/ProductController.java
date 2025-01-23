package com.grocery.controller;

import com.grocery.dto.ProductDTO;
import com.grocery.model.Product;
import com.grocery.repository.ProductRepository;
import com.grocery.service.ImageService;
import com.grocery.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Get all products
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{categoryName}")
    public List<Product> getProductsByCategory(@PathVariable String categoryName) {
        return productService.findByCategory(categoryName);  // Method to fetch products based on category
    }

    @GetMapping("/edit/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            Optional<Product> productOptional = productService.getProductById(id);
            if (productOptional.isPresent()) {
                return ResponseEntity.ok(productOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            // Log the error for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/search/{searchTerm}")
    public ResponseEntity<List<Product>> searchProducts(@PathVariable String searchTerm) {
        List<Product> products = productService.searchProducts(searchTerm);
        return products.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).build() : ResponseEntity.ok(products);
    }

    // Create a new product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id,
                                 @RequestParam(value = "file", required = false) MultipartFile file,
                                 @RequestParam String name,
                                 @RequestParam String category,
                                 @RequestParam String grams,
                                 @RequestParam double price) throws IOException {
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = imageService.uploadImage(file.getBytes(), file.getOriginalFilename());
        }

        // Fetch existing product using the repository instance
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Retain the existing image URL if no new file is uploaded
        if (imageUrl == null) {
            imageUrl = existingProduct.getImageUrl();
        }

        // Update the product with new details
        Product updatedProduct = new Product(name, price, category, imageUrl, grams);
        return productService.updateProduct(id, updatedProduct);
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
            @RequestParam("grams") String grams,
            @RequestParam("price") double price
    ) {
        try {
            String imageUrl = imageService.uploadImage(file.getBytes(), file.getOriginalFilename());
            Product product = new Product(name, price, category, imageUrl, grams);

            Product savedProduct = productService.createProduct(product);
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
    public ResponseEntity<List<Product>> getBestSellingProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {

        List<Product> bestSellingProducts = productService.getFilteredBestSellingProducts(search, category);

        if (bestSellingProducts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bestSellingProducts);
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
    public List<Product> getProductsByCategories(@RequestParam(required = false) List<String> categories) {
        System.out.println("Selected Categories: " + categories); // Log the categories received

        // Return filtered products if categories are provided, otherwise return all products
        if (categories != null && !categories.isEmpty()) {
            return productService.findProductsByCategories(categories);
        } else {
            return productService.getAllProducts(); // If no categories, return all products
        }
    }




}
