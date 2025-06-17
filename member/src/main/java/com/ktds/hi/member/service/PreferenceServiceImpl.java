// member/src/main/java/com/ktds/hi/member/service/PreferenceServiceImpl.java
package com.ktds.hi.member.service;

import com.ktds.hi.member.dto.PreferenceRequest;
import com.ktds.hi.member.dto.TasteTagResponse;
import com.ktds.hi.member.domain.TagType;
import com.ktds.hi.member.repository.entity.PreferenceEntity;
import com.ktds.hi.member.repository.entity.TagEntity;
import com.ktds.hi.member.repository.entity.TagCategory;
import com.ktds.hi.member.repository.jpa.PreferenceRepository;
import com.ktds.hi.member.repository.jpa.TagRepository;
import com.ktds.hi.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 취향 관리 서비스 구현체
 * 취향 정보 등록/수정 및 태그 관리 기능을 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PreferenceServiceImpl implements PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final TagRepository tagRepository;

    @Override
    public void savePreference(Long memberId, PreferenceRequest request) {
        // 태그 유효성 검증
        List<TagEntity> existingTags = tagRepository.findByTagNameInAndIsActiveTrue(request.getTags());
        if (existingTags.size() != request.getTags().size()) {
            throw new BusinessException("유효하지 않은 태그가 포함되어 있습니다");
        }

        // 기존 취향 정보 조회
        PreferenceEntity preference = preferenceRepository.findByMemberId(memberId)
                .orElse(null);

        if (preference != null) {
            // 기존 정보 업데이트
            preference.updatePreference(request.getTags(), request.getHealthInfo(), request.getSpicyLevel());
        } else {
            // 새로운 취향 정보 생성
            preference = PreferenceEntity.builder()
                    .memberId(memberId)
                    .tags(request.getTags())
                    .healthInfo(request.getHealthInfo())
                    .spicyLevel(request.getSpicyLevel())
                    .build();
        }

        preferenceRepository.save(preference);

        log.info("취향 정보 저장 완료: memberId={}, tags={}", memberId, request.getTags());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TasteTagResponse> getAvailableTags() {
        List<TagEntity> tags = tagRepository.findByIsActiveTrueOrderByTagCategoryAscSortOrderAsc();

        return tags.stream()
                .map(tag -> TasteTagResponse.builder()
                        .id(tag.getId())
                        .tagName(tag.getTagName())
                        .tagType(convertTagCategoryToTagType(tag.getTagCategory()))
                        .description(tag.getTagCategory().getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TasteTagResponse> getTagsByType(TagType tagType) {
        TagCategory tagCategory = convertTagTypeToTagCategory(tagType);
        List<TagEntity> tags = tagRepository.findByTagCategoryAndIsActiveTrueOrderBySortOrderAsc(tagCategory);

        return tags.stream()
                .map(tag -> TasteTagResponse.builder()
                        .id(tag.getId())
                        .tagName(tag.getTagName())
                        .tagType(tagType)
                        .description(tag.getTagCategory().getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * TagCategory를 TagType으로 변환 (기존 호환성을 위해)
     */
    private TagType convertTagCategoryToTagType(TagCategory tagCategory) {
        // TagType enum이 존재한다면 적절한 매핑 로직 구현
        // 임시로 기본값 반환
        return TagType.TASTE; // 실제 매핑 로직 필요
    }

    /**
     * TagType을 TagCategory로 변환 (기존 호환성을 위해)
     */
    private TagCategory convertTagTypeToTagCategory(TagType tagType) {
        // TagType에 따른 TagCategory 매핑
        switch (tagType) {
            case TASTE:
                return TagCategory.TASTE;
            default:
                return TagCategory.TASTE;
        }
    }
}