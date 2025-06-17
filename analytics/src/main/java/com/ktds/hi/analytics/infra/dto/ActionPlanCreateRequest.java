package com.ktds.hi.analytics.infra.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 실행계획 생성요청
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ActionPlanCreateRequest {

	private List<String> actionPlanSelect;
}
