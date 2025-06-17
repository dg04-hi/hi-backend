package com.ktds.hi.store.biz.service;

import com.ktds.hi.store.biz.usecase.in.TagUseCase;
import com.ktds.hi.store.biz.usecase.out.TagRepositoryPort;
import com.ktds.hi.store.domain.Tag;
import com.ktds.hi.store.infra.dto.response.TopClickedTagResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 태그 서비스 클래스
 * 태그 관련 비즈니스 로직을 구현
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TagService implements TagUseCase {

    private final TagRepositoryPort tagRepositoryPort;

    @Override
    public List<TopClickedTagResponse> getTopClickedTags() {
        log.info("가장 많이 클릭된 상위 5개 태그 조회 시작");

        List<Tag> topTags = tagRepositoryPort.findTopClickedTags();

        AtomicInteger rank = new AtomicInteger(1);

        List<TopClickedTagResponse> responses = topTags.stream()
                .map(tag -> TopClickedTagResponse.builder()
                        .tagId(tag.getId())
                        .tagName(tag.getTagName())
                        .tagCategory(tag.getTagCategory().name())
                        .tagColor(tag.getTagColor())
                        .clickCount(tag.getClickCount())
                        .rank(rank.getAndIncrement())
                        .build())
                .collect(Collectors.toList());

        log.info("가장 많이 클릭된 상위 5개 태그 조회 완료: count={}", responses.size());
        return responses;
    }

    @Override
    @Transactional
    public void recordTagClick(Long tagId) {
        log.info("태그 클릭 이벤트 처리 시작: tagId={}", tagId);

        Tag updatedTag = tagRepositoryPort.incrementTagClickCount(tagId);

        log.info("태그 클릭 수 증가 완료: tagId={}, clickCount={}",
                tagId, updatedTag.getClickCount());
    }
}
