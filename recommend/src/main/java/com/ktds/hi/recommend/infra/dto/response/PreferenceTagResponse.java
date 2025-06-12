package com.ktds.hi.recommend.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 취향 태그 응답 DTO
 */
@Getter
@Builder
@Schema(description = "취향 태그 응답")
public class PreferenceTagResponse {

    @Schema(description = "태그명", example = "매운맛")
    private String tagName;

    @Schema(description = "태그 아이콘", example = "🌶️")
    private String icon;

    @Schema(description = "태그 설명", example = "매운 음식을 선호")
    private String description;

    /**
     * 정적 생성 메서드
     */
    public static PreferenceTagResponse of(String tagName, String icon, String description) {
        return PreferenceTagResponse.builder()
                .tagName(tagName)
                .icon(icon)
                .description(description)
                .build();
    }
}