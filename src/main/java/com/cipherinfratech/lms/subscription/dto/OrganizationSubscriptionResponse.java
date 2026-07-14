package com.cipherinfratech.lms.subscription.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrganizationSubscriptionResponse {

    private Long subscriptionId;
    private long organizationId;

    private Long planId;
    private String planName;
    private String planCode;
    private BigDecimal price;
    private String currency;
    private String billingCycle;

    private Integer maxStudents;
    private Integer maxTrainers;
    private Integer maxCourses;
    private Integer maxGroups;
    private Integer maxAdmins;
    private Integer storageGB;

    private boolean attendanceEnabled;
    private boolean assignmentEnabled;
    private boolean certificateEnabled;
    private boolean liveClassEnabled;
    private boolean discussionForumEnabled;
    private boolean aiEnabled;
    private boolean brandingEnabled;
    private boolean whiteLabelEnabled;
    private boolean customDomainEnabled;

    private String status;
    private Date subscriptionStartDate;
    private Date subscriptionEndDate;
    private boolean autoRenew;
    private String paymentStatus;

    private String pendingPlanCode;
    private String pendingPlanName;
    private Date pendingPlanEffectiveDate;

}
