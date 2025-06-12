package com.ktds.hi.recommend.infra.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 취향 업데이트 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "취향 프로필 업데이트 요청")
public class TasteUpdateRequest {

    @NotEmpty(message = "선호 카테고리는 최소 1개 이상 선택해야 합니다")
    @Schema(description = "선호 카테고리 목록", example = "[\"한식\", \"일식\"]")
    private List<String> preferredCategories;

    @Schema(description = "선호 태그 목록", example = "[\"매운맛\", \"혼밥\", \"가성비\"]")
    private List<String> preferredTags;
}