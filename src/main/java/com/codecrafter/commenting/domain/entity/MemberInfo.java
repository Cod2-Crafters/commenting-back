package com.codecrafter.commenting.domain.entity;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import lombok.Builder;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import com.codecrafter.commenting.domain.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 일반 정보 엔티티
 * {@link MemberAuth}와 1:1 매핑
 */
@NoArgsConstructor(access = PROTECTED)
@Entity
@Getter
@Setter
public class MemberInfo extends BaseEntity {

	@Id
	@Comment("PK (MemberAuth의 PK와 동일")
	private Long id;

	/* * 인증정보 */
	@MapsId
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "id")
	private MemberAuth memberAuth;

	@Comment("이메일 주소 (편의를 위해 MemberAuth와 중복)")
	private String email;

	@Comment("닉네임")
//	@Column(nullable = false)
	@Column(nullable = true)
	private String nickname;

	@Comment("프로필 자기소개")
//	@Column(nullable = false, columnDefinition = "TEXT")
	@Column(nullable = true, columnDefinition = "TEXT")
	private String introduce;

	@Comment("링크1")
	@Column(name = "link_1")
	private String link1;

	@Comment("링크2")
	@Column(name = "link_2")
	private String link2;

	@Comment("링크3")
	@Column(name = "link_3")
	private String link3;

	@Comment("프로필 이미지 경로")
	@Column(name = "avatar_path", nullable = true)
	private String avatarPath;

	@Comment("익명 댓글 허용 여부")
	@ColumnDefault("true")
//	@Column(name = "allow_anonymous", nullable = false)
	@Column(name = "allow_anonymous", nullable = true)
	private Boolean allowAnonymous = true;

	@Comment("이메일 알림 수신 여부")
	@ColumnDefault("true")
//	@Column(name = "email_notice", nullable = false)
	@Column(name = "email_notice", nullable = true)
	private Boolean emailNotice = true;

	@Builder
	public MemberInfo(Long id, MemberAuth memberAuth, String email, String nickname, String introduce, String link1, String link2, String link3, String avatarPath, Boolean allowAnonymous, Boolean emailNotice) {
		this.id = id;
		this.memberAuth = memberAuth;
		this.email = email;
		this.nickname = nickname;
		this.introduce = introduce;
		this.link1 = link1;
		this.link2 = link2;
		this.link3 = link3;
		this.avatarPath = avatarPath;
		this.allowAnonymous = allowAnonymous;
		this.emailNotice = emailNotice;
	}


	public MemberInfo update(String email) {
		this.email = email;
		return this;
	}



	public void setMemberAuth(MemberAuth memberAuth) {
		this.memberAuth = memberAuth;
	}

}
