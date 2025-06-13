package com.ktds.hi.store.biz.usecase.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좌표 정보 값 객체
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {
    private Double latitude;
    private Double longitude;
}
