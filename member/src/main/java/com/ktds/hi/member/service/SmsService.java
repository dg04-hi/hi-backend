package com.ktds.hi.member.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * SMS 서비스 클래스
 * SMS 발송 기능을 담당 (실제 구현은 외부 SMS API 연동 필요)
 */
@Service
@Slf4j
public class SmsService {
    
    @Value("${sms.api-key:}")
    private String apiKey;
    
    @Value("${sms.api-secret:}")
    private String apiSecret;
    
    @Value("${sms.from-number:}")
    private String fromNumber;
    
    /**
     * SMS 인증번호 발송
     */
    public void sendVerificationCode(String phone, String code) {
        String message = String.format("[하이오더] 인증번호는 %s입니다.", code);
        
        // TODO: 실제 SMS API 연동 구현
        log.info("SMS 발송: phone={}, message={}", phone, message);
    }
    
    /**
     * 임시 비밀번호 SMS 발송
     */
    public void sendTempPassword(String phone, String tempPassword) {
        String message = String.format("[하이오더] 임시 비밀번호는 %s입니다. 로그인 후 비밀번호를 변경해주세요.", tempPassword);
        
        // TODO: 실제 SMS API 연동 구현
        log.info("임시 비밀번호 SMS 발송: phone={}, tempPassword={}", phone, tempPassword);
    }
}
