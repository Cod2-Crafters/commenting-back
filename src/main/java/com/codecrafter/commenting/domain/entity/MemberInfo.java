package com.codecrafter.commenting.domain.entity;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
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
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * 회원 일반 정보 엔티티
 * {@link MemberAuth}와 1:1 매핑
 */
@NoArgsConstructor(access = PROTECTED)
@Entity
@Getter
@Setter
@DynamicUpdate
@SQLDelete(sql = "UPDATE member_info SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
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
	@Column(nullable = true)
	private String nickname;

	@Comment("프로필 자기소개")
	@Column(nullable = true, columnDefinition = "TEXT")
	private String introduce;

	@Comment("링크1")
	@Column(name = "link_1", nullable = true)
	private String link1;

	@Comment("링크2")
	@Column(name = "link_2", nullable = true)
	private String link2;

	@Comment("링크3")
	@Column(name = "link_3", nullable = true)
	private String link3;

	@Comment("프로필 이미지 경로")
	@Column(name = "avatar_path", nullable = true)
	private String avatarPath;

	@Comment("회원 세팅")
	@OneToOne(mappedBy = "memberInfo", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private MemberSetting memberSetting;

	@Builder
	public MemberInfo(Long id, MemberAuth memberAuth, String email, String nickname, String introduce, String link1, String link2, String link3, String avatarPath) {
		this.id = id;
		this.memberAuth = memberAuth;
		this.email = email;
		this.nickname = nickname;
		this.introduce = introduce;
		this.link1 = link1;
		this.link2 = link2;
		this.link3 = link3;
		this.avatarPath = avatarPath;
	}

	public MemberInfo update(String email) {
		this.email = email;
		return this;
	}


	@PrePersist
	protected void onCreate() {
		if(this.id == null) {
			this.id = 0L;
		}
	}

	@PostPersist
	private void onLoadOrPersist() {
		if (this.nickname == null || this.nickname.isEmpty()) {
			this.nickname = this.email;
		}
	}

}
