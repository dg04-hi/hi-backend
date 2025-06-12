package com.ktds.hi.recommend.infra.gateway.repository;

import com.ktds.hi.recommend.infra.gateway.entity.PreferenceTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 취향 태그 JPA 리포지토리 인터페이스
 * 취향 태그 데이터의 CRUD 작업을 담당
 */
@Repository
public interface PreferenceTagJpaRepository extends JpaRepository<PreferenceTagEntity, Long> {

	/**
	 * 활성화된 태그 목록 조회
	 */
	List<PreferenceTagEntity> findByIsActiveTrueOrderByTagName();

	/**
	 * 태그명으로 태그 조회
	 */
	Optional<PreferenceTagEntity> findByTagNameAndIsActiveTrue(String tagName);

	/**
	 * 카테고리별 활성화된 태그 조회
	 */
	List<PreferenceTagEntity> findByCategoryAndIsActiveTrueOrderByTagName(String category);
}
