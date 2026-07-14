package com.cipherinfratech.lms.dashboard.repository;

import com.cipherinfratech.lms.dashboard.entities.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SectionRepo extends JpaRepository<Section, Long> {

    boolean existsByName(String name);

    Optional<Section> findByName(String name);

    @Query("SELECT COALESCE(MAX(s.position), 0) FROM Section s")
    Integer getMaxPosition();

    boolean existsByNameAndIdNot(String name,
                                 Long id);

}
