package com.ktds.hi.recommend.infra.gateway;

import com.ktds.hi.recommend.biz.usecase.out.PreferenceTagRepository;
import com.ktds.hi.recommend.biz.domain.PreferenceTag;
import com.ktds.hi.recommend.infra.gateway.repository.PreferenceTagJpaRepository;
import com.ktds.hi.recommend.infra.gateway.entity.PreferenceTagEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 취향 태그 Repository 어댑터 클래스
 * PreferenceTagRepository를 구현하여 취향 태그 데이터 액세스 기능을 제공
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PreferenceTagRepositoryAdapter implements PreferenceTagRepository {

	private final PreferenceTagJpaRepository preferenceTagJpaRepository;

	@Override
	public List<PreferenceTag> findAllActiveTags() {
		log.info("활성화된 모든 취향 태그 조회");

		List<PreferenceTagEntity> entities = preferenceTagJpaRepository.findByIsActiveTrueOrderByTagName();

		return entities.stream()
			.map(this::toPreferenceTag)
			.collect(Collectors.toList());
	}

	@Override
	public Optional<PreferenceTag> findById(Long tagId) {
		log.info("태그 ID로 태그 조회: tagId={}", tagId);

		return preferenceTagJpaRepository.findById(tagId)
			.filter(entity -> entity.getIsActive())
			.map(this::toPreferenceTag);
	}

	@Override
	public Optional<PreferenceTag> findByTagName(String tagName) {
		log.info("태그명으로 태그 조회: tagName={}", tagName);

		return preferenceTagJpaRepository.findByTagNameAndIsActiveTrue(tagName)
			.map(this::toPreferenceTag);
	}

	@Override
	public List<PreferenceTag> findByCategory(String category) {
		log.info("카테고리별 태그 조회: category={}", category);

		List<PreferenceTagEntity> entities = preferenceTagJpaRepository.findByCategoryAndIsActiveTrueOrderByTagName(category);

		return entities.stream()
			.map(this::toPreferenceTag)
			.collect(Collectors.toList());
	}

	/**
	 * 엔티티를 도메인으로 변환
	 */
	private PreferenceTag toPreferenceTag(PreferenceTagEntity entity) {
		return PreferenceTag.builder()
			.id(entity.getId())
			.tagName(entity.getTagName())
			.category(entity.getCategory())
			.icon(entity.getIcon())
			.description(entity.getDescription())
			.isActive(entity.getIsActive())
			.build();
	}
}
