package com.codecrafter.commenting.domain.entity;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Version;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Comment;

import com.codecrafter.commenting.domain.entity.base.BaseEntity;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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

	@Comment("대화의 주인 (질문받는사람)")
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private MemberInfo owner;

	@Comment("대화의 상대방(생성한사람) null이면 익명")
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "guest_id", nullable = true)
	private MemberInfo guest;

	@OneToMany(mappedBy = "conversationMST", cascade = CascadeType.PERSIST)
	private List<Conversation> conversations = new ArrayList<>();

	@Version
	private Long version;

	/**
	 * 대화어 관개를 맺는 양방향 메서드
	 *
	 * @param conversation 대화
	 */
	public void addConversation(@NonNull Conversation conversation) {
		this.conversations.add(conversation);
		conversation.setConversationMST(this);
	}

	/**
	 * 유일한 생성자 메서드
	 *
	 * @param owner 질문받는사람 MemberInfo
	 * @param guest 질문하는사람(만든사람) MemberInfo Null일경우 익명
	 * @return new ConversationMst
	 */
	public static ConversationMST create(@NonNull MemberInfo owner, @Nullable MemberInfo guest) {
		ConversationMST conversationMST = new ConversationMST();
		conversationMST.owner = owner;
		conversationMST.guest = guest;
		return conversationMST;
	}

}
