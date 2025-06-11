package com.ktds.hi.common.constants;

/**
 * 메시지 코드 상수
 */
public class MessageCode {

    public static final String SUCCESS_CREATE = "성공적으로 생성되었습니다";
    public static final String SUCCESS_UPDATE = "성공적으로 수정되었습니다";
    public static final String SUCCESS_DELETE = "성공적으로 삭제되었습니다";
    public static final String INVALID_INPUT = "입력값이 올바르지 않습니다";
    public static final String DUPLICATE_USERNAME = "이미 사용중인 아이디입니다";
    public static final String DUPLICATE_NICKNAME = "이미 사용중인 닉네임입니다";
    public static final String INVALID_CREDENTIALS = "아이디 또는 비밀번호가 일치하지 않습니다";
    public static final String ACCESS_DENIED = "접근 권한이 없습니다";

    private MessageCode() {
        // 유틸리티 클래스
    }
}