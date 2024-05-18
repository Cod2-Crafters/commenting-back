package com.codecrafter.commenting.domain.entity;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import com.codecrafter.commenting.domain.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 대화 관리 테이블
 * {@link Conversation}를 묶음
 */
@Table(name = "conversation_mst")
@NoArgsConstructor(access = PROTECTED)
@Entity
@Getter
public class ConversationMST extends BaseEntity {

	@Comment("PK")
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Comment("대화의 주인 (생성한사람)")
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private MemberInfo owner;

	@Comment("대화의 상대방")
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "guest_id", nullable = true)
	private MemberInfo guest;

	@Comment("질문자/답변자 구분")
	@ColumnDefault("false")
	@Column(name = "is_question", nullable = false)
	private Boolean isQuestion = false;

}
