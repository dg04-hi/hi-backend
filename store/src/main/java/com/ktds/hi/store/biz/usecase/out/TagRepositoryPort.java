package com.ktds.hi.store.biz.usecase.out;

// store/src/main/java/com/ktds/hi/store/biz/usecase/out/TagRepositoryPort.java
import com.ktds.hi.store.domain.Tag;

import java.util.List;
import java.util.Optional;

/**
 * 태그 리포지토리 포트 인터페이스
 * 태그 데이터 영속성 기능을 정의
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
public interface TagRepositoryPort {

    /**
     * 활성화된 모든 태그 조회
     */
    List<Tag> findAllActiveTags();

    /**
     * 태그 ID로 태그 조회
     */
    Optional<Tag> findTagById(Long tagId);

    /**
     * 태그명으로 태그 조회
     */
    Optional<Tag> findTagByName(String tagName);

    /**
     * 가장 많이 클릭된 상위 5개 태그 조회
     */
    List<Tag> findTopClickedTags();

    /**
     * 태그 클릭 수 증가
     */
    Tag incrementTagClickCount(Long tagId);

    /**
     * 태그 저장
     */
    Tag saveTag(Tag tag);
}
