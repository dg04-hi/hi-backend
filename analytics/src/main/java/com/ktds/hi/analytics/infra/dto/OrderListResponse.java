package com.ktds.hi.analytics.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponse {
	private Long storeId;
	private Integer totalCount;
	private List<OrderResponse> orders;
}

