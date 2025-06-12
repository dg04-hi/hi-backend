package com.ktds.hi.recommend.infra.gateway.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//TODO : BASEENtity 빠져있음. 추가 필요


/**
 * 취향 태그 엔티티 클래스
 * 데이터베이스 preference_tags 테이블과 매핑되는 JPA 엔티티
 */
@Entity
@Table(name = "preference_tags")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferenceTagEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "tag_name", unique = true, nullable = false, length = 50)
	private String tagName;

	@Column(name = "category", length = 50)
	private String category;

	@Column(name = "icon", length = 10)
	private String icon;

	@Column(name = "description", length = 200)
	private String description;

	@Column(name = "is_active")
	@Builder.Default
	private Boolean isActive = true;
}
