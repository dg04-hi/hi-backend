package com.ktds.hi.analytics.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 주문 통계 도메인 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatistics {
    
    private Integer totalOrders;
    private Long totalRevenue;
    private Double averageOrderValue;
    private Integer peakHour;
    private List<String> popularMenus;
    private Map<String, Integer> customerAgeDistribution;
}
