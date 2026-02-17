package com.ecommerce.order.dto;

import lombok.Data;

@Data
public class cartItemRequest {
    private Long productId;
    private Integer quantity;
}
