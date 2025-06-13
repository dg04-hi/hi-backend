package com.ktds.hi.store.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 등록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매장 등록 응답")
public class StoreCreateResponse {

    @Schema(description = "생성된 매장 ID", example = "1")
    private Long storeId;

    @Schema(description = "매장명", example = "맛집 한번 가볼래?")
    private String storeName;

    @Schema(description = "응답 메시지", example = "매장이 성공적으로 등록되었습니다.")
    private String message;
}