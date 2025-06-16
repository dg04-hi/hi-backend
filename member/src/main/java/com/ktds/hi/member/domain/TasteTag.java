package com.ktds.hi.member.domain;

import jakarta.persistence.Table;
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
@Table(name = "taste_tag")
public class TasteTag {
    
    private Long id;
    private String tagName;
    private TagType tagType; //카테고리
    private String description; //매운맛, 짠맛
    private Boolean isActive;
}
