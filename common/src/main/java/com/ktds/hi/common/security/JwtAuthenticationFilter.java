package com.ktds.hi.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.hi.common.constants.SecurityConstants;
import com.ktds.hi.common.response.ResponseCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 인증 필터
 * HTTP 요청에서 JWT 토큰을 추출하고 검증하여 인증 처리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                // 액세스 토큰인지 확인
                if (!jwtTokenProvider.isAccessToken(jwt)) {
                    sendErrorResponse(response, ResponseCode.UNAUTHORIZED, "액세스 토큰이 아닙니다.");
                    return;
                }
                
                String userId = jwtTokenProvider.getUserIdFromToken(jwt);
                String roles = jwtTokenProvider.getRolesFromToken(jwt);
                
                if (StringUtils.hasText(userId)) {
                    List<SimpleGrantedAuthority> authorities = Arrays.stream(roles.split(","))
                            .map(String::trim)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                    
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("JWT authentication failed", e);
            sendErrorResponse(response, ResponseCode.UNAUTHORIZED, "인증에 실패했습니다.");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 요청에서 JWT 토큰 추출
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(SecurityConstants.JWT_HEADER);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstants.JWT_PREFIX)) {
            return bearerToken.substring(SecurityConstants.JWT_PREFIX.length());
        }
        
        return null;
    }
    
    /**
     * 에러 응답 전송
     */
    private void sendErrorResponse(HttpServletResponse response, ResponseCode responseCode, String message) 
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        ApiResponse<Void> errorResponse = ApiResponse.error(responseCode, message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
    
    /**
     * 공개 경로인지 확인
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        return Arrays.stream(SecurityConstants.PUBLIC_ENDPOINTS)
                .anyMatch(pattern -> path.matches(pattern.replace("**", ".*")));
    }
}
