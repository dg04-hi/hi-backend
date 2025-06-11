package com.ktds.hi.common.repository;

import com.ktds.hi.common.audit.AuditAction;
import com.ktds.hi.common.audit.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 감사 로그 리포지토리
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    /**
     * 사용자별 감사 로그 조회
     */
    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 액션별 감사 로그 조회
     */
    Page<AuditLog> findByActionOrderByCreatedAtDesc(AuditAction action, Pageable pageable);
    
    /**
     * 엔티티별 감사 로그 조회
     */
    Page<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, String entityId, Pageable pageable);
    
    /**
     * 기간별 감사 로그 조회
     */
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt BETWEEN :startDate AND :endDate ORDER BY al.createdAt DESC")
    Page<AuditLog> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate, 
                                         Pageable pageable);
    
    /**
     * 사용자 액션 통계 조회
     */
    @Query("SELECT al.action, COUNT(al) FROM AuditLog al WHERE al.userId = :userId GROUP BY al.action")
    List<Object[]> findActionStatsByUserId(@Param("userId") Long userId);
    
    /**
     * 일별 로그 수 조회
     */
    @Query("SELECT DATE(al.createdAt), COUNT(al) FROM AuditLog al WHERE al.createdAt >= :startDate GROUP BY DATE(al.createdAt) ORDER BY DATE(al.createdAt)")
    List<Object[]> findDailyLogCounts(@Param("startDate") LocalDateTime startDate);
}
