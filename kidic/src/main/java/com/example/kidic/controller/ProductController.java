package com.example.kidic.controller;

import com.example.kidic.entity.Product;
import com.example.kidic.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    @GetMapping
    public ResponseEntity<List<Product>> listProducts() {
        return ResponseEntity.ok(productService.listProducts());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product request) {
        Product created = productService.createProduct(
                request.getName(),
                request.getLink(),
                request.getDescription(),
                request.getImageType(),
                request.getCategory()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product request) {
        Product updated = productService.updateProduct(
                productId,
                request.getName(),
                request.getLink(),
                request.getDescription(),
                request.getImageType(),
                request.getCategory()
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
    
    @Autowired
    private ProductService productService;
    
    /**
     * Upload image for a product
     * POST /api/products/{productId}/image
     */
    @PostMapping(value = "/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) {
        
        try {
            Product updatedProduct = productService.uploadProductImage(productId, file);
            return ResponseEntity.ok(updatedProduct);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    /**
     * Get product image
     * GET /api/products/{productId}/image
     */
    @GetMapping("/{productId}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long productId) {
        try {
            Product product = productService.getProduct(productId);
            
            if (product.getImageContent() == null || product.getImageContent().length == 0) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(product.getImageContentType()))
                    .header("Content-Disposition", "inline; filename=\"" + product.getImageName() + "\"")
                    .header("Content-Length", String.valueOf(product.getImageSize()))
                    .body(product.getImageContent());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete product image
     * DELETE /api/products/{productId}/image
     */
    @DeleteMapping("/{productId}/image")
    public ResponseEntity<Product> deleteProductImage(@PathVariable Long productId) {
        try {
            Product updatedProduct = productService.deleteProductImage(productId);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get product information
     * GET /api/products/{productId}
     */
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Long productId) {
        try {
            Product product = productService.getProduct(productId);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
