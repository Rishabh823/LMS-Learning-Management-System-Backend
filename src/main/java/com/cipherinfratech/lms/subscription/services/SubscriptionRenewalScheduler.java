package com.cipherinfratech.lms.subscription.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SubscriptionRenewalScheduler {

    private OrganizationSubscriptionService organizationSubscriptionService;

    /**
     * Runs once a day: applies any downgrade whose billing cycle has ended, and
     * gracefully drops any lapsed (non-renewing/cancelled and past due) paid
     * subscription back to the FREE plan instead of leaving the org locked out.
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void run() {

        try {
            organizationSubscriptionService.applyPendingDowngrades();
        } catch (Exception e) {
            log.error("Failed to apply pending subscription downgrades", e);
        }

        try {
            organizationSubscriptionService.expireLapsedSubscriptions();
        } catch (Exception e) {
            log.error("Failed to expire lapsed subscriptions", e);
        }
    }
}
