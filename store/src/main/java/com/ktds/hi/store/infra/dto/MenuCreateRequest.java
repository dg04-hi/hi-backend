package com.ktds.hi.store.infra.dto;

import com.ktds.hi.store.biz.domain.Menu;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메뉴 등록 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 등록 요청")
public class MenuCreateRequest {

    @NotBlank(message = "메뉴명은 필수입니다.")
    @Schema(description = "메뉴명", example = "김치찌개")
    private String menuName;

    @Schema(description = "메뉴 설명", example = "얼큰한 김치찌개")
    private String description;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    @Schema(description = "가격", example = "8000")
    private Integer price;

    @Schema(description = "메뉴 카테고리", example = "메인")
    private String category;

    @Schema(description = "이미지 URL", example = "https://example.com/kimchi.jpg")
    private String imageUrl;

    @Schema(description = "이용 가능 여부", example = "true")
    private Boolean available = true;

    /**
     * 도메인 객체로 변환
     */
    public Menu toDomain(Long storeId) {
        return Menu.builder()
                .storeId(storeId)
                .menuName(this.menuName)
                .description(this.description)
                .price(this.price)
                .category(this.category)
                .imageUrl(this.imageUrl)
                .available(this.available != null ? this.available : true)
                .build();
    }
}