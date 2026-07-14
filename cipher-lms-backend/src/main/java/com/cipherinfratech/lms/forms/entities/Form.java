package com.cipherinfratech.lms.forms.entities;

import com.cipherinfratech.lms.forms.enums.FormStatus;
import com.cipherinfratech.lms.organizations.entities.Organizations;
import com.cipherinfratech.lms.utils.Tracker;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "forms",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "organization_id")
        }
)
public class Form extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @JsonBackReference
    private Organizations organization;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormStatus status = FormStatus.DRAFT;

    @JsonManagedReference
    @OneToMany(
            mappedBy = "form",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("displayOrder ASC")
    private List<FormSection> sections;

}