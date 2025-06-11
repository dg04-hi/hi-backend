package com.ktds.hi.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 보안 관련 유틸리티 클래스
 * 현재 인증된 사용자 정보 조회 등의 기능을 제공
 */
public final class SecurityUtil {
    
    private SecurityUtil() {
        // 인스턴스 생성 방지
    }
    
    /**
     * 현재 인증된 사용자 ID 조회
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentAuthentication()
                .map(Authentication::getName)
                .map(Long::parseLong);
    }
    
    /**
     * 현재 인증된 사용자명 조회
     */
    public static Optional<String> getCurrentUsername() {
        return getCurrentAuthentication()
                .map(Authentication::getName);
    }
    
    /**
     * 현재 인증된 사용자의 권한 조회
     */
    public static Set<String> getCurrentUserRoles() {
        return getCurrentAuthentication()
                .map(auth -> auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .orElse(Set.of());
    }
    
    /**
     * 현재 사용자가 특정 권한을 가지는지 확인
     */
    public static boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }
    
    /**
     * 현재 사용자가 여러 권한 중 하나라도 가지는지 확인
     */
    public static boolean hasAnyRole(String... roles) {
        Set<String> userRoles = getCurrentUserRoles();
        for (String role : roles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 현재 사용자가 모든 권한을 가지는지 확인
     */
    public static boolean hasAllRoles(String... roles) {
        Set<String> userRoles = getCurrentUserRoles();
        for (String role : roles) {
            if (!userRoles.contains(role)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 현재 사용자가 인증되었는지 확인
     */
    public static boolean isAuthenticated() {
        return getCurrentAuthentication()
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }
    
    /**
     * 현재 사용자가 익명 사용자인지 확인
     */
    public static boolean isAnonymous() {
        return !isAuthenticated();
    }
    
    /**
     * 현재 Authentication 객체 조회
     */
    public static Optional<Authentication> getCurrentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication);
    }
    
    /**
     * 현재 사용자가 리소스 소유자인지 확인
     */
    public static boolean isOwner(Long resourceOwnerId) {
        return getCurrentUserId()
                .map(currentUserId -> currentUserId.equals(resourceOwnerId))
                .orElse(false);
    }
    
    /**
     * 현재 사용자가 관리자인지 확인
     */
    public static boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }
    
    /**
     * 현재 사용자가 매장 소유자인지 확인
     */
    public static boolean isStoreOwner() {
        return hasRole("ROLE_STORE_OWNER");
    }
    
    /**
     * 현재 사용자가 일반 사용자인지 확인
     */
    public static boolean isUser() {
        return hasRole("ROLE_USER");
    }
}
