package com.cipherinfratech.lms.organizations.entities;

import com.cipherinfratech.lms.forms.entities.Form;
import com.cipherinfratech.lms.subscription.entities.OrganizationSubscription;
import com.cipherinfratech.lms.users.entity.Users;
import com.cipherinfratech.lms.utils.Tracker;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "organizations")
public class Organizations extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long organizationId;

    @JsonIgnore
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] logo;
    private String logoFileName;
    private String logoFileType;

    @NotEmpty(message = "Organization name should not be empty")
    @NotNull(message = "Organization name should not be empty")
    @NotBlank(message = "Organization name should not be empty")
    @Size(min = 2, max = 100, message = "Organization name should be contain 2-100 characters only")
    private String fullName;

    @NotEmpty(message = "Legal business name should not be empty")
    @NotNull(message = "Legal business name should not be empty")
    @NotBlank(message = "Legal business name should not be empty")
    private String legalBusinessName;

    @NotEmpty(message = "Organization type should not be empty")
    @NotNull(message = "Organization type should not be empty")
    @NotBlank(message = "Organization type should not be empty")
    private String organizationType;

    @NotEmpty(message = "Industry should not be empty")
    @NotNull(message = "Industry should not be empty")
    @NotBlank(message = "Industry should not be empty")
    private String industry;

    @NotEmpty(message = "Company size should not be empty")
    @NotNull(message = "Company size should not be empty")
    @NotBlank(message = "Company size should not be empty")
    private String companySize;

    @NotEmpty(message = "Registration number should not be empty")
    @NotNull(message = "Registration number should not be empty")
    @NotBlank(message = "Registration number should not be empty")
    private String registrationNumber;

    private String gstNumber;

    private String panNumber;

    private String website;

    @NotEmpty(message = "EmailId should not be empty")
    @NotNull(message = "EmailId should not be empty")
    @NotBlank(message = "EmailId name should not be empty")
    @Size(min = 2, max = 100, message = "EmailId should be contain 2-100 characters only")
    @Email(message = "Invalid email id")
    private String emailId;

    @Size(min = 2, max = 100, message = "EmailId should be contain 2-100 characters only")
    @Email(message = "Invalid email id")
    private String emailIdAlternate;
    @NotEmpty(message = "Contact should not be empty")
    @NotNull(message = "Contact should not be empty")
    @NotBlank(message = "Contact name should not be empty")
    @Size(min = 8, max = 16,  message = "Contact number should be contains 8-16 digits")
    private String contact;
    @Size(min = 8, max = 16,  message = "Contact number should be contains 8-16 digits")
    private String contactAlternate;

    @Column(columnDefinition = "TEXT")
    private String aboutOrganization;

    @OneToMany(mappedBy = "organizations", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Users> users;

//    @JsonManagedReference
//    @OneToMany(mappedBy = "organizations", cascade = CascadeType.ALL)
//    private List<Course> courses;

//    @JsonManagedReference
//    @OneToMany(mappedBy = "organizations", cascade = CascadeType.ALL)
//    private List<LiveTraining> liveTrainings;

    private boolean status =true;

    @JsonManagedReference
    @OneToOne(mappedBy = "organization",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Form form;

    @JsonManagedReference
    @OneToOne(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrganizationAddress address;

    @JsonManagedReference
    @OneToOne(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrganizationDocuments documents;

    @JsonManagedReference
    @OneToOne(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrganizationBranding branding;

    @JsonManagedReference
    @OneToOne(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrganizationSettings settings;

    @JsonManagedReference
    @OneToOne(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrganizationSubscription subscription;

    @JsonManagedReference
    @OneToOne(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrganizationApproval approval;

}
