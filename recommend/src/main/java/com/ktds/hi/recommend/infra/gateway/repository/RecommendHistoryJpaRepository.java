package com.ktds.hi.recommend.infra.gateway.repository;

import com.ktds.hi.recommend.infra.gateway.entity.RecommendHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 추천 히스토리 JPA 리포지토리 인터페이스
 * 추천 히스토리 데이터의 CRUD 작업을 담당
 */
@Repository
public interface RecommendHistoryJpaRepository extends JpaRepository<RecommendHistoryEntity, Long> {
    
    /**
     * 회원 ID로 추천 히스토리 조회 (최신순)
     */
    List<RecommendHistoryEntity> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    
    /**
     * 회원 ID로 최신 추천 히스토리 조회
     */
    RecommendHistoryEntity findTopByMemberIdOrderByCreatedAtDesc(Long memberId);
}
