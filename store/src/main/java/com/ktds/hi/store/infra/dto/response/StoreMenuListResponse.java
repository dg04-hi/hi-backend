// store/src/main/java/com/ktds/hi/store/infra/dto/response/StoreMenuListResponse.java
package com.ktds.hi.store.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 매장 메뉴 목록 응답 DTO
 * 매장의 메뉴 목록을 조회할 때 사용
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매장 메뉴 목록 응답")
public class StoreMenuListResponse {

    @Schema(description = "메뉴 ID", example = "1")
    private Long menuId;

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

    /**
     * 메뉴가 판매 가능한지 확인
     */
    public boolean isMenuAvailable() {
        return isAvailable != null && isAvailable;
    }

    /**
     * 가격을 포맷된 문자열로 반환
     */
    public String getFormattedPrice() {
        if (price == null) {
            return "0원";
        }
        return String.format("%,d원", price);
    }

    /**
     * 인기 메뉴 여부 확인 (주문 횟수 기준)
     */
    public boolean isPopularMenu() {
        return orderCount != null && orderCount >= 10;
    }

    /**
     * 메뉴 상태 텍스트 반환
     */
    public String getStatusText() {
        if (isMenuAvailable()) {
            return "판매중";
        } else {
            return "품절";
        }
    }

    /**
     * 빌더 패턴을 위한 정적 메서드
     */
    public static StoreMenuListResponse of(Long menuId, String menuName, String description,
                                           Integer price, String category, String imageUrl,
                                           Boolean isAvailable, Integer orderCount) {
        return StoreMenuListResponse.builder()
                .menuId(menuId)
                .menuName(menuName)
                .description(description)
                .price(price)
                .category(category)
                .imageUrl(imageUrl)
                .isAvailable(isAvailable)
                .orderCount(orderCount)
                .build();
    }
}