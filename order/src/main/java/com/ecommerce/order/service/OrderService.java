package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderCreatedEventDto;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

//    @Value("${rabbitmq.queue.name}")
//    private String queueName;
//    @Value("${rabbitmq.routing.key}")
//    private String keyName;
//    @Value("${rabbitmq.exchange.name}")
//    private String exchangeName;

    private final OrderRepository orderRepository;
    private final CartService cartService;
//  private final RabbitTemplate rabbitTemplate;
    private final StreamBridge streamBridge;

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
        order.setUserId(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem> orderItems=cartItems.stream().
                map(cartItem ->new OrderItem(null,cartItem.getProductId(),cartItem.getQuantity(),cartItem.getPrice(),order))
                .toList();
        order.setItems(orderItems);
        Order savedOrder=orderRepository.save(order);


        //clear cartItems

        cartService.clearCarts(userId);

        OrderCreatedEventDto orderEvent=new OrderCreatedEventDto
                (savedOrder.getId(),savedOrder.getUserId(),savedOrder.getStatus(),mapOrderItemToOrderItemDto(savedOrder.getItems()),savedOrder.getTotalAmount(),savedOrder.getCreatedAt());

//        rabbitTemplate.convertAndSend(exchangeName,keyName,orderEvent);
        streamBridge.send("createOrder-out-0",orderEvent);



        return Optional.of(mapToOrderResponse(savedOrder));


    }


    private List<OrderItemDto> mapOrderItemToOrderItemDto(List<OrderItem> orderItems)
    {
        return orderItems.stream()
                .map(order -> new OrderItemDto(order.getId(),order.getProductId().toString(),order.getQuantity(),order.getPrice(),order.getPrice().multiply(BigDecimal.valueOf(order.getQuantity()))))
                .collect(Collectors.toList());

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
