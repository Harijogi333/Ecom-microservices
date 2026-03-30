package com.ecommerce.order.service;


import com.ecommerce.order.client.ProductServiceClient;
import com.ecommerce.order.client.UserServiceClient;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.dto.cartItemRequest;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.repository.CartItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
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
    int attempt=1;


    //@CircuitBreaker(name="productService",fallbackMethod = "addToCartFallback")
    @Retry(name="retryProduct",fallbackMethod = "addToCartFallback")
    @RateLimiter(name = "rateLimitBreaker",fallbackMethod ="addToCartFallback")
    public boolean addToCart(String userId, cartItemRequest request) {

        System.out.println("attempt no"+ ++attempt);
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

        CartItem existingCartItem=cartItemRepository.findByUserIdAndProductId(userId,request.getProductId());
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

    public boolean addToCartFallback(String userId, cartItemRequest request,Exception exception)
    {
        exception.printStackTrace();
        return false;
    }


    public boolean deleteCart(String userId, Long productId) {

        //Optional<User> user=userRepository.findById(Long.valueOf(userId));
        //Optional<Product> product=productRepository.findById(productId);
        if(/*user.isPresent() && product.isPresent() && */cartItemRepository.findByUserIdAndProductId(userId,productId)!=null)
        {
            cartItemRepository.deleteByUserIdAndProductId(userId,productId);
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
