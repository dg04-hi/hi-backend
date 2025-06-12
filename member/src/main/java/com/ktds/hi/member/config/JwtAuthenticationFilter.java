// package com.ktds.hi.member.config;
//
//
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.util.StringUtils;
// import org.springframework.web.filter.OncePerRequestFilter;
//
// import java.io.IOException;
//
// import com.ktds.hi.common.security.JwtTokenProvider;
//
// /**
//  * JWT 인증 필터 클래스
//  * 요청 헤더의 JWT 토큰을 검증하고 인증 정보를 설정
//  */
// @RequiredArgsConstructor
// @Slf4j
// public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//     private final JwtTokenProvider tokenProvider;
//
//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//         FilterChain filterChain) throws ServletException, IOException {
//
//         try {
//             String token = resolveToken(request);
//
//             if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
//                 Authentication authentication = tokenProvider.getAuthentication(token);
//                 SecurityContextHolder.getContext().setAuthentication(authentication);
//                 log.debug("JWT 토큰 인증 성공: {}", authentication.getName());
//             }
//         } catch (Exception e) {
//             log.error("JWT 토큰 인증 실패", e);
//             SecurityContextHolder.clearContext();
//         }
//
//         filterChain.doFilter(request, response);
//     }
//
//     /**
//      * 요청 헤더에서 JWT 토큰 추출
//      */
//     private String resolveToken(HttpServletRequest request) {
//         String bearerToken = request.getHeader("Authorization");
//         if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//             return bearerToken.substring(7);
//         }
//         return null;
//     }
// }