package com.cipherinfratech.lms.subscription.services;

import com.cipherinfratech.lms.subscription.dto.SubscriptionPlanRequest;
import com.cipherinfratech.lms.subscription.dto.SubscriptionPlanResponse;

import java.util.List;

public interface SubscriptionPlanService {

    SubscriptionPlanResponse createPlan(SubscriptionPlanRequest request);

    SubscriptionPlanResponse updatePlan(Long planId, SubscriptionPlanRequest request);

    void deletePlan(Long planId);

    SubscriptionPlanResponse togglePlanStatus(Long planId, boolean activate);

    List<SubscriptionPlanResponse> getAllPlans();

    List<SubscriptionPlanResponse> getPublicPlans();

}
