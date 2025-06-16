package com.ktds.hi.store.infra.controller;

import com.ktds.hi.common.dto.SuccessResponse;
import com.ktds.hi.store.biz.usecase.in.OrderUseCase;
import com.ktds.hi.store.infra.dto.OrderListResponse;
import com.ktds.hi.store.infra.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stores/orders")
@RequiredArgsConstructor
@Tag(name = "주문 데이터", description = "주문 데이터 조회 API")
public class OrderController {

    private final OrderUseCase orderUseCase;

    @GetMapping("/store/{storeId}")
    @Operation(summary = "가게별 주문 조회", description = "특정 가게의 모든 주문을 조회합니다")
    public ResponseEntity<SuccessResponse<OrderListResponse>> getOrdersByStore(
            @PathVariable Long storeId) {

        OrderListResponse response = orderUseCase.getOrdersByStore(storeId);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @GetMapping("/store/{storeId}/period")
    @Operation(summary = "가게별 기간 주문 조회", description = "특정 가게의 기간별 주문을 조회합니다")
    public ResponseEntity<SuccessResponse<OrderListResponse>> getOrdersByStoreAndPeriod(
            @PathVariable Long storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        OrderListResponse response = orderUseCase.getOrdersByStoreAndPeriod(storeId, startDate, endDate);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "단일 주문 조회", description = "특정 주문 하나를 조회합니다 (테스트용)")
    public ResponseEntity<SuccessResponse<OrderResponse>> getOrder(@PathVariable Long orderId) {

        OrderResponse response = orderUseCase.getOrder(orderId);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @GetMapping("/all")
    @Operation(summary = "전체 주문 조회", description = "모든 주문을 조회합니다 (Analytics 서비스용)")
    public ResponseEntity<SuccessResponse<List<OrderResponse>>> getAllOrders() {

        List<OrderResponse> response = orderUseCase.getAllOrders();
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}