package com.cipherinfratech.lms.dashboard.repository;

import com.cipherinfratech.lms.dashboard.dto.SidebarProjection;
import com.cipherinfratech.lms.dashboard.entities.Page;
import com.cipherinfratech.lms.users.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PageRepo extends JpaRepository<Page, Long> {

    boolean existsByUrl(String url);

    Optional<Page> findByUrl(String url);

    @Query("SELECT COALESCE(MAX(p.position), 0) FROM Page p WHERE p.section.id = :sectionId")
    Integer getMaxPositionBySectionId(Long sectionId);

    @Query("""
        SELECT p FROM Page p
        JOIN PageAccess pa ON pa.page.id = p.id
        WHERE pa.role = :role
        AND p.section.status = true
        ORDER BY p.section.position ASC, p.position ASC
    """)
    List<Page> findAllByRoleWithSection(Roles role);

    @Query("""
        SELECT 
            s.id AS moduleId,
            s.name AS moduleName,
            s.position AS modulePosition,
            p.id AS pageId,
            p.name AS pageName,
            p.url AS pageUrl,
            p.position AS pagePosition
        FROM Page p
        JOIN p.section s
        JOIN PageAccess pa ON pa.page.id = p.id
        WHERE pa.role = :role
        AND s.status = true
        ORDER BY s.position ASC, p.position ASC
    """)
    List<SidebarProjection> findSidebarByRole(Roles role);

    @Query("""
    SELECT 
        s.id AS moduleId,
        s.name AS moduleName,
        s.position AS modulePosition,
        p.id AS pageId,
        p.name AS pageName,
        p.url AS pageUrl,
        p.position AS pagePosition
    FROM Page p
    JOIN p.section s
    WHERE s.status = true
    ORDER BY s.position ASC, p.position ASC
""")
    List<SidebarProjection> findAllForAdmin();

    List<Page> findBySectionIdOrderByPosition(Long sectionId);

    List<Page> findAllBySectionId(Long sectionId);

    Optional<Page> findById(Long id);

    boolean existsByUrlAndIdNot(String url, Long id);

}
