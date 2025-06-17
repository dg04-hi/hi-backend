package com.ktds.hi.store.infra.gateway.repository;

import com.ktds.hi.store.domain.Order;
import com.ktds.hi.store.infra.gateway.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByStoreId(Long storeId);

    List<OrderEntity> findByStoreIdAndOrderDateBetween(
            Long storeId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    //기간 조회시, 메뉴명을 조회하기 위해서 조인하는 쿼리
    @Query("SELECT o, m.menuName FROM OrderEntity o LEFT JOIN MenuEntity m ON o.menuId = m.id " +
        "WHERE o.storeId = :storeId AND o.orderDate BETWEEN :startDate AND :endDate")
    List<Object[]> findByStoreIdAndOrderDateBetweenWithMenuName(
        @Param("storeId") Long storeId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

}