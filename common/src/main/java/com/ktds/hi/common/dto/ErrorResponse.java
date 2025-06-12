package com.ktds.hi.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 에러 응답 DTO 클래스 (제네릭 지원)
 * 에러 메시지와 상세 데이터를 반환할 때 사용
 * 기존 코드와의 호환성을 유지하면서 제네릭 타입 지원
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse<T> {

	private boolean success;
	private String code;
	private String message;
	private String path;
	private T data;
	private List<ValidationError> validationErrors;
	private LocalDateTime timestamp;

	/**
	 * Validation 에러 정보
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ValidationError {
		private String field;
		private Object rejectedValue;
		private String message;
	}

	/**
	 * 에러 응답 생성 (에러 코드, 메시지, 경로, 상세 데이터 포함)
	 */
	public static <T> ErrorResponse<T> of(String code, String message, String path, T data) {
		return ErrorResponse.<T>builder()
			.success(false)
			.code(code)
			.message(message)
			.path(path)
			.data(data)
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 에러 응답 생성 (에러 코드, 메시지, 경로 포함)
	 */
	public static ErrorResponse<Void> of(String code, String message, String path) {
		return ErrorResponse.<Void>builder()
			.success(false)
			.code(code)
			.message(message)
			.path(path)
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 에러 응답 생성 (에러 코드, 메시지만 포함) - 기존 호환성 유지
	 */
	public static ErrorResponse<Void> of(String code, String message) {
		return ErrorResponse.<Void>builder()
			.success(false)
			.code(code)
			.message(message)
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * Validation 에러 응답 생성
	 */
	public static ErrorResponse<Void> ofValidation(String message, List<ValidationError> validationErrors) {
		return ErrorResponse.<Void>builder()
			.success(false)
			.code("VALIDATION_ERROR")
			.message(message)
			.validationErrors(validationErrors)
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * Validation 에러 응답 생성 (경로 포함)
	 */
	public static ErrorResponse<Void> ofValidation(String message, String path, List<ValidationError> validationErrors) {
		return ErrorResponse.<Void>builder()
			.success(false)
			.code("VALIDATION_ERROR")
			.message(message)
			.path(path)
			.validationErrors(validationErrors)
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * ResponseCode를 사용한 에러 응답 생성
	 */
	public static ErrorResponse<Void> ofResponseCode(ResponseCode responseCode, String path) {
		return ErrorResponse.<Void>builder()
			.success(false)
			.code(responseCode.getCode())
			.message(responseCode.getMessage())
			.path(path)
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 비즈니스 예외를 위한 에러 응답 생성
	 */
	public static ErrorResponse<Void> ofBusinessException(String code, String message) {
		return ErrorResponse.<Void>builder()
			.success(false)
			.code(code != null ? code : "BUSINESS_ERROR")
			.message(message)
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 내부 서버 에러 응답 생성
	 */
	public static ErrorResponse<Void> ofInternalError(String message) {
		return ErrorResponse.<Void>builder()
			.success(false)
			.code("INTERNAL_SERVER_ERROR")
			.message(message != null ? message : "내부 서버 오류가 발생했습니다.")
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 인증 에러 응답 생성
	 */
	public static ErrorResponse<Void> ofUnauthorized(String message) {
		return ErrorResponse.<Void>builder()
			.success(false)
			.code("UNAUTHORIZED")
			.message(message != null ? message : "인증이 필요합니다.")
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 권한 에러 응답 생성
	 */
	public static ErrorResponse<Void> ofForbidden(String message) {
		return ErrorResponse.<Void>builder()
			.success(false)
			.code("FORBIDDEN")
			.message(message != null ? message : "접근 권한이 없습니다.")
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 리소스 찾을 수 없음 에러 응답 생성
	 */
	public static ErrorResponse<Void> ofNotFound(String message) {
		return ErrorResponse.<Void>builder()
			.success(false)
			.code("NOT_FOUND")
			.message(message != null ? message : "요청한 리소스를 찾을 수 없습니다.")
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 잘못된 요청 에러 응답 생성
	 */
	public static ErrorResponse<Void> ofBadRequest(String message) {
		return ErrorResponse.<Void>builder()
			.success(false)
			.code("BAD_REQUEST")
			.message(message != null ? message : "잘못된 요청입니다.")
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 생성자 (메시지만) - 기존 호환성 유지
	 */
	public ErrorResponse(String message) {
		this.success = false;
		this.code = "ERROR";
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}

	/**
	 * 생성자 (코드와 메시지) - 기존 호환성 유지
	 */
	public ErrorResponse(String code, String message) {
		this.success = false;
		this.code = code;
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}

	/**
	 * 생성자 (코드, 메시지, 데이터)
	 */
	public ErrorResponse(String code, String message, T data) {
		this.success = false;
		this.code = code;
		this.message = message;
		this.data = data;
		this.timestamp = LocalDateTime.now();
	}
}