package com.cipherinfratech.lms.forms.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "form_field_options")
public class FormFieldOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    @JsonBackReference
    private FormField field;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private Integer displayOrder;

}