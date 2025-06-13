package com.ktds.hi.store.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 수정 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매장 수정 응답")
public class StoreUpdateResponse {

    @Schema(description = "매장 ID", example = "1")
    private Long storeId;

    @Schema(description = "응답 메시지", example = "매장 정보가 성공적으로 수정되었습니다.")
    private String message;
}