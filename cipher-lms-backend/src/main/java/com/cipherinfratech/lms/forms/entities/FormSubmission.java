package com.cipherinfratech.lms.forms.entities;

import com.cipherinfratech.lms.organizations.entities.Organizations;
import com.cipherinfratech.lms.users.entity.Users;
import com.cipherinfratech.lms.utils.Tracker;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "form_submissions")
public class FormSubmission extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    @JsonBackReference
    private Form form;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @JsonBackReference
    private Organizations organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private Users user;

    @Column(nullable = false)
    private boolean submitted = true;

    @JsonManagedReference
    @OneToMany(
            mappedBy = "submission",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<FormSubmissionValue> values = new ArrayList<>();
}