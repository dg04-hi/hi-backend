package com.ktds.hi.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 기본 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseResponse {

    private Boolean success;
    private String message;
    private Long timestamp;

    public BaseResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}
