package com.ktds.hi.store.infra.gateway;

import com.ktds.hi.store.biz.usecase.out.TagRepositoryPort;
import com.ktds.hi.store.domain.Tag;
import com.ktds.hi.store.domain.TagCategory;
import com.ktds.hi.store.infra.gateway.entity.TagEntity;
import com.ktds.hi.store.infra.gateway.repository.TagJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 태그 리포지토리 어댑터 클래스
 * TagRepositoryPort를 구현하여 태그 데이터 액세스 기능을 제공
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TagRepositoryAdapter implements TagRepositoryPort {

    private final TagJpaRepository tagJpaRepository;

    @Override
    public List<Tag> findAllActiveTags() {
        log.info("활성화된 모든 태그 조회");

        List<TagEntity> entities = tagJpaRepository.findByIsActiveTrueOrderByTagName();

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Tag> findTagById(Long tagId) {
        log.info("태그 ID로 태그 조회: tagId={}", tagId);

        return tagJpaRepository.findById(tagId)
                .filter(entity -> Boolean.TRUE.equals(entity.getIsActive()))
                .map(this::toDomain);
    }

    @Override
    public Optional<Tag> findTagByName(String tagName) {
        log.info("태그명으로 태그 조회: tagName={}", tagName);

        return tagJpaRepository.findByTagNameAndIsActiveTrue(tagName)
                .map(this::toDomain);
    }

    @Override
    public List<Tag> findTopClickedTags(Integer storeId) {
        log.info("가장 많이 클릭된 상위 5개 태그 조회");

        List<TagEntity> entities = tagJpaRepository.findTop5ByOrderByClickCountDesc(storeId,
                PageRequest.of(0, 5)
        );

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Tag incrementTagClickCount(Long tagId) {
        log.info("태그 클릭 수 증가: tagId={}", tagId);

        TagEntity entity = tagJpaRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("태그를 찾을 수 없습니다: " + tagId));

        entity.incrementClickCount();
        TagEntity saved = tagJpaRepository.save(entity);

        return toDomain(saved);
    }

    @Override
    public Tag saveTag(Tag tag) {
        log.info("태그 저장: tagName={}", tag.getTagName());

        TagEntity entity = toEntity(tag);
        TagEntity saved = tagJpaRepository.save(entity);

        return toDomain(saved);
    }

    /**
     * 엔티티를 도메인으로 변환
     */
    private Tag toDomain(TagEntity entity) {
        return Tag.builder()
                .id(entity.getId())
                .tagName(entity.getTagName())
                .tagCategory(entity.getTagCategory())
                .tagColor(entity.getTagColor())
                .sortOrder(entity.getSortOrder())
                .isActive(entity.getIsActive())
                .clickCount(entity.getClickCount())
                .build();
    }

    /**
     * 도메인을 엔티티로 변환
     */
    private TagEntity toEntity(Tag domain) {
        return TagEntity.builder()
                .id(domain.getId())
                .tagName(domain.getTagName())
                .tagCategory(domain.getTagCategory())
                .tagColor(domain.getTagColor())
                .sortOrder(domain.getSortOrder())
                .isActive(domain.getIsActive())
                .clickCount(domain.getClickCount())
                .build();
    }
}
