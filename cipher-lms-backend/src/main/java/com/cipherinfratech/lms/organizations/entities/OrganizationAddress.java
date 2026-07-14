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
@Table(name = "organization_address")
public class OrganizationAddress extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "organization_id", nullable = false)
    @JsonBackReference
    private Organizations organization;

    private String addressLine1;

    private String addressLine2;

    private String country;

    private String state;

    private String city;

    private String pincode;

}
