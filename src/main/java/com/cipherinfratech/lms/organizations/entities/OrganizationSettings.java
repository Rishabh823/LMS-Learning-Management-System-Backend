package com.cipherinfratech.lms.organizations.entities;

import com.cipherinfratech.lms.utils.Tracker;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "organization_settings")
public class OrganizationSettings extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "organization_id", nullable = false)
    @JsonBackReference
    private Organizations organization;

    // Workspace
    private String timezone;
    private String language;
    private String currency;
    private String defaultTheme;

    // Notifications
    private boolean emailNotification = true;
    private boolean smsNotification = false;
    private boolean whatsappNotification = false;

    // Registration
    private boolean allowSelfRegistration = false;
    private boolean allowTrainerRegistration = false;
    private boolean allowExternalExaminee = false;

    // Operational (not plan-governed)
    private Integer maximumConcurrentSessions;

    // NOTE: usage limits (maxStudents/maxTrainers/maxCourses/maxGroups/maxAdmins/storageGB)
    // and feature flags (attendanceEnabled/certificateEnabled/liveClassEnabled/aiEnabled/
    // brandingEnabled/customDomainEnabled/discussionForumEnabled) now live on the
    // organization's SubscriptionPlan (see subscription module) instead of here.

}
