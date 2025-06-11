package com.ktds.hi.member.repository.jpa;

import com.ktds.hi.member.repository.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 JPA 리포지토리 인터페이스
 * 회원 데이터의 CRUD 작업을 담당
 */
@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    
    /**
     * 사용자명으로 회원 조회
     */
    Optional<MemberEntity> findByUsername(String username);
    
    /**
     * 닉네임으로 회원 조회
     */
    Optional<MemberEntity> findByNickname(String nickname);
    
    /**
     * 전화번호로 회원 조회
     */
    Optional<MemberEntity> findByPhone(String phone);
    
    /**
     * 사용자명 존재 여부 확인
     */
    boolean existsByUsername(String username);
    
    /**
     * 닉네임 존재 여부 확인
     */
    boolean existsByNickname(String nickname);
}
