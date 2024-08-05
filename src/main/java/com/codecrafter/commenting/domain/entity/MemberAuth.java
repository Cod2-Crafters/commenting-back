package com.codecrafter.commenting.domain.entity;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.codecrafter.commenting.domain.request.SignUpRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.PrePersist;
import java.util.Collection;
import java.util.Collections;
import lombok.Builder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import com.codecrafter.commenting.domain.entity.base.BaseEntity;
import com.codecrafter.commenting.domain.enumeration.Provider;

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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * 회원 인증 정보 엔티티
 * {@link MemberInfo}와 1:1 매핑
 */
@NoArgsConstructor(access = PROTECTED)
@Entity
@Getter
public class MemberAuth extends BaseEntity implements UserDetails {

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
	@Column(nullable = true)
	private String password;

	@Comment("계정 활성화 여부")
	@ColumnDefault("false")
	@Column(name = "is_enabled", nullable = false)
	private Boolean isEnabled = false;

	@Comment("회원 정보")
	@OneToOne(mappedBy = "memberAuth", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private MemberInfo memberInfo;

	@Builder
	public MemberAuth(Long id, String email, Provider provider, String password, Boolean isEnabled, MemberInfo memberInfo) {
		this.id = id;
		this.email = email;
		this.provider = provider;
		this.password = password;
		this.isEnabled = isEnabled;
		this.memberInfo = memberInfo;
		if (memberInfo != null) {
			memberInfo.setMemberAuth(this);
		}
	}

	public MemberAuth update(String email) {
		this.email = email;
		return this;
	}

	public static MemberAuth from(SignUpRequest request) {
		return MemberAuth.builder()
			.email(request.email())
			.provider(request.provider())
			.password(request.password())
			//.createdAt(LocalDateTime.now())
			.build();
	}

	@PrePersist
	protected void onCreate() {
		if (this.isEnabled == null) {
			this.isEnabled = false;
		}
		if (this.id == null) {
			this.id = 0L;
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public String getUsername() {
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

}
