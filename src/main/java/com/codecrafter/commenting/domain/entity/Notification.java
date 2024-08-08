package com.codecrafter.commenting.domain.entity;

import com.codecrafter.commenting.domain.entity.base.BaseEntity;
import com.codecrafter.commenting.domain.enumeration.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;

/**
 * 알림 엔티티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receive_member_id")
    @Comment("알림받는 회원PK")
    private MemberInfo receiverInfo;

    @Comment("알림 종류")
    @Enumerated(value = EnumType.STRING)
    @NotNull
    private NotificationType notificationType;

    @Comment("알림 내용")
    @NotNull
    private String message;

    @Comment("알림 원인 url")
    private String url;

    @Comment("알림 확인 여부")
    @ColumnDefault(value = "false")
    private Boolean isRead;

    @Builder
    public Notification(MemberInfo receiverInfo, NotificationType notificationType, String message, Boolean isRead, String url) {
        this.receiverInfo = receiverInfo;
        this.notificationType = notificationType;
        this.message = message;
        this.isRead = isRead;
        this.url = url;
    }
}
