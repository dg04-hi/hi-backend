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
 * AI 피드백 엔티티
 * AI가 생성한 피드백 정보를 저장
 */
@Entity
@Table(name = "ai_feedback",
    indexes = {
        @Index(name = "idx_ai_feedback_store_id", columnList = "store_id"),
        @Index(name = "idx_ai_feedback_generated_at", columnList = "generated_at"),
        @Index(name = "idx_ai_feedback_created_at", columnList = "created_at"),
        @Index(name = "idx_ai_feedback_confidence_score", columnList = "confidence_score")
    })
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
    
    @Column(name = "summary", length = 1000)
    private String summary;
    
    @Column(name = "positive_points", columnDefinition = "TEXT")
    private String positivePointsJson;

    @Column(name = "negative_points", columnDefinition = "TEXT")
    private String negativePointsJson;

    @Column(name = "improvement_points", columnDefinition = "TEXT")
    private String improvementPointsJson;
    
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendationsJson;
    
    @Column(name = "sentiment_analysis", length = 500)
    private String sentimentAnalysis;
    
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    @Column(name = "generated_at")
    private LocalDateTime generatedAt;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
