package com.ktds.hi.store.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private Long storeId;
    private Long menuId;
    private String menuName;
    private Integer customerAge;
    private String customerGender;
    private BigDecimal orderAmount;
    private LocalDateTime orderDate;
    private LocalDateTime createdAt;
}