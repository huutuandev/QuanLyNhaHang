package com.restaurant.management.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodOrderResponse {
    private Long id;
    private String orderCode;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;
    private String note;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private Double subtotal;
    private Double shippingFee;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FoodOrderItemResponse> orderItems;
}
