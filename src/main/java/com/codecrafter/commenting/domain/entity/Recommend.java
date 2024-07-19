package com.codecrafter.commenting.domain.entity;

import com.codecrafter.commenting.domain.entity.base.BaseEntity;
import com.codecrafter.commenting.domain.enumeration.RecommendStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Entity
@Getter
public class Recommend {
    @Comment("PK")
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Comment("추천한 대화")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "conversation_id", nullable = false  )
    private Conversation conversation;

    @Comment("추천을 누른 계정")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MemberInfo memberInfo;

    @Comment("좋아요/고마와요 여부")
    @Enumerated(EnumType.STRING)
    @Column(name = "recommend_status")
    private RecommendStatus recommendStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Recommend(Conversation conversation, MemberInfo memberInfo, RecommendStatus recommendStatus) {
        this.conversation = conversation;
        this.memberInfo = memberInfo;
        this.recommendStatus = recommendStatus;
    }
}
