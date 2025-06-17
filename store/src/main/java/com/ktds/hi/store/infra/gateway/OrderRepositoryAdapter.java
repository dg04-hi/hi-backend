package com.ktds.hi.store.infra.gateway;

import com.ktds.hi.store.biz.usecase.out.OrderRepositoryPort;
import com.ktds.hi.store.domain.Order;
import com.ktds.hi.store.infra.gateway.entity.OrderEntity;
import com.ktds.hi.store.infra.gateway.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public List<Order> findOrdersByStoreId(Long storeId) {
        return orderJpaRepository.findByStoreId(storeId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findOrdersByStoreIdAndPeriod(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {

        // return orderJpaRepository.findByStoreIdAndOrderDateBetween(storeId, startDate, endDate)
        //         .stream()
        //         .map(this::toDomain)
        //         .collect(Collectors.toList());

        return orderJpaRepository.findByStoreIdAndOrderDateBetweenWithMenuName(storeId, startDate, endDate)
            .stream()
            .map(result -> {
                OrderEntity entity = (OrderEntity) result[0];
                String menuName = (String) result[1];

                Order order = this.toDomain(entity);  // 기존 toDomain 메서드 활용
                order.setMenuName(menuName);  // menuName만 별도로 설정
                return order;
            })
            .toList();
    }



    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return orderJpaRepository.findById(orderId)
                .map(this::toDomain);
    }

    @Override
    public List<Order> findAllOrders() {
        return orderJpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Order saveOrder(Order order) {
        OrderEntity entity = toEntity(order);
        OrderEntity saved = orderJpaRepository.save(entity);
        return toDomain(saved);
    }

    private Order toDomain(OrderEntity entity) {
        return Order.builder()
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

    private OrderEntity toEntity(Order domain) {
        return OrderEntity.builder()
                .id(domain.getId())
                .storeId(domain.getStoreId())
                .menuId(domain.getMenuId())
                .customerAge(domain.getCustomerAge())
                .customerGender(domain.getCustomerGender())
                .orderAmount(domain.getOrderAmount())
                .orderDate(domain.getOrderDate())
                .build();
    }
}