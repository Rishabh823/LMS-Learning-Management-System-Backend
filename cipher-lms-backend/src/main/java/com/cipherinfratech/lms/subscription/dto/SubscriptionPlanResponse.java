package com.cipherinfratech.lms.subscription.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionPlanResponse {

    private Long planId;
    private String planName;
    private String planCode;
    private String description;
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

    private boolean status;

}
