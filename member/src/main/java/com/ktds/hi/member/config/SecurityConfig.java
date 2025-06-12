package com.ktds.hi.member.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.hi.common.security.JwtTokenProvider;
import com.ktds.hi.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * JWT 기반 인증 및 권한 관리 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 보안 필터 체인 설정
     * JWT 인증 방식을 사용하고 세션은 무상태로 관리
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**", "/api/members/register").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-resources/**", "/webjars/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * JWT 인증 필터 빈
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider,new ObjectMapper());
    }

    /**
     * 비밀번호 암호화 빈
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 매니저 빈
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // @Qualifier("memberJwtTokenProvider")
    // private final JwtTokenProvider jwtTokenProvider;
    // private final AuthService authService;
    //
    // /**
    //  * 보안 필터 체인 설정
    //  * JWT 인증 방식을 사용하고 세션은 무상태로 관리
    //  */
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    //         .csrf(csrf -> csrf.disable())
    //         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //         .authorizeHttpRequests(authz -> authz
    //             .requestMatchers("/api/auth/**", "/api/members/register").permitAll()
    //             .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
    //             .requestMatchers("/actuator/**").permitAll()
    //             .anyRequest().authenticated()
    //         )
    //         .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, authService),
    //                        UsernamePasswordAuthenticationFilter.class);
    //
    //     return http.build();
    // }
    //
    // /**
    //  * 비밀번호 암호화 빈
    //  */
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
    //
    // /**
    //  * 인증 매니저 빈
    //  */
    // @Bean
    // public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    //     return config.getAuthenticationManager();
    // }
}
