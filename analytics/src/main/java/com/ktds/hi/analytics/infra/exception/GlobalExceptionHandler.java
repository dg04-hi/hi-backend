package com.ktds.hi.analytics.infra.exception;

import com.ktds.hi.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 글로벌 예외 처리 핸들러
 * 모든 컨트롤러에서 발생하는 예외를 중앙에서 처리
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 분석 서비스 커스텀 예외 처리
     */
    @ExceptionHandler(AnalyticsException.class)
    public ResponseEntity<ErrorResponse> handleAnalyticsException(AnalyticsException ex) {
        log.error("Analytics Exception: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Analytics Error")
                .message(ex.getMessage())
                .path("/api/analytics")
                .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * 매장 정보 없음 예외 처리
     */
    @ExceptionHandler(StoreNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStoreNotFoundException(StoreNotFoundException ex) {
        log.error("Store Not Found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Store Not Found")
                .message(ex.getMessage())
                .path("/api/analytics")
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * 실행 계획 없음 예외 처리
     */
    @ExceptionHandler(ActionPlanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleActionPlanNotFoundException(ActionPlanNotFoundException ex) {
        log.error("Action Plan Not Found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Action Plan Not Found")
                .message(ex.getMessage())
                .path("/api/action-plans")
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * AI 서비스 예외 처리
     */
    @ExceptionHandler(AIServiceException.class)
    public ResponseEntity<ErrorResponse> handleAIServiceException(AIServiceException ex) {
        log.error("AI Service Exception: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error("AI Service Error")
                .message("AI 서비스 연동 중 오류가 발생했습니다.")
                .path("/api/analytics")
                .build();
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
    
    /**
     * 외부 서비스 예외 처리
     */
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceException(ExternalServiceException ex) {
        log.error("External Service Exception: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error("External Service Error")
                .message("외부 서비스 연동 중 오류가 발생했습니다.")
                .path("/api/analytics")
                .build();
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
    
    /**
     * 입력 값 검증 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation Exception: {}", ex.getMessage());
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message(errorMessage)
                .path("/api/analytics")
                .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * 바인딩 예외 처리
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        log.error("Bind Exception: {}", ex.getMessage());
        
        String errorMessage = ex.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Binding Error")
                .message(errorMessage)
                .path("/api/analytics")
                .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * 제약 조건 위반 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint Violation Exception: {}", ex.getMessage());
        
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Constraint Violation")
                .message(errorMessage)
                .path("/api/analytics")
                .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("Type Mismatch Exception: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Type Mismatch")
                .message("잘못된 파라미터 타입입니다: " + ex.getName())
                .path("/api/analytics")
                .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * 일반적인 RuntimeException 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime Exception: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("내부 서버 오류가 발생했습니다.")
                .path("/api/analytics")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * 모든 예외의 최종 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Unexpected Exception: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Unexpected Error")
                .message("예상치 못한 오류가 발생했습니다.")
                .path("/api/analytics")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
