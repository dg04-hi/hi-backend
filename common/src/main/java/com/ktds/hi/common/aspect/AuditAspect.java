package com.ktds.hi.common.aspect;

import com.ktds.hi.common.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 감사 로깅 AOP
 * 특정 메서드 실행 시 자동으로 감사 로그를 기록
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {
    
    private final AuditLogService auditLogService;
    
    /**
     * 서비스 메서드 실행 후 감사 로그 기록
     */
    @AfterReturning(
        pointcut = "execution(* com.ktds.hi.*.biz.service.*Service.create*(..))",
        returning = "result"
    )
    public void auditCreate(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            
            auditLogService.logCreate(
                className.replace("Service", ""),
                extractEntityId(result),
                String.format("%s.%s 실행", className, methodName)
            );
        } catch (Exception e) {
            log.warn("Failed to audit create operation", e);
        }
    }
    
    /**
     * 결과 객체에서 ID 추출
     */
    private String extractEntityId(Object result) {
        if (result == null) {
            return "UNKNOWN";
        }
        
        try {
            // 리플렉션을 사용하여 getId() 메서드 호출
            var method = result.getClass().getMethod("getId");
            Object id = method.invoke(result);
            return id != null ? id.toString() : "UNKNOWN";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}