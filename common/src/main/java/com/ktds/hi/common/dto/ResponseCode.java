package com.ktds.hi.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * API 응답 코드 열거형
 * 표준화된 응답 코드와 메시지 관리
 */
@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // 성공
    SUCCESS("200", "성공"),
    CREATED("201", "생성됨"),

    // 클라이언트 오류 (4xx)
    BAD_REQUEST("400", "잘못된 요청"),
    UNAUTHORIZED("401", "인증 실패"),
    FORBIDDEN("403", "접근 권한 없음"),
    NOT_FOUND("404", "리소스를 찾을 수 없음"),
    METHOD_NOT_ALLOWED("405", "허용되지 않은 메서드"),
    CONFLICT("409", "리소스 충돌"),
    UNPROCESSABLE_ENTITY("422", "처리할 수 없는 엔티티"),

    // 서버 오류 (5xx)
    INTERNAL_SERVER_ERROR("500", "내부 서버 오류"),
    BAD_GATEWAY("502", "잘못된 게이트웨이"),
    SERVICE_UNAVAILABLE("503", "서비스 사용 불가"),

    // 비즈니스 로직 오류
    INVALID_INPUT("1001", "입력값이 올바르지 않습니다"),
    DUPLICATE_USERNAME("1002", "이미 사용중인 아이디입니다"),
    DUPLICATE_NICKNAME("1003", "이미 사용중인 닉네임입니다"),
    INVALID_CREDENTIALS("1004", "아이디 또는 비밀번호가 일치하지 않습니다"),
    ACCESS_DENIED("1005", "접근 권한이 없습니다"),
    EXPIRED_TOKEN("1006", "토큰이 만료되었습니다"),
    INVALID_TOKEN("1007", "유효하지 않은 토큰입니다"),
    USER_NOT_FOUND("1008", "사용자를 찾을 수 없습니다"),
    STORE_NOT_FOUND("1009", "매장을 찾을 수 없습니다"),
    REVIEW_NOT_FOUND("1010", "리뷰를 찾을 수 없습니다"),

    // 파일 관련 오류
    FILE_UPLOAD_ERROR("2001", "파일 업로드 실패"),
    FILE_NOT_FOUND("2002", "파일을 찾을 수 없습니다"),
    INVALID_FILE_FORMAT("2003", "지원하지 않는 파일 형식입니다"),
    FILE_SIZE_EXCEEDED("2004", "파일 크기가 허용 범위를 초과했습니다"),

    // 외부 서비스 오류
    SMS_SEND_ERROR("3001", "SMS 전송 실패"),
    EMAIL_SEND_ERROR("3002", "이메일 전송 실패"),
    EXTERNAL_API_ERROR("3003", "외부 API 호출 실패"),

    // 인증인가 관련
    INSUFFICIENT_PRIVILEGES("INSUFFICIENT_PRIVILEGES", "권한 부족"),

    // 리소스 관련
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "리소스를 찾을 수 없음"),
    RESOURCE_ALREADY_EXISTS("RESOURCE_ALREADY_EXISTS", "리소스가 이미 존재함"),
    RESOURCE_LOCKED("RESOURCE_LOCKED", "리소스가 잠겨있음"),

    // 파일 처리 관련
    FILE_TOO_LARGE("FILE_TOO_LARGE", "파일 크기가 너무 큼"),
    UNSUPPORTED_FILE_TYPE("UNSUPPORTED_FILE_TYPE", "지원하지 않는 파일 형식"),
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "파일 업로드 실패"),

    // 결제 관련
    PAYMENT_FAILED("PAYMENT_FAILED", "결제 실패"),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "잔액 부족"),
    PAYMENT_CANCELLED("PAYMENT_CANCELLED", "결제 취소됨"),

    // AI 분석 관련
    AI_ANALYSIS_FAILED("AI_ANALYSIS_FAILED", "AI 분석 실패"),
    AI_SERVICE_UNAVAILABLE("AI_SERVICE_UNAVAILABLE", "AI 서비스 이용 불가"),
    ANALYSIS_IN_PROGRESS("ANALYSIS_IN_PROGRESS", "분석 진행 중");

    private final String code;
    private final String message;
}