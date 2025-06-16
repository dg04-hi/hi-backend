package com.ktds.hi.common.security;

import jakarta.servlet.http.HttpServletRequest;
import com.ktds.hi.common.exception.BusinessException;
import com.ktds.hi.common.constants.SecurityConstants;
import jakarta.servlet.http.HttpServletRequest;
import com.ktds.hi.common.exception.BusinessException;
import com.ktds.hi.common.dto.ResponseCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성 및 검증 제공자
 * JWT 토큰의 생성, 파싱, 검증 기능을 담당
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final JwtParser jwtParser;

    public JwtTokenProvider(
            @Value("${app.jwt.secret-key:hiorder-secret-key-for-jwt-token-generation-2024}") String secretKeyString,
            @Value("${app.jwt.access-token-validity:3600000}") long accessTokenValidity,
            @Value("${app.jwt.refresh-token-validity:604800000}") long refreshTokenValidity) {

        // 비밀키 생성 (256비트 이상이어야 함)
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            // 32바이트 미만이면 패딩
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
            keyBytes = paddedKey;
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);

        this.accessTokenValidityInMilliseconds = accessTokenValidity;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidity;

        // JwtParser 초기화 (deprecated 메서드 대신 새로운 방식 사용)
        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }
    /**
     * HTTP 요청에서 점주 정보 추출
     */
    public Long extractOwnerInfo(HttpServletRequest request) {
        try {
            // Authorization 헤더에서 토큰 추출
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new BusinessException("UNAUTHORIZED", "인증 토큰이 필요합니다.");
            }

            String token = authHeader.substring(7); // "Bearer " 제거

            // 토큰 유효성 검증
            if (!validateToken(token)) {
                throw new BusinessException("UNAUTHORIZED", "유효하지 않은 토큰입니다.");
            }

            // 토큰에서 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                throw new BusinessException("UNAUTHORIZED", "토큰에서 사용자 정보를 찾을 수 없습니다.");
            }

            // 토큰에서 권한 정보 추출
            String roles = getRolesFromToken(token);
            if (roles == null || !roles.contains("OWNER")) {
                throw new BusinessException("FORBIDDEN", "점주 권한이 필요합니다.");
            }

            log.debug("점주 정보 추출 완료: ownerId={}", userId);
            return Long.parseLong(userId);

        } catch (NumberFormatException e) {
            log.error("사용자 ID 형변환 실패: {}", e.getMessage());
            throw new BusinessException("UNAUTHORIZED", "잘못된 사용자 ID 형식입니다.");
        } catch (BusinessException e) {
            throw e; // 비즈니스 예외는 그대로 전파
        } catch (Exception e) {
            log.error("점주 정보 추출 중 오류 발생: {}", e.getMessage(), e);
            throw new BusinessException("UNAUTHORIZED", "인증 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * HTTP 요청에서 사용자 정보 추출 (일반 사용자용)
     */
    public Long extractUserInfo(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new BusinessException("UNAUTHORIZED", "인증 토큰이 필요합니다.");
            }

            String token = authHeader.substring(7);

            if (!validateToken(token)) {
                throw new BusinessException("UNAUTHORIZED", "유효하지 않은 토큰입니다.");
            }

            String userId = getUserIdFromToken(token);
            if (userId == null) {
                throw new BusinessException("UNAUTHORIZED", "토큰에서 사용자 정보를 찾을 수 없습니다.");
            }

            return Long.parseLong(userId);

        } catch (NumberFormatException e) {
            throw new BusinessException("UNAUTHORIZED", "잘못된 사용자 ID 형식입니다.");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("사용자 정보 추출 중 오류 발생: {}", e.getMessage(), e);
            throw new BusinessException("UNAUTHORIZED", "인증 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * HttpServletRequest에서 점주 ID 추출
     *
     * @param request HTTP 요청 객체
     * @return 점주 ID
     */
    public Long extractOwnerIdFromRequest(HttpServletRequest request) {
        try {
            String token = getJwtFromRequest(request);
            if (token == null) {
                throw new BusinessException(ResponseCode.UNAUTHORIZED, "토큰이 필요합니다.");
            }

            if (!validateToken(token)) {
                throw new BusinessException(ResponseCode.INVALID_TOKEN, "유효하지 않은 토큰입니다.");
            }

            String userId = getUserIdFromToken(token);
            if (userId == null || userId.trim().isEmpty()) {
                throw new BusinessException(ResponseCode.INVALID_TOKEN, "토큰에서 사용자 정보를 찾을 수 없습니다.");
            }

            return Long.parseLong(userId);
        } catch (NumberFormatException e) {
            throw new BusinessException(ResponseCode.INVALID_TOKEN, "유효하지 않은 사용자 ID 형식입니다.");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("토큰에서 점주 ID 추출 실패", e);
            throw new BusinessException(ResponseCode.UNAUTHORIZED, "인증에 실패했습니다.");
        }
    }

    /**
     * 요청에서 JWT 토큰 추출
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * 액세스 토큰 생성
     */
    public String createAccessToken(String userId, Collection<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(userId)
                .claim("type", SecurityConstants.TOKEN_TYPE_ACCESS)
                .claim("roles", String.join(",", roles))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 리프레시 토큰 생성
     */
    public String createRefreshToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(userId)
                .claim("type", SecurityConstants.TOKEN_TYPE_REFRESH)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            log.error("토큰에서 사용자 ID 추출 실패", e);
            return null;
        }
    }

    /**
     * 토큰에서 역할 정보 추출
     */
    public String getRolesFromToken(String token) {
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            return claims.get("roles", String.class);
        } catch (Exception e) {
            log.error("토큰에서 역할 정보 추출 실패", e);
            return "";
        }
    }

    /**
     * 토큰에서 인증 객체 생성
     */
    public Authentication getAuthentication(String token) {
        try {
            String userId = getUserIdFromToken(token);
            String roles = getRolesFromToken(token);

            if (userId != null) {
                List<SimpleGrantedAuthority> authorities = Arrays.stream(roles.split(","))
                        .filter(role -> !role.trim().isEmpty())
                        .map(String::trim)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                return new UsernamePasswordAuthenticationToken(userId, null, authorities);
            }
        } catch (Exception e) {
            log.error("토큰에서 인증 객체 생성 실패", e);
        }
        return null;
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            log.debug("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.debug("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.debug("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.debug("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.debug("JWT token validation failed: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 액세스 토큰인지 확인
     */
    public boolean isAccessToken(String token) {
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            String tokenType = claims.get("type", String.class);
            return SecurityConstants.TOKEN_TYPE_ACCESS.equals(tokenType);
        } catch (Exception e) {
            log.debug("토큰 타입 확인 실패", e);
            return false;
        }
    }

    /**
     * 리프레시 토큰인지 확인
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            String tokenType = claims.get("type", String.class);
            return SecurityConstants.TOKEN_TYPE_REFRESH.equals(tokenType);
        } catch (Exception e) {
            log.debug("토큰 타입 확인 실패", e);
            return false;
        }
    }

    /**
     * 토큰 만료 시간 가져오기
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("토큰에서 만료 시간 추출 실패", e);
            return null;
        }
    }

    /**
     * 토큰이 곧 만료되는지 확인 (15분 이내)
     */
    public boolean isTokenExpiringSoon(String token) {
        try {
            Date expirationDate = getExpirationDateFromToken(token);
            if (expirationDate == null) {
                return true;
            }

            long now = System.currentTimeMillis();
            long expiration = expirationDate.getTime();
            long fifteenMinutes = 15 * 60 * 1000; // 15분

            return (expiration - now) < fifteenMinutes;
        } catch (Exception e) {
            log.debug("토큰 만료 임박 확인 실패", e);
            return true;
        }
    }
}