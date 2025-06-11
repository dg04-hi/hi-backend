package com.ktds.hi.common.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 커스텀 사용자 상세 정보
 * Spring Security UserDetails 인터페이스 구현
 */
@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    
    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final List<String> roles;
    private final boolean enabled;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * 기본 생성자 (활성화된 사용자)
     */
    public static CustomUserDetails of(Long id, String username, String email, String password, List<String> roles) {
        return new CustomUserDetails(
                id, username, email, password, roles,
                true, true, true, true
        );
    }
    
    /**
     * 상태를 지정한 생성자
     */
    public static CustomUserDetails of(Long id, String username, String email, String password, List<String> roles,
                                       boolean enabled, boolean accountNonExpired, 
                                       boolean accountNonLocked, boolean credentialsNonExpired) {
        return new CustomUserDetails(
                id, username, email, password, roles,
                enabled, accountNonExpired, accountNonLocked, credentialsNonExpired
        );
    }
}
