package com.ktds.hi.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공통 API 응답 클래스
 * 모든 API 응답에 사용되는 표준 형식
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    /**
     * 성공 응답 생성
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 성공 응답 생성 (메시지 포함)
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(ResponseCode.SUCCESS.getCode())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 성공 응답 생성 (데이터 없음)
     */
    public static ApiResponse<Void> success() {
        return ApiResponse.<Void>builder()
                .success(true)
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 에러 응답 생성
     */
    public static <T> ApiResponse<T> error(ResponseCode responseCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 에러 응답 생성 (메시지 포함)
     */
    public static <T> ApiResponse<T> error(ResponseCode responseCode, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(responseCode.getCode())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 에러 응답 생성 (코드와 메시지 직접 지정)
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}