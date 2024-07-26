package com.codecrafter.commenting.repository;

import com.codecrafter.commenting.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
