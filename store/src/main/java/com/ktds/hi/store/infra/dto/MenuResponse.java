package com.ktds.hi.store.infra.dto;

import com.ktds.hi.store.biz.domain.Menu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메뉴 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 응답")
public class MenuResponse {

    @Schema(description = "메뉴 ID", example = "1")
    private Long menuId;

    @Schema(description = "메뉴명", example = "김치찌개")
    private String menuName;

    @Schema(description = "메뉴 설명", example = "얼큰한 김치찌개")
    private String description;

    @Schema(description = "가격", example = "8000")
    private Integer price;

    @Schema(description = "메뉴 카테고리", example = "메인")
    private String category;

    @Schema(description = "이미지 URL")
    private String imageUrl;

    @Schema(description = "이용 가능 여부", example = "true")
    private Boolean available;

    /**
     * 도메인 객체로부터 생성
     */
    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getId())
                .menuName(menu.getMenuName())
                .description(menu.getDescription())
                .price(menu.getPrice())
                .category(menu.getCategory())
                .imageUrl(menu.getImageUrl())
                .available(menu.getAvailable())
                .build();
    }
}