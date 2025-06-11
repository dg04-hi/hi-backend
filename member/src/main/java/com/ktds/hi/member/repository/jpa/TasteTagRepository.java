package com.ktds.hi.member.repository.jpa;

import com.ktds.hi.member.domain.TagType;
import com.ktds.hi.member.repository.entity.TasteTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 취향 태그 JPA 리포지토리 인터페이스
 * 취향 태그 데이터의 CRUD 작업을 담당
 */
@Repository
public interface TasteTagRepository extends JpaRepository<TasteTagEntity, Long> {
    
    /**
     * 활성화된 태그 목록 조회
     */
    List<TasteTagEntity> findByIsActiveTrue();
    
    /**
     * 태그 유형별 태그 목록 조회
     */
    List<TasteTagEntity> findByTagTypeAndIsActiveTrue(TagType tagType);
    
    /**
     * 태그명으로 태그 조회
     */
    List<TasteTagEntity> findByTagNameIn(List<String> tagNames);
}
