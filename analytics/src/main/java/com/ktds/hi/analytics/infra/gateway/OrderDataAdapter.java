package com.ktds.hi.analytics.infra.gateway;

import com.ktds.hi.analytics.biz.domain.OrderStatistics;
import com.ktds.hi.analytics.biz.usecase.out.OrderDataPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
            String url = String.format("%s/api/stores/orders/store/%d/period?startDate=%s&endDate=%s",
                storeServiceUrl, storeId, startDate, endDate);


            OrderStatistics statistics = restTemplate.getForObject(url, OrderStatistics.class);
            
            if (statistics != null) {
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
