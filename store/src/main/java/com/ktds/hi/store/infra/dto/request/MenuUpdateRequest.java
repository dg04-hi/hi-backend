package com.ktds.hi.store.infra.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 메뉴 수정 요청 DTO
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 수정 요청")
public class MenuUpdateRequest {

    @Schema(description = "메뉴명", example = "치킨버거")
    private String menuName;

    @Schema(description = "메뉴 설명", example = "바삭한 치킨과 신선한 야채가 들어간 버거")
    private String description;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    @Schema(description = "가격", example = "8500")
    private Integer price;

    @Schema(description = "카테고리", example = "메인메뉴")
    private String category;

    @Schema(description = "이미지 URL", example = "/images/chicken-burger.jpg")
    private String imageUrl;

    @Schema(description = "판매 가능 여부", example = "true")
    private Boolean isAvailable;
}