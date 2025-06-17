package com.ktds.hi.analytics.infra.gateway;

import com.ktds.hi.analytics.biz.domain.OrderStatistics;
import com.ktds.hi.analytics.biz.usecase.out.OrderDataPort;
import com.ktds.hi.analytics.infra.dto.OrderListResponse;
import com.ktds.hi.analytics.infra.dto.OrderResponse;
import com.ktds.hi.common.dto.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 주문 데이터 어댑터 클래스
 * 외부 주문 서비스와의 연동을 담당
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDataAdapter implements OrderDataPort {
    
    private final RestTemplate restTemplate;
    
    @Value("${external.services.store}")
    private String storeServiceUrl;
    
    @Override
    public OrderStatistics getOrderStatistics(Long storeId, LocalDate startDate, LocalDate endDate) {
        log.info("주문 통계 조회: storeId={}, period={} ~ {}", storeId, startDate, endDate);
        
        try {

            // LocalDate를 LocalDateTime으로 변환 (시작일은 00:00:00, 종료일은 23:59:59)
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            String url = String.format("%s/api/stores/orders/store/%d/period?startDate=%s&endDate=%s",
                storeServiceUrl, storeId, startDateTime, endDateTime);


            // ParameterizedTypeReference를 사용하여 제네릭 타입 안전하게 파싱
            ParameterizedTypeReference<SuccessResponse<OrderListResponse>> responseType =
                new ParameterizedTypeReference<SuccessResponse<OrderListResponse>>() {};

            ResponseEntity<SuccessResponse<OrderListResponse>> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, null, responseType);

            SuccessResponse<OrderListResponse> successResponse = responseEntity.getBody();

            if (successResponse != null && successResponse.isSuccess() && successResponse.getData() != null) {
                OrderListResponse orderListResponse = successResponse.getData();

                // OrderListResponse를 OrderStatistics로 변환
                OrderStatistics statistics = convertToOrderStatistics(orderListResponse);

                log.info("주문 통계 조회 완료: storeId={}, totalOrders={}",
                    storeId, statistics.getTotalOrders());
                return statistics;
            }
            
        } catch (Exception e) {
            log.error("주문 통계 조회 실패: storeId={}", storeId, e);
        }
        
        // 실패 시 더미 데이터 반환
        return createDummyOrderStatistics(storeId);
    }

    /**
     * OrderListResponse를 OrderStatistics로 변환
     */
    private OrderStatistics convertToOrderStatistics(OrderListResponse orderListResponse) {
        List<OrderResponse> orders = orderListResponse.getOrders();

        if (orders == null || orders.isEmpty()) {
            return createEmptyOrderStatistics();
        }

        // 총 주문 수
        int totalOrders = orders.size();

        // 총 매출 계산
        BigDecimal totalRevenue = orders.stream()
            .map(OrderResponse::getOrderAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 평균 주문 금액 계산
        double averageOrderValue = totalRevenue.doubleValue() / totalOrders;

        // 시간대별 주문 분석 (피크 시간 계산)
        Map<Integer, Long> hourlyOrderCount = orders.stream()
            .collect(Collectors.groupingBy(
                order -> order.getOrderDate().getHour(),
                Collectors.counting()
            ));

        int peakHour = hourlyOrderCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(12); // 기본값 12시

        // 연령대별 분포 계산
        Map<String, Integer> ageDistribution = calculateCustomerAgeDistribution(orders);

        //메뉴명 매핑
        Map<Long, String> menuIdToName = orders.stream()
            .collect(Collectors.toMap(
                OrderResponse::getMenuId,
                OrderResponse::getMenuName,
                (existing, replacement) -> existing // 중복시 기존값 유지
            ));

        // 인기 메뉴 계산 (메뉴ID별 주문 횟수) - 실제로는 메뉴명을 가져와야 하지만 임시로 메뉴ID 사용
        List<String> popularMenus = orders.stream()
            .collect(Collectors.groupingBy(
                OrderResponse::getMenuId,
                Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
            .limit(4)
            .map(entry -> menuIdToName.get(entry.getKey())) // 실제로는 메뉴명으로 변환 필요
            .collect(Collectors.toList());

        return OrderStatistics.builder()
            .totalOrders(totalOrders)
            .totalRevenue(totalRevenue.longValue())
            .averageOrderValue(averageOrderValue)
            .peakHour(peakHour)
            .popularMenus(popularMenus)
            .customerAgeDistribution(ageDistribution)
            .build();
    }

    /**
     * 연령대별 분포 계산
     */
    private Map<String, Integer> calculateCustomerAgeDistribution(List<OrderResponse> orders) {
        Map<String, Long> ageGroupCount = orders.stream()
            .collect(Collectors.groupingBy(
                order -> getAgeGroup(order.getCustomerAge()),
                Collectors.counting()
            ));

        // Long을 Integer로 변환
        Map<String, Integer> result = new HashMap<>();
        ageGroupCount.forEach((key, value) -> result.put(key, value.intValue()));

        return result;
    }

    /**
     * 연령대별 분포 계산
     */
    private Map<String, Integer> calculateAgeDistribution(List<OrderResponse> orders) {
        Map<String, Long> ageGroupCount = orders.stream()
            .collect(Collectors.groupingBy(
                order -> getAgeGroup(order.getCustomerAge()),
                Collectors.counting()
            ));

        // Long을 Integer로 변환
        Map<String, Integer> result = new HashMap<>();
        ageGroupCount.forEach((key, value) -> result.put(key, value.intValue()));

        return result;
    }

    /**
     * 나이를 연령대 그룹으로 변환
     */
    private String getAgeGroup(Integer age) {
        if (age == null) return "미분류";
        if (age < 20) return "10대";
        if (age < 30) return "20대";
        if (age < 40) return "30대";
        if (age < 50) return "40대";
        if (age < 60) return "50대";
        return "60대+";
    }

    /**
     * 빈 주문 통계 생성
     */
    private OrderStatistics createEmptyOrderStatistics() {
        return OrderStatistics.builder()
            .totalOrders(0)
            .totalRevenue(0L)
            .averageOrderValue(0.0)
            .peakHour(12)
            .popularMenus(Arrays.asList())
            .customerAgeDistribution(new HashMap<>())
            .build();
    }
    
    @Override
    public Integer getCurrentOrderCount(Long storeId) {
        log.info("실시간 주문 현황 조회: storeId={}", storeId);
        
        try {
            String url = storeServiceUrl + "/api/orders/stores/" + storeId + "/current-count";
            Integer count = restTemplate.getForObject(url, Integer.class);
            
            log.info("실시간 주문 현황 조회 완료: storeId={}, count={}", storeId, count);
            return count != null ? count : 0;
            
        } catch (Exception e) {
            log.error("실시간 주문 현황 조회 실패: storeId={}", storeId, e);
            return 3; // 더미 값
        }
    }
    
    @Override
    public Long getMonthlyRevenue(Long storeId, int year, int month) {
        log.info("월별 매출 조회: storeId={}, year={}, month={}", storeId, year, month);
        
        try {
            String url = String.format("%s/api/orders/stores/%d/revenue?year=%d&month=%d",
                    storeServiceUrl, storeId, year, month);
            
            Long revenue = restTemplate.getForObject(url, Long.class);
            
            log.info("월별 매출 조회 완료: storeId={}, revenue={}", storeId, revenue);
            return revenue != null ? revenue : 0L;
            
        } catch (Exception e) {
            log.error("월별 매출 조회 실패: storeId={}", storeId, e);
            return 5500000L; // 더미 값
        }
    }
    
    /**
     * 더미 주문 통계 생성
     */
    private OrderStatistics createDummyOrderStatistics(Long storeId) {
        Map<String, Integer> ageDistribution = new HashMap<>();
        ageDistribution.put("20대", 35);
        ageDistribution.put("30대", 45);
        ageDistribution.put("40대", 25);
        ageDistribution.put("50대", 15);
        
        return OrderStatistics.builder()
                .totalOrders(156)
                .totalRevenue(3280000L)
                .averageOrderValue(21025.64)
                .peakHour(19)
                .popularMenus(Arrays.asList("치킨버거", "불고기버거", "감자튀김", "콜라"))
                .customerAgeDistribution(ageDistribution)
                .build();
    }
}
