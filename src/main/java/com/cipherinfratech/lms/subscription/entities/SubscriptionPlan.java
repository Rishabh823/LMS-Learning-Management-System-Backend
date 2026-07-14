package com.cipherinfratech.lms.subscription.entities;

import com.cipherinfratech.lms.subscription.enums.BillingCycle;
import com.cipherinfratech.lms.utils.Tracker;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "subscription_plan")
public class SubscriptionPlan extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @NotBlank(message = "Plan name should not be empty")
    private String planName;

    @NotBlank(message = "Plan code should not be empty")
    @Column(unique = true)
    private String planCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Price should not be empty")
    private BigDecimal price;

    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle = BillingCycle.MONTHLY;

    // Limits - null means unlimited
    private Integer maxStudents;
    private Integer maxTrainers;
    private Integer maxCourses;
    private Integer maxGroups;
    private Integer maxAdmins;

    @Column(name = "storage_gb")
    private Integer storageGB;

    // Features
    private boolean attendanceEnabled = false;
    private boolean assignmentEnabled = false;
    private boolean certificateEnabled = false;
    private boolean liveClassEnabled = false;
    private boolean discussionForumEnabled = false;
    private boolean aiEnabled = false;
    private boolean brandingEnabled = false;
    private boolean whiteLabelEnabled = false;
    private boolean customDomainEnabled = false;

    private boolean status = true;

}
