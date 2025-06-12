package com.ktds.hi.analytics.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 매장 통계 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreStatisticsResponse {
    
    private Long storeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalOrders;
    private Long totalRevenue;
    private Double averageOrderValue;
    private Integer peakHour;
    private List<String> popularMenus;
    private Map<String, Integer> customerAgeDistribution;
    private Integer totalReviews;
    private LocalDateTime generatedAt;
}
