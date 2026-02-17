package com.ecommerce.order.controller;


import com.ecommerce.order.dto.cartItemRequest;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping()
    public ResponseEntity<String> addToCart(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody cartItemRequest request)
    {
        if(!cartService.addToCart(userId,request)) {
            return ResponseEntity.badRequest().body("product out of stock or user not found or product not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("cart created or updated successfully");
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> deleteCartItem(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable Long productId)
    {
        boolean deleted=cartService.deleteCart(userId,productId);
        return  deleted?ResponseEntity.noContent().build()
                :ResponseEntity.notFound().build();
    }

    @GetMapping()
    public ResponseEntity<List<CartItem>> getCartItemByUser(@RequestHeader("X-User-ID") String userId)
    {
        return ResponseEntity.ok(cartService.getcartsByUser(userId));
    }
}
