package com.cipherinfratech.lms.dashboard.entities;

import com.cipherinfratech.lms.users.entity.Roles;
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
                    "role", "page_id"
                }
        )
})
public class PageAccess extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Roles role;

    @ManyToOne
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;
}
