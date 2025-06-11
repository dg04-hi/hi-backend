package com.ktds.hi.member.repository.jpa;

import com.ktds.hi.member.repository.entity.PreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 취향 정보 JPA 리포지토리 인터페이스
 * 취향 정보 데이터의 CRUD 작업을 담당
 */
@Repository
public interface PreferenceRepository extends JpaRepository<PreferenceEntity, Long> {
    
    /**
     * 회원 ID로 취향 정보 조회
     */
    Optional<PreferenceEntity> findByMemberId(Long memberId);
    
    /**
     * 회원 ID로 취향 정보 존재 여부 확인
     */
    boolean existsByMemberId(Long memberId);
    
    /**
     * 회원 ID로 취향 정보 삭제
     */
    void deleteByMemberId(Long memberId);
}
