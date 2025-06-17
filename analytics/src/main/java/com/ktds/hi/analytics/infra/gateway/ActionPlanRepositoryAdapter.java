package com.ktds.hi.analytics.infra.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.hi.analytics.biz.domain.ActionPlan;
import com.ktds.hi.analytics.biz.usecase.out.ActionPlanPort;
import com.ktds.hi.analytics.infra.gateway.entity.ActionPlanEntity;
import com.ktds.hi.analytics.infra.gateway.repository.ActionPlanJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 실행 계획 리포지토리 어댑터 클래스 (완성버전)
 * ActionPlan Port를 구현하여 데이터 영속성 기능을 제공
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActionPlanRepositoryAdapter implements ActionPlanPort {
    
    private final ActionPlanJpaRepository actionPlanJpaRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    public List<ActionPlan> findActionPlansByStoreId(Long storeId) {
        return actionPlanJpaRepository.findByStoreIdOrderByCreatedAtDesc(storeId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<ActionPlan> findActionPlanById(Long planId) {
        return actionPlanJpaRepository.findById(planId)
                .map(this::toDomain);
    }
    
    @Override
    public ActionPlan saveActionPlan(ActionPlan actionPlan) {
        ActionPlanEntity entity = toEntity(actionPlan);
        ActionPlanEntity saved = actionPlanJpaRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public void deleteActionPlan(Long planId) {
        actionPlanJpaRepository.deleteById(planId);
    }

    @Override
    public List<String> findActionPlanTitleByFeedbackId(Long feedbackId) {
        return actionPlanJpaRepository.findActionPlanTitleByFeedbackId(feedbackId);
    }

    /**
     * Entity를 Domain으로 변환
     */
    private ActionPlan toDomain(ActionPlanEntity entity) {
        return ActionPlan.builder()
                .id(entity.getId())
                .storeId(entity.getStoreId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .period(entity.getPeriod())
                .status(entity.getStatus())
                .tasks(parseTasksJson(entity.getTasksJson()))
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .completedAt(entity.getCompletedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * Domain을 Entity로 변환
     */
    private ActionPlanEntity toEntity(ActionPlan domain) {
        return ActionPlanEntity.builder()
                .id(domain.getId())
                .feedbackId(domain.getFeedbackId())
                .storeId(domain.getStoreId())
                .userId(domain.getUserId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .period(domain.getPeriod())
                .status(domain.getStatus())
                .tasksJson(parseTasksToJson(domain.getTasks()))
                .note(domain.getNote())
                .completedAt(domain.getCompletedAt())
                .build();
    }
    
    /**
     * JSON 문자열을 List로 변환
     */
    private List<String> parseTasksJson(String tasksJson) {
        if (tasksJson == null || tasksJson.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            return objectMapper.readValue(tasksJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Tasks JSON 파싱 실패: {}", tasksJson, e);
            return List.of();
        }
    }
    
    /**
     * List를 JSON 문자열로 변환
     */
    private String parseTasksToJson(List<String> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return "[]";
        }
        
        try {
            return objectMapper.writeValueAsString(tasks);
        } catch (JsonProcessingException e) {
            log.warn("Tasks JSON 직렬화 실패: {}", tasks, e);
            return "[]";
        }
    }
}
