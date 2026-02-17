package com.ecommerce.order.service;


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

    //private final ProductRepository productRepository;
    //private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;


    public boolean addToCart(String userId, cartItemRequest request) {

        /*Optional<Product> productOpt=productRepository.findById(request.getProductId());
        if(productOpt.isEmpty())
        {
            return false;
        }

        Product product=productOpt.get();
        if(product.getStockQuantity()<request.getQuantity())
        {
            return  false;
        }

        Optional<User> userOpt=userRepository.findById(Long.valueOf(userId));
        if(userOpt.isEmpty())
        {
            return false;
        }

        User user=userOpt.get();*/

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
            cartItem.setUserId(Long.valueOf(userId));
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
