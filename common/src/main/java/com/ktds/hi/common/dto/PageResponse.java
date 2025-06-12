package com.ktds.hi.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 페이징 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Boolean first;
    private Boolean last;
    private Boolean empty;

    /**
     * PageResponse 생성을 위한 정적 팩토리 메서드
     */
    public static <T> PageResponse<T> of(List<T> content, Integer page, Integer size, Long totalElements) {
        Integer totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<T>builder()
            .content(content)
            .page(page)
            .size(size)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .first(page == 0)
            .last(page >= totalPages - 1)
            .empty(content.isEmpty())
            .build();
    }
}