package com.ktds.hi.analytics.infra.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;


/**
 * 실행 계획 저장 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionPlanSaveRequest {
    
    @NotNull(message = "매장 ID는 필수입니다")
    private Long storeId;
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다")
    private String title;
    
    @Size(max = 1000, message = "설명은 1000자 이하여야 합니다")
    private String description;
    
    @Size(max = 50, message = "기간은 50자 이하여야 합니다")
    private String period;
    
    private List<Long> feedbackIds;
    private List<String> tasks;
}
