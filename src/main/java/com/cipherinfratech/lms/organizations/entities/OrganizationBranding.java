package com.cipherinfratech.lms.organizations.entities;

import com.cipherinfratech.lms.utils.Tracker;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "organization_branding")
public class OrganizationBranding extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "organization_id", nullable = false)
    @JsonBackReference
    private Organizations organization;

    private String primaryColor;

    private String secondaryColor;

    @Lob
    @JsonIgnore
    @Column(columnDefinition = "LONGBLOB")
    private byte[] banner;
    private String bannerFileName;
    private String bannerFileType;

    @Lob
    @JsonIgnore
    @Column(columnDefinition = "LONGBLOB")
    private byte[] favicon;
    private String faviconFileName;
    private String faviconFileType;

}
