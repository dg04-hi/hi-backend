package com.ktds.hi.recommend.biz.usecase.out;

import com.ktds.hi.recommend.biz.domain.PreferenceTag;

import java.util.List;
import java.util.Optional;

/**
 * 취향 태그 Repository 인터페이스
 * 취향 태그 관련 데이터 액세스 기능을 정의
 */
public interface PreferenceTagRepository {

	/**
	 * 활성화된 모든 취향 태그 조회
	 */
	List<PreferenceTag> findAllActiveTags();

	/**
	 * 태그 ID로 태그 조회
	 */
	Optional<PreferenceTag> findById(Long tagId);

	/**
	 * 태그명으로 태그 조회
	 */
	Optional<PreferenceTag> findByTagName(String tagName);

	/**
	 * 카테고리별 태그 조회
	 */
	List<PreferenceTag> findByCategory(String category);
}
