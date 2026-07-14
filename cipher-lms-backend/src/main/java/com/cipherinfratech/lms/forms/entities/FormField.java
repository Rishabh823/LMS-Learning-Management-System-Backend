package com.cipherinfratech.lms.forms.entities;

import com.cipherinfratech.lms.forms.enums.FieldType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "form_fields")
public class FormField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @JsonBackReference
    private FormSection section;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String fieldName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FieldType fieldType;

    private String placeholder;

    private String defaultValue;

    private boolean required;

    private boolean readOnly;

    private boolean hidden;

    private Integer displayOrder;

    private Integer minLength;

    private Integer maxLength;

    private Integer minValue;

    private Integer maxValue;

    private String regexPattern;

    @JsonManagedReference
    @OneToMany(
            mappedBy = "field",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("displayOrder ASC")
    private List<FormFieldOption> options = new ArrayList<>();

    private String fileName;

    private String fileType;

    @Lob
    @JsonIgnore
    @Column(columnDefinition = "LONGBLOB")
    private byte[] fileData;

}