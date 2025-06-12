package com.ktds.hi.analytics.biz.usecase.out;

import com.ktds.hi.analytics.biz.domain.OrderStatistics;

import java.time.LocalDate;

/**
 * 주문 데이터 포트 인터페이스
 * 주문 통계 조회를 위한 출력 포트
 */
public interface OrderDataPort {
    
    /**
     * 기간별 주문 통계 조회
     */
    OrderStatistics getOrderStatistics(Long storeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 실시간 주문 현황 조회
     */
    Integer getCurrentOrderCount(Long storeId);
    
    /**
     * 월별 매출 조회
     */
    Long getMonthlyRevenue(Long storeId, int year, int month);
}
