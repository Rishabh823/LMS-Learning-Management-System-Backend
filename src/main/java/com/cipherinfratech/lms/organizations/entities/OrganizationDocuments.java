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
@Table(name = "organization_documents")
public class OrganizationDocuments extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "organization_id", nullable = false)
    @JsonBackReference
    private Organizations organization;

    @Lob
    @JsonIgnore
    @Column(columnDefinition = "LONGBLOB")
    private byte[] certificateOfIncorporationFile;
    private String certificateOfIncorporationFileName;
    private String certificateOfIncorporationFileType;

    @Lob
    @JsonIgnore
    @Column(columnDefinition = "LONGBLOB")
    private byte[] gstCertificateFile;
    private String gstCertificateFileName;
    private String gstCertificateFileType;

    @Lob
    @JsonIgnore
    @Column(columnDefinition = "LONGBLOB")
    private byte[] panCardFile;
    private String panCardFileName;
    private String panCardFileType;

    @Lob
    @JsonIgnore
    @Column(columnDefinition = "LONGBLOB")
    private byte[] msmeCertificateFile;
    private String msmeCertificateFileName;
    private String msmeCertificateFileType;

    @Lob
    @JsonIgnore
    @Column(columnDefinition = "LONGBLOB")
    private byte[] isoCertificateFile;
    private String isoCertificateFileName;
    private String isoCertificateFileType;

}
