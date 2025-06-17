package com.ktds.hi.store.infra.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 모든 태그 응답 DTO
 * 태그 기본 정보를 담는 응답 클래스
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Getter
@Builder
public class AllTagResponse {

    /**
     * 태그 ID
     */
    private Long id;

    /**
     * 태그 카테고리
     */
    private String tagCategory;

    /**
     * 태그명
     */
    private String tagName;
}
