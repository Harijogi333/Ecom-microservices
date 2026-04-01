package com.ecommerce.order.dto;

import com.ecommerce.order.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEventDto {

    private long orderId;
    private String userId;
    private OrderStatus orderStatus;
    private List<OrderItemDto> orderItems;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;


}
