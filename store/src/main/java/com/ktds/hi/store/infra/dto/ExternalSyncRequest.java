package com.ktds.hi.store.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 외부 플랫폼 동기화 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "외부 플랫폼 동기화 요청")
public class ExternalSyncRequest {

    @NotBlank(message = "플랫폼 정보는 필수입니다")
    @Schema(description = "플랫폼 타입", example = "NAVER", allowableValues = {"NAVER", "KAKAO", "GOOGLE", "HIORDER"})
    private String platform;

    @NotBlank(message = "외부 매장 ID는 필수입니다")
    @Schema(description = "외부 플랫폼의 매장 ID", example = "naver_store_12345")
    private String externalStoreId;

    @Schema(description = "동기화 옵션", example = "FULL")
    private String syncOption = "FULL"; // FULL, INCREMENTAL

    @Schema(description = "시작 날짜", example = "2024-01-01")
    private String startDate;

    @Schema(description = "종료 날짜", example = "2024-12-31")
    private String endDate;
}
