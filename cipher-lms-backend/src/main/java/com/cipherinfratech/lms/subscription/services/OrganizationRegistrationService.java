package com.cipherinfratech.lms.subscription.services;

import com.cipherinfratech.lms.subscription.dto.AdminInfoDTO;
import com.cipherinfratech.lms.subscription.dto.OrganizationInfoDTO;
import com.cipherinfratech.lms.subscription.dto.OrganizationRegisterRequest;
import com.cipherinfratech.lms.subscription.dto.OrganizationRegistrationResponse;
import com.cipherinfratech.lms.subscription.entities.SubscriptionPlan;

public interface OrganizationRegistrationService {

    OrganizationRegistrationResponse registerFreePlan(OrganizationRegisterRequest request);

    /**
     * Shared org+admin+subscription creation, reused for both free signup and
     * post-payment-verification paid signup. Organization and admin are activated
     * immediately (no approval gate) and an auto-approved OrganizationApproval
     * record is created for consistency with the rest of the codebase.
     */
    OrganizationRegistrationResponse createOrganizationWithPlan(
            OrganizationInfoDTO orgInfo,
            AdminInfoDTO adminInfo,
            SubscriptionPlan plan
    );

}
