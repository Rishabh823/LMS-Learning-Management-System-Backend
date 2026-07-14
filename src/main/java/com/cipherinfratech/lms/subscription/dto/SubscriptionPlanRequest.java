package com.cipherinfratech.lms.subscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionPlanRequest {

    @NotBlank(message = "Plan name should not be empty")
    private String planName;

    @NotBlank(message = "Plan code should not be empty")
    private String planCode;

    private String description;

    @NotNull(message = "Price should not be empty")
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

}
