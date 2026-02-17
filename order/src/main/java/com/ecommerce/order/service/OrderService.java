package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    public Optional<OrderResponse> createOrder(String userId) {

        //validate the Cart Items

        List<CartItem> cartItems=cartService.getcartsByUser(userId);
        if(cartItems.isEmpty())
        {
            return Optional.empty();
        }

        //validate user

        /*Optional<User> user=userRepository.findById(Long.valueOf(userId));
        if(user.isEmpty())
        {
            return Optional.empty();
        }*/

        //Total amount

        BigDecimal totalPrice=cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        // create order

        Order order=new Order();
        order.setUserId(Long.valueOf(userId));
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem> orderItems=cartItems.stream().
                map(cartItem ->new OrderItem(null,cartItem.getProductId(),cartItem.getQuantity(),cartItem.getPrice(),order))
                .toList();
        order.setItems(orderItems);
        Order savedOrder=orderRepository.save(order);


        //clear cartItems

        cartService.clearCarts(userId);

        return Optional.of(mapToOrderResponse(savedOrder));


    }

    private OrderResponse mapToOrderResponse(Order order)
    {
        OrderResponse response=new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus());
        response.setOrderItem(order.getItems().stream().map(
                item ->new OrderItemDto(item.getId(),item.getProductId().toString(),item.getQuantity(),item.getPrice(),BigDecimal.valueOf(item.getQuantity()).multiply(item.getPrice()))).toList()
        );
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        return response;

    }
}
