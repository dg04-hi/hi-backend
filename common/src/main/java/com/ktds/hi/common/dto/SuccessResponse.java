package com.ktds.hi.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 성공 응답 DTO 클래스
 * 간단한 성공 메시지를 반환할 때 사용
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse {

    private boolean success;
    private String message;
    private LocalDateTime timestamp;

    /**
     * 성공 응답 생성 (메시지 포함)
     */
    public static SuccessResponse of(String message) {
        return SuccessResponse.builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 성공 응답 생성 (기본 메시지)
     */
    public static SuccessResponse success() {
        return SuccessResponse.builder()
                .success(true)
                .message("성공")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 생성자 (메시지만)
     */
    public SuccessResponse(String message) {
        this.success = true;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}