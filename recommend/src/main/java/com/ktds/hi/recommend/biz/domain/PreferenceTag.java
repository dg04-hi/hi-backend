package com.ktds.hi.recommend.biz.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * 취향 태그 도메인 클래스
 * 사용자 취향 태그 정보를 나타냄
 */
@Getter
@Builder
public class PreferenceTag {

	private Long id;
	private String tagName;
	private String category;
	private String icon;
	private String description;
	private boolean isActive;

	/**
	 * 정적 팩토리 메서드 - 기본 태그 생성
	 */
	public static PreferenceTag of(String tagName, String category, String icon, String description) {
		return PreferenceTag.builder()
			.tagName(tagName)
			.category(category)
			.icon(icon)
			.description(description)
			.isActive(true)
			.build();
	}
}
