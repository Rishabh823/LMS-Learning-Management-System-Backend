package com.cipherinfratech.lms.notification.repo;

import com.cipherinfratech.lms.notification.dto.NotificationProjection;
import com.cipherinfratech.lms.notification.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<NotificationProjection> findByEmailOrderByCreatedDateDesc(
            String email,
            Pageable pageable
    );

    long countByEmailAndIsReadFalse(String email);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.isRead = true
            WHERE n.email = :email
            AND n.isRead = false
            """)
    int markAllAsReadByEmail(@Param("email") String email);

}
