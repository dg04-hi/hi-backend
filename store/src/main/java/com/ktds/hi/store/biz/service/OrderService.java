package com.ktds.hi.store.biz.service;

import com.ktds.hi.store.biz.usecase.in.OrderUseCase;
import com.ktds.hi.store.biz.usecase.out.OrderRepositoryPort;
import com.ktds.hi.store.domain.Order;
import com.ktds.hi.store.infra.dto.OrderListResponse;
import com.ktds.hi.store.infra.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService implements OrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    @Override
    public OrderListResponse getOrdersByStore(Long storeId) {
        log.info("가게 {} 의 주문 목록 조회", storeId);

        List<Order> orders = orderRepositoryPort.findOrdersByStoreId(storeId);
        List<OrderResponse> orderResponses = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return OrderListResponse.builder()
                .storeId(storeId)
                .totalCount(orderResponses.size())
                .orders(orderResponses)
                .build();
    }

    @Override
    public OrderListResponse getOrdersByStoreAndPeriod(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("가게 {} 의 기간별 주문 목록 조회: {} ~ {}", storeId, startDate, endDate);

        List<Order> orders = orderRepositoryPort.findOrdersByStoreIdAndPeriod(storeId, startDate, endDate);
        List<OrderResponse> orderResponses = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return OrderListResponse.builder()
                .storeId(storeId)
                .totalCount(orderResponses.size())
                .orders(orderResponses)
                .build();
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        log.info("주문 {} 조회", orderId);

        Order order = orderRepositoryPort.findOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

        return toResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        log.info("전체 주문 목록 조회");

        return orderRepositoryPort.findAllOrders().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .storeId(order.getStoreId())
                .menuId(order.getMenuId())
                .menuName(order.getMenuName())
                .customerAge(order.getCustomerAge())
                .customerGender(order.getCustomerGender())
                .orderAmount(order.getOrderAmount())
                .orderDate(order.getOrderDate())
                .createdAt(order.getCreatedAt())
                .build();
    }
}