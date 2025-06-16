package com.ktds.hi.store.infra.gateway.repository;

import com.ktds.hi.store.infra.gateway.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByStoreId(Long storeId);

    List<OrderEntity> findByStoreIdAndOrderDateBetween(
            Long storeId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}