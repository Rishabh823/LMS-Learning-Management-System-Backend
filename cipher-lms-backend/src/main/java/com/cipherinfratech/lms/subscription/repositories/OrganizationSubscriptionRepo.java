package com.cipherinfratech.lms.subscription.repositories;

import com.cipherinfratech.lms.subscription.entities.OrganizationSubscription;
import com.cipherinfratech.lms.subscription.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrganizationSubscriptionRepo extends JpaRepository<OrganizationSubscription, Long> {

    Optional<OrganizationSubscription> findByOrganization_OrganizationId(long organizationId);

    List<OrganizationSubscription> findByPendingPlanIsNotNullAndPendingPlanEffectiveDateBefore(Date date);

    List<OrganizationSubscription> findByStatusAndSubscriptionEndDateBefore(SubscriptionStatus status, Date date);

    boolean existsBySubscriptionPlan_PlanId(Long planId);
}
