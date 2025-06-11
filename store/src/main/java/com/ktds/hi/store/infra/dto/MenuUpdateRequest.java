package com.ktds.hi.store.infra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메뉴 수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuUpdateRequest {
    
    @NotBlank(message = "메뉴명은 필수입니다.")
    @Size(max = 100, message = "메뉴명은 100자를 초과할 수 없습니다.")
    private String menuName;
    
    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다.")
    private String description;
    
    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 1, message = "가격은 0보다 큰 값이어야 합니다.")
    private Integer price;
    
    private String category;
    private String imageUrl;
    private Boolean isAvailable;
}
