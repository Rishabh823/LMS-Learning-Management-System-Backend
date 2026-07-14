package com.cipherinfratech.lms.dashboard.entities;

import com.cipherinfratech.lms.utils.Tracker;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {
                        "section_id", "id"
                }
        )
})
public class Page extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false)
    private boolean status = true;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

}
