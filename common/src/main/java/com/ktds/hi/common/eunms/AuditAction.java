package com.ktds.hi.common.eunms;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 감사 로그 액션 열거형
 * 시스템에서 발생하는 모든 감사 대상 액션을 정의
 */
@Getter
@RequiredArgsConstructor
public enum AuditAction {

    CREATE("생성", "새로운 데이터가 생성됨"),
    READ("조회", "데이터가 조회됨"),
    UPDATE("수정", "기존 데이터가 수정됨"),
    DELETE("삭제", "데이터가 삭제됨"),
    LOGIN("로그인", "사용자가 로그인함"),
    LOGOUT("로그아웃", "사용자가 로그아웃함"),
    REGISTER("회원가입", "새로운 사용자가 회원가입함"),
    PASSWORD_CHANGE("비밀번호 변경", "사용자가 비밀번호를 변경함"),
    PROFILE_UPDATE("프로필 수정", "사용자가 프로필을 수정함"),
    FILE_UPLOAD("파일 업로드", "파일이 업로드됨"),
    FILE_DOWNLOAD("파일 다운로드", "파일이 다운로드됨"),
    PERMISSION_GRANT("권한 부여", "사용자에게 권한이 부여됨"),
    PERMISSION_REVOKE("권한 회수", "사용자의 권한이 회수됨"),
    DATA_EXPORT("데이터 내보내기", "데이터가 내보내기됨"),
    DATA_IMPORT("데이터 가져오기", "데이터가 가져오기됨"),
    SYSTEM_ACCESS("시스템 접근", "시스템에 접근함"),
    API_CALL("API 호출", "API가 호출됨"),
    ERROR("오류", "시스템 오류가 발생함");

    private final String displayName;
    private final String description;
}