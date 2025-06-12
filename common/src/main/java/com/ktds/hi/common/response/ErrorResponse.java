package com.ktds.hi.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import com.ktds.hi.common.dto.ResponseCode;

/**
 * 에러 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String code;
    private String message;
    private String path;
    private Long timestamp;
    private List<ValidationError> validationErrors;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private Object rejectedValue;
        private String message;
    }

    public static ErrorResponse of(ResponseCode responseCode, String path) {
        return ErrorResponse.builder()
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .path(path)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ErrorResponse of(String code, String message, String path) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .path(path)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}