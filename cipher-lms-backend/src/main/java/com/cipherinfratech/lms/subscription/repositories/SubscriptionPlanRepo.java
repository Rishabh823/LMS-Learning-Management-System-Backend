package com.cipherinfratech.lms.subscription.repositories;

import com.cipherinfratech.lms.subscription.entities.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepo extends JpaRepository<SubscriptionPlan, Long> {

    Optional<SubscriptionPlan> findByPlanCodeIgnoreCase(String planCode);

    boolean existsByPlanCodeIgnoreCase(String planCode);

    List<SubscriptionPlan> findByStatusTrue();

    List<SubscriptionPlan> findAllByOrderByPriceAsc();
}
