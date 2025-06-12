package com.ktds.hi.analytics.infra.gateway.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 분석 데이터 엔티티
 * 매장의 분석 정보를 저장
 */
@Entity
@Table(name = "analytics",
    indexes = {
        @Index(name = "idx_analytics_store_id", columnList = "store_id"),
        @Index(name = "idx_analytics_last_analysis_date", columnList = "last_analysis_date"),
        @Index(name = "idx_analytics_created_at", columnList = "created_at"),
        @Index(name = "idx_analytics_average_rating", columnList = "average_rating")
    })
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AnalyticsEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "store_id", nullable = false)
    private Long storeId;
    
    @Column(name = "total_reviews")
    private Integer totalReviews;
    
    @Column(name = "average_rating")
    private Double averageRating;
    
    @Column(name = "sentiment_score")
    private Double sentimentScore;
    
    @Column(name = "positive_review_rate")
    private Double positiveReviewRate;
    
    @Column(name = "negative_review_rate")
    private Double negativeReviewRate;
    
    @Column(name = "last_analysis_date")
    private LocalDateTime lastAnalysisDate;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
