package com.ktds.hi.analytics.infra.gateway.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 통계 엔티티 클래스
 * 데이터베이스 order_statistics 테이블과 매핑되는 JPA 엔티티
 */
@Entity
@Table(name = "order_statistics")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StatisticsEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "store_id", nullable = false)
    private Long storeId;
    
    @Column(name = "analysis_date")
    private LocalDate analysisDate;
    
    @Column(name = "total_orders")
    private Integer totalOrders;
    
    @Column(name = "total_revenue", precision = 15, scale = 2)
    private BigDecimal totalRevenue;
    
    @Column(name = "avg_order_amount", precision = 10, scale = 2)
    private BigDecimal avgOrderAmount;
    
    @Column(name = "peak_hour")
    private Integer peakHour;
    
    @Column(name = "age_statistics_json", columnDefinition = "TEXT")
    private String ageStatisticsJson;
    
    @Column(name = "gender_statistics_json", columnDefinition = "TEXT")
    private String genderStatisticsJson;
    
    @Column(name = "time_statistics_json", columnDefinition = "TEXT")
    private String timeStatisticsJson;
    
    @Column(name = "menu_popularity_json", columnDefinition = "TEXT")
    private String menuPopularityJson;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * JSON 문자열을 객체로 변환하는 메서드들
     */
    public Map<String, Object> getAgeStatisticsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(ageStatisticsJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
    
    public Map<String, Object> getGenderStatisticsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(genderStatisticsJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
    
    public Map<String, Object> getTimeStatisticsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(timeStatisticsJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
    
    public Map<String, Object> getMenuPopularityMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(menuPopularityJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
}
