package com.ktds.hi.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 성공 응답 DTO 클래스 (제네릭 지원)
 * 성공 메시지와 데이터를 반환할 때 사용
 * 기존 코드와의 호환성을 유지하면서 제네릭 타입 지원
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse<T> {

	private boolean success;
	private String message;
	private T data;
	private LocalDateTime timestamp;

	/**
	 * 성공 응답 생성 (데이터와 메시지 포함)
	 */
	public static <T> SuccessResponse<T> of(T data, String message) {
		return SuccessResponse.<T>builder()
			.success(true)
			.message(message)
			.data(data)
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 성공 응답 생성 (데이터만 포함, 기본 메시지)
	 */
	public static <T> SuccessResponse<T> of(T data) {
		return SuccessResponse.<T>builder()
			.success(true)
			.message("성공")
			.data(data)
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 성공 응답 생성 (메시지만 포함) - 기존 호환성 유지
	 */
	public static SuccessResponse<Void> of(String message) {
		return SuccessResponse.<Void>builder()
			.success(true)
			.message(message)
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 성공 응답 생성 (기본 메시지) - 기존 호환성 유지
	 */
	public static SuccessResponse<Void> success() {
		return SuccessResponse.<Void>builder()
			.success(true)
			.message("성공")
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 생성자 (메시지만) - 기존 호환성 유지
	 */
	public SuccessResponse(String message) {
		this.success = true;
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}

	/**
	 * 생성자 (데이터와 메시지)
	 */
	public SuccessResponse(T data, String message) {
		this.success = true;
		this.message = message;
		this.data = data;
		this.timestamp = LocalDateTime.now();
	}
}