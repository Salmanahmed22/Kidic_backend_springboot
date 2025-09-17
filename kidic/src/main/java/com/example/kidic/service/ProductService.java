package com.example.kidic.service;

import com.example.kidic.entity.Product;
import com.example.kidic.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    /**
     * Create a new product (without file upload)
     */
    public Product createProduct(String name,
                                 String link,
                                 String description,
                                 Product.ImageType imageType,
                                 Product.CategoryType category) {
        Product product = new Product(name, link, description, imageType, category);
        return productRepository.save(product);
    }
    
    /**
     * Update an existing product (without file upload)
     */
    public Product updateProduct(Long productId,
                                 String name,
                                 String link,
                                 String description,
                                 Product.ImageType imageType,
                                 Product.CategoryType category) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (name != null) product.setName(name);
        if (link != null) product.setLink(link);
        if (description != null) product.setDescription(description);
        if (imageType != null) product.setImageType(imageType);
        if (category != null) product.setCategory(category);
        return productRepository.save(product);
    }
    
    /**
     * Delete a product
     */
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(productId);
    }
    
    /**
     * List all products
     */
    public java.util.List<Product> listProducts() {
        return productRepository.findAll();
    }
    
    /**
     * Upload image for a product
     */
    public Product uploadProductImage(Long productId, MultipartFile file) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        // Process file for database storage
        FileStorageService.FileStorageResult fileResult = fileStorageService.processFileForDatabase(file);
        
        // Update product with file information
        product.setImageType(Product.ImageType.CUSTOM);
        product.setImageName(fileResult.getOriginalFileName());
        product.setImageContent(fileResult.getFileContent());
        product.setImageSize(fileResult.getFileSize());
        product.setImageContentType(fileResult.getContentType());
        
        return productRepository.save(product);
    }
    
    /**
     * Get a product
     */
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
    
    /**
     * Delete product image
     */
    public Product deleteProductImage(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        // Clear file information
        product.setImageType(Product.ImageType.IMAGE_1); // Default to first image
        product.setImageName(null);
        product.setImageContent(null);
        product.setImageSize(null);
        product.setImageContentType(null);
        
        return productRepository.save(product);
    }
}
