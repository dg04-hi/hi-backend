package com.ktds.hi.common.audit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA Auditing을 위한 사용자 정보 제공자
 */
@Component
@ConditionalOnClass(AuditorAware.class)  // 👈 이 어노테이션 추가
public class CustomAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("system");
        }

        return Optional.of(authentication.getName());
    }
}