package com.ktds.hi.store.infra.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 가용성 변경 요청")
public class MenuAvailabilityRequest {

    @NotNull(message = "가용성 여부는 필수입니다.")
    @Schema(description = "판매 가능 여부", example = "true", required = true)
    private Boolean isAvailable;

    @Schema(description = "변경 사유", example = "재료 소진으로 인한 일시 판매 중단")
    private String reason;
}