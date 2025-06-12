package com.ktds.hi.recommend.biz.usecase.in;

import java.util.List;

import com.ktds.hi.recommend.infra.dto.response.PreferenceTagResponse;
import com.ktds.hi.recommend.infra.dto.response.TasteAnalysisResponse;

/**
 * 취향 분석 유스케이스 인터페이스
 * 사용자 취향 분석 기능을 정의
 */
public interface TasteAnalysisUseCase {
    
    /**
     * 사용자 취향 분석
     */
    TasteAnalysisResponse analyzeMemberTaste(Long memberId);
    
    /**
     * 취향 프로필 업데이트
     */
    void updateTasteProfile(Long memberId);

    /**
     * 가용한 취향 태그 목록 조회
     */
    List<PreferenceTagResponse> getAvailablePreferenceTags();
}
