package com.codecrafter.commenting.repository;

import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.entity.Notification;
import com.codecrafter.commenting.domain.response.Notification.NotificationResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(
        value = "SELECT new com.codecrafter.commenting.domain.response.Notification.NotificationResponse(" +
                "n.id, " +
                "mi.nickname, " +
                "n.message, " +
                "c.content, " +
                "n.notificationType, " +
                "n.createdAt, " +
                "n.url, " +
                "mi.avatarPath, " +
                "n.isRead, " +
                "c.conversationMST.id" +
                ") " +
                "FROM Notification n " +
                "LEFT JOIN Conversation c ON n.notificationTypeId = c.id " +
                "LEFT JOIN MemberInfo mi ON c.memberInfo.id = mi.id " +
                "WHERE n.receiverInfo.id = :id " +
                "ORDER BY n.id DESC"
    )
    List<NotificationResponse> findByReceiverId(@Param("id") Long id);

    @Modifying
    @Query(
        value = "UPDATE Notification n " +
                "SET n.isRead = true " +
                "WHERE n.receiverInfo.id = :id " +
                "AND n.isRead = false"
    )
    void markAllNotificationsAsRead(@Param("id") Long id);

    long countByIsReadFalseAndReceiverInfo(MemberInfo info);

    long countByIsReadFalseAndReceiverInfoEmail(String email);

}
