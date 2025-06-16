package com.ktds.hi.store.biz.usecase.in;

import com.ktds.hi.store.infra.dto.OrderListResponse;
import com.ktds.hi.store.infra.dto.OrderResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderUseCase {

    OrderListResponse getOrdersByStore(Long storeId);

    OrderListResponse getOrdersByStoreAndPeriod(Long storeId, LocalDateTime startDate, LocalDateTime endDate);

    OrderResponse getOrder(Long orderId);

    List<OrderResponse> getAllOrders();
}