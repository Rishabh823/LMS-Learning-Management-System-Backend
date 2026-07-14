package com.cipherinfratech.lms.phishing.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class PhishingEmailSend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long orgId;

    private UUID userId;
    private String template;

    private String emailId;

    @JsonInclude()
    @Transient
    private String subject;

    @JsonInclude()
    @Transient
    private String body;

    private boolean isEmailOpen;
    private String country_code;
    private String country_name;
    private String city;
    private String postal;
    private String latitude;
    private String longitude;
    private String IPv4;
    private String state;

    private String credentialsUserName;

    private String credentialsPassword;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy  hh:mm:ss a")
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @UpdateTimestamp
    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy  hh:mm:ss a")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;



}
