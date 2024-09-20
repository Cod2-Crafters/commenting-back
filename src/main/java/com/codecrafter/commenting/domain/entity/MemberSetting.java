package com.codecrafter.commenting.domain.entity;


import com.codecrafter.commenting.domain.entity.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@SQLDelete(sql = "UPDATE member_setting SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class MemberSetting extends BaseEntity {

    @Id
    @Comment("PK MemberInfo의 PK와 동일")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private MemberInfo memberInfo;

    @Comment("익명 댓글 허용 여부")
    @ColumnDefault("true")
    private Boolean allowAnonymous;

    @Comment("이메일 알림 수신 여부")
    @ColumnDefault("false")
    private Boolean emailNotice;

    @Comment("광역 질문 수신 여부")
    @ColumnDefault("true")
    private Boolean allowGlobalQuestion;

    @Comment("스페이스 일시 중지")
    @ColumnDefault("false")
    private Boolean isSpacePaused;

    public MemberSetting(MemberInfo memberInfo) {
        this.memberInfo = memberInfo;
    }
}
