package com.ktds.hi.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP 설정 클래스
 * AspectJ 자동 프록시를 활성화
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Configuration
@EnableAspectJAutoProxy
public class CommonAopConfig {
    // AOP 자동 설정을 위한 설정 클래스
    // @EnableAspectJAutoProxy 어노테이션으로 AspectJ 자동 프록시 활성화
}