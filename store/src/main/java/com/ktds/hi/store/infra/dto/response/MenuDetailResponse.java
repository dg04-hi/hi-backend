package com.ktds.hi.store.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 메뉴 상세 응답 DTO
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 상세 정보")
public class MenuDetailResponse {

    @Schema(description = "메뉴 ID", example = "1")
    private Long menuId;

    @Schema(description = "매장 ID", example = "1")
    private Long storeId;

    @Schema(description = "메뉴명", example = "치킨버거")
    private String menuName;

    @Schema(description = "메뉴 설명", example = "바삭한 치킨과 신선한 야채가 들어간 버거")
    private String description;

    @Schema(description = "가격", example = "8500")
    private Integer price;

    @Schema(description = "카테고리", example = "메인메뉴")
    private String category;

    @Schema(description = "이미지 URL", example = "/images/chicken-burger.jpg")
    private String imageUrl;

    @Schema(description = "판매 가능 여부", example = "true")
    private Boolean isAvailable;

    @Schema(description = "주문 횟수", example = "25")
    private Integer orderCount;

    @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}