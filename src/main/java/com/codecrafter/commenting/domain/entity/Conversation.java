package com.codecrafter.commenting.domain.entity;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import com.codecrafter.commenting.domain.entity.base.BaseEntity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * 단일 대화 엔티티
 */
@NoArgsConstructor(access = PROTECTED)
@Entity
@Getter
@SQLDelete(sql = "UPDATE conversation SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@DynamicInsert
public class Conversation extends BaseEntity {

	@Comment("PK")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("대화 master Id")
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "mst_id", nullable = false)
	@Setter
	private ConversationMST conversationMST;

	@Comment("대화내용")
	@Column(nullable = false, columnDefinition = "TEXT")
	@Setter
	private String content;

	@Comment("좋아요 여부")
	@ColumnDefault("false")
	@Column(name = "is_good")
	@Setter
	private boolean isGood = false;

	@Comment("고마워요 여부")
	@ColumnDefault("false")
	@Column(name = "is_thanked")
	@Setter
	private boolean isThanked = false;

	@Comment("비공개 여부")
	@ColumnDefault("false")
	@Column(name = "is_private")
	@Setter
	private boolean isPrivate = false;

	@Comment("질문 여부(대화의 주인이 질문자인지)")
	@ColumnDefault("false")
	@Column(name = "is_question")
	@Setter
	private boolean isQuestion = false;

	@Comment("작성자 정보(익명일수도)")
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "writer_info_id")
	private MemberInfo memberInfo;

	@OneToMany(mappedBy = "conversation", cascade = CascadeType.REMOVE)
	private List<Recommend> recommends = new ArrayList<>();

	@Builder
	public Conversation(String content, boolean isPrivate, boolean isQuestion, MemberInfo memberInfo) {
		this.content = content;
		this.isPrivate = isPrivate;
		this.isQuestion = isQuestion;
		this.memberInfo = memberInfo;
	}

	/**
	 * Builder를 제외하고 유일한 생성자 메서드
	 *
	 * @param content
	 * @param isPrivate
	 * @param isQuestion
	 * @param memberInfo
	 * @return new Conversation
	 */
	private static Conversation create(@NonNull String content, boolean isPrivate, boolean isQuestion,
		@Nullable MemberInfo memberInfo) {
		return
			Conversation.builder()
				.content(content)
				.isPrivate(isPrivate)
				.isQuestion(isQuestion)
				.memberInfo(memberInfo)
				.build();
	}

}
