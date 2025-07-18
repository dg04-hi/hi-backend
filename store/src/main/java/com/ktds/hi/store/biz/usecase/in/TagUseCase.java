package com.ktds.hi.store.biz.usecase.in;

import com.ktds.hi.store.infra.dto.response.AllTagResponse;
import com.ktds.hi.store.infra.dto.response.TopClickedTagResponse;

import java.util.List;

/**
 * 태그 유스케이스 인터페이스
 * 태그 관련 비즈니스 로직을 정의
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
public interface TagUseCase {

    /**
     * 모든 활성화된 태그 목록 조회
     *
     * @return 모든 태그 목록
     */
    List<AllTagResponse> getAllTags();

    /**
     * 가장 많이 클릭된 상위 5개 태그 조회
     */
    List<TopClickedTagResponse> getTopClickedTags(Integer storeId);

    /**
     * 태그 클릭 이벤트 처리
     */
    void recordTagClick(Long tagId);
}