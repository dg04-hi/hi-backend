package com.ktds.hi.store.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 매장 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매장 수정 요청")
public class StoreUpdateRequest {

    @NotBlank(message = "매장명은 필수입니다.")
    @Size(max = 100, message = "매장명은 100자를 초과할 수 없습니다.")
    @Schema(description = "매장명", example = "맛집 한번 가볼래?")
    private String storeName;

    @NotBlank(message = "주소는 필수입니다.")
    @Schema(description = "매장 주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "매장 설명", example = "맛있는 한식당입니다.")
    private String description;

    @Schema(description = "전화번호", example = "02-1234-5678")
    private String phone;

    @Schema(description = "운영시간", example = "월-금 09:00-21:00, 토-일 10:00-20:00")
    private String operatingHours;

    @Schema(description = "매장 태그 목록", example = "[\"맛집\", \"혼밥\", \"가성비\"]")
    private List<String> tags;

    @Schema(description = "매장 이미지")
    private String imageUrl;
}
