package com.cipherinfratech.lms.forms.entities;

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
@Table(name = "form_sections")
public class FormSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    @JsonBackReference
    private Form form;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer displayOrder;

    @JsonManagedReference
    @OneToMany(
            mappedBy = "section",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("displayOrder ASC")
    private List<FormField> fields = new ArrayList<>();

}