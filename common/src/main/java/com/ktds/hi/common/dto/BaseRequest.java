package com.ktds.hi.common.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 기본 요청 DTO
 */
@Getter
@Setter
public abstract class BaseRequest {

    private String requestId;
    private Long timestamp;

    public BaseRequest() {
        this.timestamp = System.currentTimeMillis();
    }
}
