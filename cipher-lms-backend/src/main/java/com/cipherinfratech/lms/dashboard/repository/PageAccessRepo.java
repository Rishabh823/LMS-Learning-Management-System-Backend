package com.cipherinfratech.lms.dashboard.repository;

import com.cipherinfratech.lms.dashboard.entities.Page;
import com.cipherinfratech.lms.dashboard.entities.PageAccess;
import com.cipherinfratech.lms.users.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PageAccessRepo extends JpaRepository<PageAccess, Long> {

    boolean existsByPageAndRole(Page page, Roles role);

    @Query("SELECT pa.role FROM PageAccess pa WHERE pa.page.id = :pageId")
    List<Roles> findRolesByPageId(Long pageId);

    List<PageAccess> findByPage(Page page);

    @Transactional
    @Modifying
    @Query("DELETE FROM PageAccess pa WHERE pa.page = :page")
    void deleteByPage(Page page);

    @Transactional
    @Modifying
    @Query("DELETE FROM PageAccess pa WHERE pa.page.id = :pageId")
    void deleteByPageId(Long pageId);

}