package com.cipherinfratech.lms.organizations.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OrganizationDetailsResponse {

    private long organizationId;

    // Organization Information
    private String organizationName;
    private String legalBusinessName;
    private String organizationType;
    private String industry;
    private String companySize;
    private String registrationNumber;
    private String gstNumber;
    private String panNumber;
    private String website;
    private String description;
    private boolean hasLogo;
    private boolean status;

    private String emailId;
    private String emailIdAlternate;
    private String contact;
    private String contactAlternate;

    // Owner / Primary Administrator
    private String adminName;
    private String adminEmail;
    private String adminPhone;
    private String designation;
    private String department;

    // Address
    private String addressLine1;
    private String addressLine2;
    private String country;
    private String state;
    private String city;
    private String pincode;

    // Documents (metadata only, never blobs)
    private boolean hasCertificateOfIncorporation;
    private boolean hasGstCertificate;
    private boolean hasPanCard;
    private boolean hasMsmeCertificate;
    private boolean hasIsoCertificate;

    // Branding
    private String primaryColor;
    private String secondaryColor;
    private boolean hasBanner;
    private boolean hasFavicon;

    // Workspace
    private String timezone;
    private String language;
    private String currency;
    private String defaultTheme;

    // Settings (workspace/notifications/registration - not plan-governed)
    private boolean emailNotification;
    private boolean smsNotification;
    private boolean whatsappNotification;
    private boolean allowSelfRegistration;
    private boolean allowTrainerRegistration;
    private boolean allowExternalExaminee;
    private Integer maximumConcurrentSessions;

    // Subscription (plan, limits and feature flags all come from the assigned SubscriptionPlan)
    private Long planId;
    private String planCode;
    private String planName;
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

    private String subscriptionStatus;
    private Date subscriptionStartDate;
    private Date subscriptionEndDate;
    private boolean autoRenew;
    private String paymentStatus;
    private String pendingPlanCode;
    private Date pendingPlanEffectiveDate;

    // Approval
    private String approvalStatus;
    private String remarks;
    private String approvedBy;
    private Date approvedDate;
    private String rejectedBy;
    private Date rejectedDate;

    private Date createdDate;

}
