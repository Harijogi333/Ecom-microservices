package com.ecommerce.order.repository;


import com.ecommerce.order.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    CartItem findByUserIdAndProductId(String userId, Long productId);

    void deleteByUserIdAndProductId(String userId, Long productId);

    List<CartItem> findByUserId(String userId);

    void deleteByUserId(String userId);
}
