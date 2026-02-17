package com.ecommerce.product.repository;


import com.ecommerce.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

     List<Product> findByActiveTrue();

     @Query("select p from products p where p.active=true and p.stockQuantity>0 and LOWER(p.name) like LOWER(CONCAT('%',:word,'%'))")
     List<Product> searchProducts(@Param("word") String word);

     Optional<Product> findByIdAndActiveTrue(Long id);
}
