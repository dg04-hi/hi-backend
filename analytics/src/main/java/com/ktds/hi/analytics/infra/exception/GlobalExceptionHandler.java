package com.ktds.hi.analytics.infra.exception;

import com.ktds.hi.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 글로벌 예외 처리 핸들러 (수정 완료)
 * 모든 컨트롤러에서 발생하는 예외를 중앙에서 처리
 * 새로운 ErrorResponse 필드 구조에 맞게 수정
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 분석 서비스 커스텀 예외 처리
     */
    @ExceptionHandler(AnalyticsException.class)
    public ResponseEntity<ErrorResponse<Void>> handleAnalyticsException(
        AnalyticsException ex, HttpServletRequest request) {
        log.error("Analytics Exception: {}", ex.getMessage(), ex);

        ErrorResponse<Void> errorResponse = ErrorResponse.of(
            ex.getErrorCode(),
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 매장 정보 없음 예외 처리
     */
    @ExceptionHandler(StoreNotFoundException.class)
    public ResponseEntity<ErrorResponse<Void>> handleStoreNotFoundException(
        StoreNotFoundException ex, HttpServletRequest request) {
        log.error("Store Not Found: {}", ex.getMessage());

        ErrorResponse<Void> errorResponse = ErrorResponse.of(
            "STORE_NOT_FOUND",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * 실행 계획 없음 예외 처리
     */
    @ExceptionHandler(ActionPlanNotFoundException.class)
    public ResponseEntity<ErrorResponse<Void>> handleActionPlanNotFoundException(
        ActionPlanNotFoundException ex, HttpServletRequest request) {
        log.error("Action Plan Not Found: {}", ex.getMessage());

        ErrorResponse<Void> errorResponse = ErrorResponse.of(
            "ACTION_PLAN_NOT_FOUND",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * AI 서비스 예외 처리
     */
    @ExceptionHandler(AIServiceException.class)
    public ResponseEntity<ErrorResponse<Void>> handleAIServiceException(
        AIServiceException ex, HttpServletRequest request) {
        log.error("AI Service Exception: {}", ex.getMessage(), ex);

        ErrorResponse<Void> errorResponse = ErrorResponse.of(
            "AI_SERVICE_ERROR",
            "AI 서비스 연동 중 오류가 발생했습니다.",
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    /**
     * 외부 서비스 예외 처리
     */
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse<Void>> handleExternalServiceException(
        ExternalServiceException ex, HttpServletRequest request) {
        log.error("External Service Exception: {}", ex.getMessage(), ex);

        ErrorResponse<Void> errorResponse = ErrorResponse.of(
            ex.getErrorCode(),
            "외부 서비스 연동 중 오류가 발생했습니다.",
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    /**
     * 입력 값 검증 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<Void>> handleValidationException(
        MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation Exception: {}", ex.getMessage());

        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> ErrorResponse.ValidationError.builder()
                .field(error.getField())
                .rejectedValue(error.getRejectedValue())
                .message(error.getDefaultMessage())
                .build())
            .collect(Collectors.toList());

        ErrorResponse<Void> errorResponse = ErrorResponse.ofValidation(
            "입력값 검증 실패",
            request.getRequestURI(),
            validationErrors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 바인딩 예외 처리
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse<Void>> handleBindException(
        BindException ex, HttpServletRequest request) {
        log.error("Bind Exception: {}", ex.getMessage());

        List<ErrorResponse.ValidationError> validationErrors = ex.getFieldErrors()
            .stream()
            .map(error -> ErrorResponse.ValidationError.builder()
                .field(error.getField())
                .rejectedValue(error.getRejectedValue())
                .message(error.getDefaultMessage())
                .build())
            .collect(Collectors.toList());

        ErrorResponse<Void> errorResponse = ErrorResponse.ofValidation(
            "바인딩 실패",
            request.getRequestURI(),
            validationErrors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 제약 조건 위반 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse<Void>> handleConstraintViolationException(
        ConstraintViolationException ex, HttpServletRequest request) {
        log.error("Constraint Violation Exception: {}", ex.getMessage());

        String errorMessage = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));

        ErrorResponse<Void> errorResponse = ErrorResponse.of(
            "CONSTRAINT_VIOLATION",
            errorMessage,
            request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse<Void>> handleTypeMismatchException(
        MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.error("Type Mismatch Exception: {}", ex.getMessage());

        ErrorResponse<Void> errorResponse = ErrorResponse.of(
            "TYPE_MISMATCH",
            "잘못된 파라미터 타입입니다: " + ex.getName(),
            request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 일반적인 RuntimeException 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse<Void>> handleRuntimeException(
        RuntimeException ex, HttpServletRequest request) {
        log.error("Runtime Exception: {}", ex.getMessage(), ex);

        ErrorResponse<Void> errorResponse = ErrorResponse.of(
            "RUNTIME_ERROR",
            "내부 서버 오류가 발생했습니다.",
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 모든 예외의 최종 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<Void>> handleAllExceptions(
        Exception ex, HttpServletRequest request) {
        log.error("Unexpected Exception: {}", ex.getMessage(), ex);

        ErrorResponse<Void> errorResponse = ErrorResponse.ofInternalError(
            "예상치 못한 오류가 발생했습니다."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}