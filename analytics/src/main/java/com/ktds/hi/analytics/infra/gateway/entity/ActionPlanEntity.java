package com.ktds.hi.analytics.infra.gateway.entity;

import com.ktds.hi.analytics.biz.domain.PlanStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
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
 * 실행 계획 엔티티
 * 점주의 개선 실행 계획을 저장
 */
@Entity
@Table(name = "action_plan",
    indexes = {
        @Index(name = "idx_action_plan_store_id", columnList = "store_id"),
        @Index(name = "idx_action_plan_user_id", columnList = "user_id"),
        @Index(name = "idx_action_plan_status", columnList = "status"),
        @Index(name = "idx_action_plan_created_at", columnList = "created_at")
    })
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ActionPlanEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "store_id", nullable = false)
    private Long storeId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "title", nullable = false, length = 100)
    private String title;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "period", length = 50)
    private String period;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PlanStatus status;
    
    @Column(name = "tasks", columnDefinition = "TEXT")
    private String tasksJson;
    
    @Column(name = "note", length = 1000)
    private String note;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
