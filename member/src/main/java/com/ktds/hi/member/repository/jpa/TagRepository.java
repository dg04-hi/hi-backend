// member/src/main/java/com/ktds/hi/member/repository/jpa/TagRepository.java
package com.ktds.hi.member.repository.jpa;

import com.ktds.hi.member.repository.entity.TagEntity;
import com.ktds.hi.member.repository.entity.TagCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 태그 정보 JPA 리포지토리 인터페이스
 * 태그 정보 데이터의 CRUD 작업을 담당
 */
@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    /**
     * 태그명 목록으로 활성 태그 조회
     */
    List<TagEntity> findByTagNameInAndIsActiveTrue(List<String> tagNames);

    /**
     * 활성 태그 전체 조회 (카테고리, 정렬순서별 정렬)
     */
    List<TagEntity> findByIsActiveTrueOrderByTagCategoryAscSortOrderAsc();

    /**
     * 카테고리별 활성 태그 조회 (정렬순서별 정렬)
     */
    List<TagEntity> findByTagCategoryAndIsActiveTrueOrderBySortOrderAsc(TagCategory tagCategory);

    /**
     * 태그명으로 태그 조회
     */
    Optional<TagEntity> findByTagNameAndIsActiveTrue(String tagName);

    /**
     * 카테고리별 태그 개수 조회
     */
    Long countByTagCategoryAndIsActiveTrue(TagCategory tagCategory);

    /**
     * 활성 태그 존재 여부 확인
     */
    boolean existsByTagNameAndIsActiveTrue(String tagName);
}