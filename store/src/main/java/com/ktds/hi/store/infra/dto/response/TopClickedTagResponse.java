package com.ktds.hi.store.infra.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 인기 태그 응답 DTO 클래스
 * 가장 많이 클릭된 태그 정보를 전달
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Getter
@Builder
public class TopClickedTagResponse {
    private Long tagId;
    private String tagName;
    private String tagCategory;
    private String tagColor;
    private Long clickCount;
    private Integer rank;
}