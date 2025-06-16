package com.ktds.hi.store.domain;


import lombok.Builder;
import lombok.Getter;

/**
 * 태그 도메인 클래스
 * 매장 태그 정보를 나타냄
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Getter
@Builder
public class Tag {
    private Long id;
    private String tagName;
    private TagCategory tagCategory;
    private String tagColor;
    private Integer sortOrder;
    private Boolean isActive;
    private Long clickCount;

    /**
     * 클릭 수 증가
     */
    public Tag incrementClickCount() {
        return Tag.builder()
                .id(this.id)
                .tagName(this.tagName)
                .tagCategory(this.tagCategory)
                .tagColor(this.tagColor)
                .sortOrder(this.sortOrder)
                .isActive(this.isActive)
                .clickCount(this.clickCount != null ? this.clickCount + 1 : 1L)
                .build();
    }

    /**
     * 활성 상태 확인
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }
}