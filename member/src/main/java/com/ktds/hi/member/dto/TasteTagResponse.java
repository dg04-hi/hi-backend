package com.ktds.hi.member.dto;

import com.ktds.hi.member.domain.TagType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 취향 태그 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "취향 태그 정보")
public class TasteTagResponse {
    
    @Schema(description = "태그 ID")
    private Long id;
    
    @Schema(description = "태그명")
    private String tagName;
    
    @Schema(description = "태그 유형")
    private TagType tagType;
    
    @Schema(description = "태그 설명")
    private String description;
}
