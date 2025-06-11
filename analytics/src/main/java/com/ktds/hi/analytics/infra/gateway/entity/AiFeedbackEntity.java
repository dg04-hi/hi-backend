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
import java.util.List;
import java.util.Map;

/**
 * AI 피드백 엔티티 클래스
 * 데이터베이스 ai_feedback 테이블과 매핑되는 JPA 엔티티
 */
@Entity
@Table(name = "ai_feedback")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AiFeedbackEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "store_id", nullable = false)
    private Long storeId;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(length = 20)
    private String sentiment;
    
    @Column(name = "positive_points_json", columnDefinition = "TEXT")
    private String positivePointsJson;
    
    @Column(name = "negative_points_json", columnDefinition = "TEXT")
    private String negativePointsJson;
    
    @Column(name = "recommendations_json", columnDefinition = "TEXT")
    private String recommendationsJson;
    
    @Column(precision = 3, scale = 2)
    private BigDecimal confidence;
    
    @Column(name = "analysis_date")
    private LocalDate analysisDate;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * JSON 문자열을 객체로 변환하는 메서드들
     */
    public Map<String, Object> getPositivePointsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(positivePointsJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
    
    public Map<String, Object> getNegativePointsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(negativePointsJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
    
    public List<String> getRecommendationsList() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(recommendationsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
