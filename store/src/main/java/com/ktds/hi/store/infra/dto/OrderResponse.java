package com.ktds.hi.store.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long storeId;
    private Long menuId;
    private String menuName;
    private Integer customerAge;
    private String customerGender;
    private BigDecimal orderAmount;
    private LocalDateTime orderDate;
    private LocalDateTime createdAt;

    // Entity를 Response로 변환
    public static OrderResponse from(com.ktds.hi.store.infra.gateway.entity.OrderEntity entity) {
        return OrderResponse.builder()
                .id(entity.getId())
                .storeId(entity.getStoreId())
                .menuId(entity.getMenuId())
                .customerAge(entity.getCustomerAge())
                .customerGender(entity.getCustomerGender())
                .orderAmount(entity.getOrderAmount())
                .orderDate(entity.getOrderDate())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}