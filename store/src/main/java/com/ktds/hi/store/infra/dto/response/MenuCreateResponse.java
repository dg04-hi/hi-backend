package com.ktds.hi.store.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 메뉴 등록 요청 DTO
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 등록 응답")
public class MenuCreateResponse {

    @Schema(description = "생성된 메뉴 ID", example = "1")
    private Long menuId;

    @Schema(description = "응답 메시지", example = "메뉴가 성공적으로 등록되었습니다.")
    private String message;
}
