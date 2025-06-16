package com.ktds.hi.member.repository.entity;

import com.ktds.hi.member.domain.TagType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 취향 태그 엔티티 클래스
 * 데이터베이스 taste_tags 테이블과 매핑되는 JPA 엔티티
 */
// TasteTagEntity.java
@Entity
@Table(name = "taste_tags")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasteTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_name", unique = true, nullable = false, length = 50)
    private String tagName; // 매운맛, 단맛, 짠맛 등

    @Column(name = "tag_color", length = 7)
    private String tagColor; // #FF5722

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_type", nullable = false)
    private TagType tagType;

    @Column(length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_category", nullable = false)
    private TagCategory tagCategory; // 추가된 필드
}