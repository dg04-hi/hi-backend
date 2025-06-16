/*
 */
package com.ktds.hi.member.repository.jpa;

import com.ktds.hi.member.domain.TagType;
import com.ktds.hi.member.repository.entity.TagCategory;
import com.ktds.hi.member.repository.entity.TasteTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TasteTagRepository extends JpaRepository<TasteTagEntity, Long> {

    List<TasteTagEntity> findByTagTypeAndIsActiveTrue(TagType tagType);

    List<TasteTagEntity> findByIsActiveTrue();

    List<TasteTagEntity> findByTagNameIn(List<String> tagNames);

    List<TasteTagEntity> findByTagCategoryAndIsActiveTrue(TagCategory tagCategory);

    List<TasteTagEntity> findByIsActiveTrueOrderBySortOrder();

    Optional<TasteTagEntity> findByTagNameAndTagCategory(String tagName, TagCategory tagCategory);

    boolean existsByTagNameAndTagCategory(String tagName, TagCategory tagCategory);
}