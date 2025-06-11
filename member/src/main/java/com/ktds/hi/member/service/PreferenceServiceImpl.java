package com.ktds.hi.member.service;

import com.ktds.hi.member.dto.PreferenceRequest;
import com.ktds.hi.member.dto.TasteTagResponse;
import com.ktds.hi.member.domain.TagType;
import com.ktds.hi.member.repository.entity.PreferenceEntity;
import com.ktds.hi.member.repository.entity.TasteTagEntity;
import com.ktds.hi.member.repository.jpa.PreferenceRepository;
import com.ktds.hi.member.repository.jpa.TasteTagRepository;
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
    private final TasteTagRepository tasteTagRepository;
    
    @Override
    public void savePreference(Long memberId, PreferenceRequest request) {
        // 태그 유효성 검증
        List<TasteTagEntity> existingTags = tasteTagRepository.findByTagNameIn(request.getTags());
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
        List<TasteTagEntity> tags = tasteTagRepository.findByIsActiveTrue();
        
        return tags.stream()
                .map(tag -> TasteTagResponse.builder()
                        .id(tag.getId())
                        .tagName(tag.getTagName())
                        .tagType(tag.getTagType())
                        .description(tag.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TasteTagResponse> getTagsByType(TagType tagType) {
        List<TasteTagEntity> tags = tasteTagRepository.findByTagTypeAndIsActiveTrue(tagType);
        
        return tags.stream()
                .map(tag -> TasteTagResponse.builder()
                        .id(tag.getId())
                        .tagName(tag.getTagName())
                        .tagType(tag.getTagType())
                        .description(tag.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
