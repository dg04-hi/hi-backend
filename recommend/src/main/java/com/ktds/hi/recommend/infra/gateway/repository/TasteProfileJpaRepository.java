package com.ktds.hi.recommend.infra.gateway.repository;

import com.ktds.hi.recommend.infra.gateway.entity.TasteProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 취향 프로필 JPA 리포지토리 인터페이스
 * 취향 프로필 데이터의 CRUD 작업을 담당
 */
@Repository
public interface TasteProfileJpaRepository extends JpaRepository<TasteProfileEntity, Long> {
    
    /**
     * 회원 ID로 취향 프로필 조회
     */
    Optional<TasteProfileEntity> findByMemberId(Long memberId);
    
    /**
     * 회원 ID로 취향 프로필 존재 여부 확인
     */
    boolean existsByMemberId(Long memberId);
    
    /**
     * 회원 ID로 취향 프로필 삭제
     */
    void deleteByMemberId(Long memberId);
}
