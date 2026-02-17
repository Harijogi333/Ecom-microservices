package com.ecommerce.product.service;


import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.model.ProductRequest;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse addProduct(ProductRequest productRequest)
    {
        Product product=new Product();
        productRepository.save(mapToProduct(productRequest,product));
        return mapToProductResponse(product);
    }

    public Optional<ProductResponse> updateProduct(Long id, ProductRequest productRequest) {

        return productRepository.findById(id)
                .map(existingProduct ->
                {
                    mapToProduct(productRequest,existingProduct);
                    productRepository.save(existingProduct);
                    return mapToProductResponse(existingProduct);
                });

    }
    public List<ProductResponse> getAllProducts() {

        return productRepository.findByActiveTrue().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }
    public boolean deleteProduct(Long id) {

       return productRepository.findByIdAndActiveTrue(id)
               .map(product->{
                   product.setActive(false);
                   productRepository.save(product);
                   return true;
               }).orElse(false);
    }

    public List<ProductResponse> searchProduct(String word) {

        return productRepository.searchProducts(word).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

    }


    private ProductResponse mapToProductResponse(Product product)
    {
        ProductResponse response=new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setDescription(product.getDescription());
        response.setCategory(product.getCategory());
        response.setImageUrl(product.getImageUrl());
        response.setStockQuantity(product.getStockQuantity());
        response.setActive(product.getActive());
        return response;
    }

    private Product mapToProduct(ProductRequest productRequest,Product product)
    {
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        return product;
    }



}
