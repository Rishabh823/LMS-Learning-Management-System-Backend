package com.cipherinfratech.lms.subscription.services;

import com.cipherinfratech.lms.subscription.dto.*;

public interface OrganizationSubscriptionService {

    OrganizationSubscriptionResponse getCurrentSubscription(long organizationId);

    CreateOrderResponse createUpgradeOrder(long organizationId, CreateOrderRequest request);

    OrganizationSubscriptionResponse verifyUpgrade(long organizationId, UpgradeVerifyRequest request);

    OrganizationSubscriptionResponse requestDowngrade(long organizationId, DowngradeRequest request);

    OrganizationSubscriptionResponse cancelSubscription(long organizationId);

    CreateOrderResponse createRenewOrder(long organizationId);

    OrganizationSubscriptionResponse verifyRenewal(long organizationId, UpgradeVerifyRequest request);

    OrganizationSubscriptionResponse assignPlan(AdminAssignPlanRequest request);

    // Scheduled maintenance - also invocable directly (e.g. by an admin tool) for testing
    void applyPendingDowngrades();

    void expireLapsedSubscriptions();

}
