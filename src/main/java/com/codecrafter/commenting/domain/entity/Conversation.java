package com.codecrafter.commenting.domain.entity;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import com.codecrafter.commenting.domain.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 단일 대화 엔티티
 */
@NoArgsConstructor(access = PROTECTED)
@Entity
@Getter
public class Conversation extends BaseEntity {

	@Comment("PK")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("대화 master Id")
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "mst_id", nullable = false)
	private ConversationMST conversationMST;

	@Comment("대화내용")
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Comment("좋아요 여부")
	@ColumnDefault("false")
	@Column(name = "is_good")
	private Boolean isGood = false;

	@Comment("비공개 여부")
	@ColumnDefault("false")
	@Column(name = "is_private")
	private Boolean isPrivate = false;

	@Comment("질문 여부(대화의 주인이 질문자인지)")
	@ColumnDefault("false")
	@Column(name = "is_question")
	private Boolean isQuestion = false;

	@Comment("작성자 정보(익명일수도)")
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "writer_info_id")
	private MemberInfo memberInfo;

}
