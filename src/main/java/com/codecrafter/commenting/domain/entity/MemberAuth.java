package com.codecrafter.commenting.domain.entity;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import com.codecrafter.commenting.domain.entity.base.BaseEntity;
import com.codecrafter.commenting.domain.entity.base.Provider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 인증 정보 엔티티
 * {@link MemberInfo}와 1:1 매핑
 */
@NoArgsConstructor(access = PROTECTED)
@Entity
@Getter
public class MemberAuth extends BaseEntity {

	@Id
	@Comment("PK")
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Comment("이메일 주소 (로그인ID)")
	@Column(nullable = false)
	private String email;

	@Comment("로그인 provider")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Provider provider;

	@Comment("비밀번호")
	@Column(nullable = false)
	private String password;

	@Comment("계정 활성화 여부")
	@ColumnDefault("false")
	@Column(name = "is_enabled", nullable = false)
	private Boolean isEnabled = false;

	@Comment("회원 정보")
	@OneToOne(mappedBy = "memberAuth", fetch = FetchType.LAZY)
	private MemberInfo memberInfo;

}

