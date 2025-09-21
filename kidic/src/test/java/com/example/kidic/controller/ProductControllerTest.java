package com.example.kidic.controller;

import com.example.kidic.entity.Product;
import com.example.kidic.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void testListProducts_Success() throws Exception {
        Product product1 = new Product("Product1", "link1", "desc1", Product.ImageType.IMAGE_1, Product.CategoryType.TOYS);
        Product product2 = new Product("Product2", "link2", "desc2", Product.ImageType.IMAGE_2, Product.CategoryType.BOOKS);
        List<Product> products = Arrays.asList(product1, product2);

        when(productService.listProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Product1"))
                .andExpect(jsonPath("$[1].name").value("Product2"));

        verify(productService, times(1)).listProducts();
    }

    @Test
    void testListProducts_MissingAuthorization() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).listProducts();
    }

    @Test
    void testCreateProduct_Success() throws Exception {
        Product request = new Product("New Product", "new-link", "new-desc", Product.ImageType.IMAGE_1, Product.CategoryType.TOYS);
        Product created = new Product("New Product", "new-link", "new-desc", Product.ImageType.IMAGE_1, Product.CategoryType.TOYS);
        created.setId(1L);

        when(productService.createProduct(anyString(), anyString(), anyString(), any(), any())).thenReturn(created);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Product\",\"link\":\"new-link\",\"description\":\"new-desc\",\"imageType\":\"IMAGE_1\",\"category\":\"TOYS\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Product"));

        verify(productService, times(1)).createProduct("New Product", "new-link", "new-desc", Product.ImageType.IMAGE_1, Product.CategoryType.TOYS);
    }

    @Test
    void testCreateProduct_MissingAuthorization() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Product\",\"link\":\"new-link\",\"description\":\"new-desc\",\"imageType\":\"IMAGE_1\",\"category\":\"TOYS\"}"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        Product request = new Product("Updated Product", "updated-link", "updated-desc", Product.ImageType.IMAGE_2, Product.CategoryType.BOOKS);
        Product updated = new Product("Updated Product", "updated-link", "updated-desc", Product.ImageType.IMAGE_2, Product.CategoryType.BOOKS);
        updated.setId(1L);

        when(productService.updateProduct(eq(1L), anyString(), anyString(), anyString(), any(), any())).thenReturn(updated);

        mockMvc.perform(put("/api/products/1")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Product\",\"link\":\"updated-link\",\"description\":\"updated-desc\",\"imageType\":\"IMAGE_2\",\"category\":\"BOOKS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"));

        verify(productService, times(1)).updateProduct(1L, "Updated Product", "updated-link", "updated-desc", Product.ImageType.IMAGE_2, Product.CategoryType.BOOKS);
    }

    @Test
    void testUpdateProduct_MissingAuthorization() throws Exception {
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Product\",\"link\":\"updated-link\",\"description\":\"updated-desc\",\"imageType\":\"IMAGE_2\",\"category\":\"BOOKS\"}"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).updateProduct(anyLong(), anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProduct_MissingAuthorization() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).deleteProduct(anyLong());
    }

    @Test
    void testUploadProductImage_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        Product updatedProduct = new Product("Product", "link", "desc", Product.ImageType.CUSTOM, Product.CategoryType.TOYS);
        updatedProduct.setId(1L);

        when(productService.uploadProductImage(eq(1L), any(MultipartFile.class))).thenReturn(updatedProduct);

        mockMvc.perform(multipart("/api/products/1/image")
                        .file(file)
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageType").value("CUSTOM"));

        verify(productService, times(1)).uploadProductImage(1L, file);
    }

    @Test
    void testUploadProductImage_MissingAuthorization() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(multipart("/api/products/1/image")
                        .file(file))
                .andExpect(status().isBadRequest());

        verify(productService, never()).uploadProductImage(anyLong(), any(MultipartFile.class));
    }

    @Test
    void testGetProductImage_Success() throws Exception {
        Product product = new Product("Product", "link", "desc", Product.ImageType.CUSTOM, Product.CategoryType.TOYS);
        product.setImageContent("test image content".getBytes());
        product.setImageContentType("image/jpeg");
        product.setImageName("test.jpg");
        product.setImageSize(18L);

        when(productService.getProduct(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("image/jpeg")))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"test.jpg\""))
                .andExpect(header().string("Content-Length", "18"))
                .andExpect(content().bytes("test image content".getBytes()));

        verify(productService, times(1)).getProduct(1L);
    }

    @Test
    void testGetProductImage_NoImage() throws Exception {
        Product product = new Product("Product", "link", "desc", Product.ImageType.IMAGE_1, Product.CategoryType.TOYS);
        product.setImageContent(null);

        when(productService.getProduct(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1/image"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProduct(1L);
    }

    @Test
    void testGetProductImage_NonExistentProduct() throws Exception {
        when(productService.getProduct(999L)).thenThrow(new IllegalArgumentException("Product not found"));

        mockMvc.perform(get("/api/products/999/image"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProduct(999L);
    }

    @Test
    void testDeleteProductImage_Success() throws Exception {
        Product updatedProduct = new Product("Product", "link", "desc", Product.ImageType.IMAGE_1, Product.CategoryType.TOYS);
        updatedProduct.setId(1L);

        when(productService.deleteProductImage(1L)).thenReturn(updatedProduct);

        mockMvc.perform(delete("/api/products/1/image")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageType").value("IMAGE_1"));

        verify(productService, times(1)).deleteProductImage(1L);
    }

    @Test
    void testDeleteProductImage_MissingAuthorization() throws Exception {
        mockMvc.perform(delete("/api/products/1/image"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).deleteProductImage(anyLong());
    }

    @Test
    void testDeleteProductImage_NonExistentProduct() throws Exception {
        when(productService.deleteProductImage(999L)).thenThrow(new IllegalArgumentException("Product not found"));

        mockMvc.perform(delete("/api/products/999/image")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProductImage(999L);
    }

    @Test
    void testGetProduct_Success() throws Exception {
        Product product = new Product("Product", "link", "desc", Product.ImageType.IMAGE_1, Product.CategoryType.TOYS);
        product.setId(1L);

        when(productService.getProduct(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Product"));

        verify(productService, times(1)).getProduct(1L);
    }

    @Test
    void testGetProduct_NonExistentProduct() throws Exception {
        when(productService.getProduct(999L)).thenThrow(new IllegalArgumentException("Product not found"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProduct(999L);
    }
}
