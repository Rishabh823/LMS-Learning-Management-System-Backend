package com.cipherinfratech.lms.organizations.entities;

import com.cipherinfratech.lms.organizations.enums.ApprovalStatus;
import com.cipherinfratech.lms.utils.Tracker;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "organization_approval")
public class OrganizationApproval extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "organization_id", nullable = false)
    @JsonBackReference
    private Organizations organization;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    private String approvedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date approvedDate;

    private String rejectedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date rejectedDate;

}
