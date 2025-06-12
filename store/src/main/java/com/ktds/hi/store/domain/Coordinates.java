package com.ktds.hi.store.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좌표 값 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {

    private Double latitude;
    private Double longitude;
}