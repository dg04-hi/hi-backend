package com.ktds.hi.store.biz.usecase.out;

import com.ktds.hi.store.domain.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {

    List<Order> findOrdersByStoreId(Long storeId);

    List<Order> findOrdersByStoreIdAndPeriod(Long storeId, LocalDateTime startDate, LocalDateTime endDate);

    Optional<Order> findOrderById(Long orderId);

    List<Order> findAllOrders();

    Order saveOrder(Order order);
}