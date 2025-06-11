package com.ktds.hi.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 취향 태그 도메인 클래스
 * 사용 가능한 취향 태그 정보를 담는 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasteTag {
    
    private Long id;
    private String tagName;
    private TagType tagType;
    private String description;
    private Boolean isActive;
}
