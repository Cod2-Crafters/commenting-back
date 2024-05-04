package com.codecrafter.commenting.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
public class MemberInfo {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private MemberAuth memberAuth;

    //private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("프로필 자기소개")
    private String introduce;

    @Column(name = "link1")
    @Comment("인스타그램 링크")
    private String link1;

    @Column(name = "link2")
    private String link2;

    @Column(name = "link3")
    private String link3;

    @Column(name = "avatar_path", nullable = false)
    private String avatarPath;

    //@ColumnDefault("Y")
    @ColumnDefault("true")
    @Column(name = "allow_anonymous", nullable = false)
    private Boolean allowAnonymous = true;

    //@ColumnDefault("Y")
    @ColumnDefault("true")
    @Column(name = "email_notice", nullable = false)
    private Boolean emailNotice = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

}
