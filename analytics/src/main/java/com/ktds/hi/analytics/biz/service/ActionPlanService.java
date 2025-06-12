package com.ktds.hi.analytics.biz.service;

import com.ktds.hi.analytics.biz.domain.ActionPlan;
import com.ktds.hi.analytics.biz.domain.PlanStatus;
import com.ktds.hi.analytics.biz.usecase.in.ActionPlanUseCase;
import com.ktds.hi.analytics.biz.usecase.out.ActionPlanPort;
import com.ktds.hi.analytics.biz.usecase.out.AnalyticsPort;
import com.ktds.hi.analytics.biz.usecase.out.EventPort;
import com.ktds.hi.analytics.infra.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 실행 계획 서비스 구현 클래스
 * 실행 계획 관련 비즈니스 로직을 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActionPlanService implements ActionPlanUseCase {
    
    private final ActionPlanPort actionPlanPort;
    private final AnalyticsPort analyticsPort;
    private final EventPort eventPort;
    
    @Override
    public List<ActionPlanListResponse> getActionPlans(Long storeId) {
        log.info("실행 계획 목록 조회: storeId={}", storeId);
        
        try {
            List<ActionPlan> actionPlans = actionPlanPort.findActionPlansByStoreId(storeId);
            
            return actionPlans.stream()
                    .map(plan -> ActionPlanListResponse.builder()
                            .id(plan.getId())
                            .title(plan.getTitle())
                            .status(plan.getStatus())
                            .period(plan.getPeriod())
                            .createdAt(plan.getCreatedAt())
                            .completedAt(plan.getCompletedAt())
                            .build())
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("실행 계획 목록 조회 중 오류 발생: storeId={}", storeId, e);
            throw new RuntimeException("실행 계획 조회에 실패했습니다.", e);
        }
    }
    
    @Override
    public ActionPlanDetailResponse getActionPlanDetail(Long planId) {
        log.info("실행 계획 상세 조회: planId={}", planId);
        
        try {
            ActionPlan actionPlan = actionPlanPort.findActionPlanById(planId)
                    .orElseThrow(() -> new RuntimeException("실행 계획을 찾을 수 없습니다: " + planId));
            
            return ActionPlanDetailResponse.builder()
                    .id(actionPlan.getId())
                    .storeId(actionPlan.getStoreId())
                    .title(actionPlan.getTitle())
                    .description(actionPlan.getDescription())
                    .period(actionPlan.getPeriod())
                    .status(actionPlan.getStatus())
                    .tasks(actionPlan.getTasks())
                    .note(actionPlan.getNote())
                    .createdAt(actionPlan.getCreatedAt())
                    .completedAt(actionPlan.getCompletedAt())
                    .build();
                    
        } catch (Exception e) {
            log.error("실행 계획 상세 조회 중 오류 발생: planId={}", planId, e);
            throw new RuntimeException("실행 계획 상세 조회에 실패했습니다.", e);
        }
    }
    
    @Override
    @Transactional
    public ActionPlanSaveResponse saveActionPlan(ActionPlanSaveRequest request) {
        log.info("실행 계획 저장: storeId={}, title={}", request.getStoreId(), request.getTitle());
        
        try {
            // 1. AI 피드백 존재 여부 확인
            if (request.getFeedbackIds() != null && !request.getFeedbackIds().isEmpty()) {
                validateFeedbackIds(request.getFeedbackIds());
            }
            
            // 2. 실행 계획 생성
            ActionPlan actionPlan = ActionPlan.builder()
                    .storeId(request.getStoreId())
                    .userId(request.getUserId())
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .period(request.getPeriod())
                    .status(PlanStatus.PLANNED)
                    .tasks(request.getTasks() != null ? request.getTasks() : List.of())
                    .createdAt(LocalDateTime.now())
                    .build();
            
            // 3. 저장
            ActionPlan savedPlan = actionPlanPort.saveActionPlan(actionPlan);
            
            // 4. 이벤트 발행
            eventPort.publishActionPlanCreatedEvent(savedPlan);
            
            // 5. 응답 생성
            ActionPlanSaveResponse response = ActionPlanSaveResponse.builder()
                    .id(savedPlan.getId())
                    .title(savedPlan.getTitle())
                    .status(savedPlan.getStatus())
                    .createdAt(savedPlan.getCreatedAt())
                    .build();
            
            log.info("실행 계획 저장 완료: planId={}", savedPlan.getId());
            return response;
            
        } catch (Exception e) {
            log.error("실행 계획 저장 중 오류 발생: storeId={}", request.getStoreId(), e);
            throw new RuntimeException("실행 계획 저장에 실패했습니다.", e);
        }
    }
    
    @Override
    @Transactional
    public ActionPlanCompleteResponse completeActionPlan(Long planId, ActionPlanCompleteRequest request) {
        log.info("실행 계획 완료 처리: planId={}", planId);
        
        try {
            // 1. 실행 계획 조회
            ActionPlan actionPlan = actionPlanPort.findActionPlanById(planId)
                    .orElseThrow(() -> new RuntimeException("실행 계획을 찾을 수 없습니다: " + planId));
            
            // 2. 상태 업데이트
            ActionPlan updatedPlan = ActionPlan.builder()
                    .id(actionPlan.getId())
                    .storeId(actionPlan.getStoreId())
                    .userId(actionPlan.getUserId())
                    .title(actionPlan.getTitle())
                    .description(actionPlan.getDescription())
                    .period(actionPlan.getPeriod())
                    .status(PlanStatus.COMPLETED)
                    .tasks(actionPlan.getTasks())
                    .note(request.getNote())
                    .createdAt(actionPlan.getCreatedAt())
                    .completedAt(LocalDateTime.now())
                    .build();
            
            // 3. 저장
            ActionPlan savedPlan = actionPlanPort.saveActionPlan(updatedPlan);
            
            // 4. 응답 생성
            ActionPlanCompleteResponse response = ActionPlanCompleteResponse.builder()
                    .id(savedPlan.getId())
                    .status(savedPlan.getStatus())
                    .completedAt(savedPlan.getCompletedAt())
                    .note(savedPlan.getNote())
                    .build();
            
            log.info("실행 계획 완료 처리 완료: planId={}", planId);
            return response;
            
        } catch (Exception e) {
            log.error("실행 계획 완료 처리 중 오류 발생: planId={}", planId, e);
            throw new RuntimeException("실행 계획 완료 처리에 실패했습니다.", e);
        }
    }
    
    @Override
    @Transactional
    public ActionPlanDeleteResponse deleteActionPlan(Long planId) {
        log.info("실행 계획 삭제: planId={}", planId);
        
        try {
            // 1. 실행 계획 존재 여부 확인
            ActionPlan actionPlan = actionPlanPort.findActionPlanById(planId)
                    .orElseThrow(() -> new RuntimeException("실행 계획을 찾을 수 없습니다: " + planId));
            
            // 2. 삭제
            actionPlanPort.deleteActionPlan(planId);
            
            // 3. 응답 생성
            ActionPlanDeleteResponse response = ActionPlanDeleteResponse.builder()
                    .planId(planId)
                    .deleted(true)
                    .deletedAt(LocalDateTime.now())
                    .build();
            
            log.info("실행 계획 삭제 완료: planId={}", planId);
            return response;
            
        } catch (Exception e) {
            log.error("실행 계획 삭제 중 오류 발생: planId={}", planId, e);
            throw new RuntimeException("실행 계획 삭제에 실패했습니다.", e);
        }
    }
    
    /**
     * 피드백 ID 검증
     */
    private void validateFeedbackIds(List<Long> feedbackIds) {
        for (Long feedbackId : feedbackIds) {
            // AI 피드백 존재 여부 확인 로직
            // 실제로는 AI 피드백 리포지토리에서 확인해야 함
            log.debug("피드백 ID 검증: feedbackId={}", feedbackId);
        }
    }
}
