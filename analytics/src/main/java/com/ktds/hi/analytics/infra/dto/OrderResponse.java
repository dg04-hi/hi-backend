package com.ktds.hi.analytics.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
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
}
