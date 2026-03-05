package com.ecommerce.order.service;


import com.ecommerce.order.client.ProductServiceClient;
import com.ecommerce.order.client.UserServiceClient;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.dto.cartItemRequest;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.repository.CartItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    public boolean addToCart(String userId, cartItemRequest request) {
        ProductResponse productResponse =productServiceClient.getProductDetailsById(request.getProductId());

        if(productResponse==null || productResponse.getStockQuantity()<request.getQuantity())
        {
            return false;
        }

        UserResponse userResponse= userServiceClient.findUserById(userId);

        if(userResponse==null)
        {
            return false;
        }

        CartItem existingCartItem=cartItemRepository.findByUserIdAndProductId(userId,request.getProductId().toString());
        if(existingCartItem!=null)
        {
            existingCartItem.setQuantity(existingCartItem.getQuantity()+request.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(1000));
            cartItemRepository.save(existingCartItem);
        }
        else {

            CartItem cartItem=new CartItem();
            cartItem.setProductId(request.getProductId());
            cartItem.setUserId(userId);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(BigDecimal.valueOf(1000));
            cartItemRepository.save(cartItem);

        }
        return true;

    }

    public boolean deleteCart(String userId, Long productId) {

        //Optional<User> user=userRepository.findById(Long.valueOf(userId));
        //Optional<Product> product=productRepository.findById(productId);
        if(/*user.isPresent() && product.isPresent() && */cartItemRepository.findByUserIdAndProductId(userId,productId.toString())!=null)
        {
            cartItemRepository.deleteByUserIdAndProductId(userId,productId.toString());
            return true;
        }
        return false;
    }

    public  List<CartItem> getcartsByUser(String userId) {

        return cartItemRepository.findByUserId(userId);
    }


    public void clearCarts(String userId) {

        cartItemRepository.deleteByUserId(userId);
    }
}
