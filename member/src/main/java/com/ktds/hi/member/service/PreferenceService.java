package com.ktds.hi.member.service;

import com.ktds.hi.member.dto.PreferenceRequest;
import com.ktds.hi.member.dto.TasteTagResponse;
import com.ktds.hi.member.domain.TagType;

import java.util.List;

/**
 * 취향 관리 서비스 인터페이스
 * 취향 정보 등록/수정 및 태그 관리 기능을 정의
 */
public interface PreferenceService {
    
    /**
     * 취향 정보 등록/수정
     */
    void savePreference(Long memberId, PreferenceRequest request);
    
    /**
     * 사용 가능한 취향 태그 목록 조회
     */
    List<TasteTagResponse> getAvailableTags();
    
    /**
     * 태그 유형별 태그 목록 조회
     */
    List<TasteTagResponse> getTagsByType(TagType tagType);
}
