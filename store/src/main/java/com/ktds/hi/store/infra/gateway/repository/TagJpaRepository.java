package com.ktds.hi.store.infra.gateway.repository;

import com.ktds.hi.store.domain.TagCategory;
import com.ktds.hi.store.infra.gateway.entity.TagEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 태그 JPA 리포지토리 인터페이스
 * 태그 데이터의 CRUD 작업을 담당
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Repository
public interface TagJpaRepository extends JpaRepository<TagEntity, Long> {

    /**
     * 활성화된 태그 목록 조회
     */
    List<TagEntity> findByIsActiveTrueOrderByTagName();

    /**
     * 태그명으로 조회
     */
    Optional<TagEntity> findByTagNameAndIsActiveTrue(String tagName);

    /**
     * 카테고리별 태그 조회
     */
    List<TagEntity> findByTagCategoryAndIsActiveTrueOrderByTagName(TagCategory category);

    /**
     * 클릭 수 기준 상위 태그 조회
     */
    @Query("SELECT t FROM TagEntity t WHERE t.isActive = true ORDER BY t.clickCount DESC")
    List<TagEntity> findTopClickedTags(PageRequest pageRequest);

    /**
     * 클릭 수 기준 상위 5개 태그 조회
     */
    @Query("SELECT t FROM TagEntity t WHERE t.isActive = true ORDER BY t.clickCount DESC")
    List<TagEntity> findTop5ByOrderByClickCountDesc(PageRequest pageRequest);
}
