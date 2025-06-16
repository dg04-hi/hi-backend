package com.ktds.hi.member.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tags")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_name", nullable = false, length = 50)
    private String tagName; // 매운맛, 깨끗한, 유제품 등

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_category", nullable = false)
    private TagCategory tagCategory; // TASTE, ATMOSPHERE, ALLERGY 등

    @Column(name = "tag_color", length = 7)
    private String tagColor; // #FF5722

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}