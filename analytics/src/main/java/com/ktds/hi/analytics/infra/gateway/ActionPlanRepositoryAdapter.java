package com.ktds.hi.analytics.infra.gateway;

import com.ktds.hi.analytics.biz.domain.ActionPlan;
import com.ktds.hi.analytics.biz.domain.PlanStatus;
import com.ktds.hi.analytics.biz.usecase.out.ActionPlanPort;
import com.ktds.hi.analytics.infra.gateway.entity.ActionPlanEntity;
import com.ktds.hi.analytics.infra.gateway.repository.ActionPlanJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 실행 계획 리포지토리 어댑터 클래스
 * ActionPlan Port를 구현하여 데이터 영속성 기능을 제공
 */
@Component
@RequiredArgsConstructor
public class ActionPlanRepositoryAdapter implements ActionPlanPort {
    
    private final ActionPlanJpaRepository actionPlanJpaRepository;
    
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
                .tasks(entity.getTasksJson() != null ? parseTasksJson(entity.getTasksJson()) : List.of())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .completedAt(entity.getCompletedAt())
                .build();
    }
    
    /**
     * Domain을 Entity로 변환
     */
    private ActionPlanEntity toEntity(ActionPlan domain) {
        return ActionPlanEntity.builder()
                .id(domain.getId())
                .storeId(domain.getStoreId())
                .userId(domain.getUserId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .period(domain.getPeriod())
                .status(domain.getStatus())
                .tasksJson(domain.getTasks() != null ? toTasksJsonString(domain.getTasks()) : "[]")
                .note(domain.getNote())
                .createdAt(domain.getCreatedAt())
                .completedAt(domain.getCompletedAt())
                .build();
    }
    
    /**
     * JSON 문자열을 Tasks List로 파싱
     */
    private List<String> parseTasksJson(String json) {
        if (json == null || json.trim().isEmpty() || "[]".equals(json.trim())) {
            return List.of();
        }
        return Arrays.asList(json.replace("[", "").replace("]", "").replace("\"", "").split(","));
    }
    
    /**
     * Tasks List를 JSON 문자열로 변환
     */
    private String toTasksJsonString(List<String> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return "[]";
        }
        return "[\"" + String.join("\",\"", tasks) + "\"]";
    }
}
