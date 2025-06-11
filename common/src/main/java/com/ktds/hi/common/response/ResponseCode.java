package com.ktds.hi.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 응답 코드 열거형
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {

    // 성공
    SUCCESS("200", "성공"),

    // 클라이언트 에러
    BAD_REQUEST("400", "잘못된 요청"),
    UNAUTHORIZED("401", "인증 실패"),
    FORBIDDEN("403", "접근 권한 없음"),
    NOT_FOUND("404", "리소스를 찾을 수 없음"),
    CONFLICT("409", "리소스 충돌"),
    VALIDATION_ERROR("422", "입력값 검증 실패"),

    // 서버 에러
    INTERNAL_SERVER_ERROR("500", "내부 서버 오류"),
    SERVICE_UNAVAILABLE("503", "서비스 이용 불가");

    private final String code;
    private final String message;
}