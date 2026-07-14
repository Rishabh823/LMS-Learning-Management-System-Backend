package com.cipherinfratech.lms.subscription.controllers;

import com.cipherinfratech.lms.subscription.dto.AdminAssignPlanRequest;
import com.cipherinfratech.lms.subscription.dto.SubscriptionPlanRequest;
import com.cipherinfratech.lms.subscription.services.OrganizationSubscriptionService;
import com.cipherinfratech.lms.subscription.services.SubscriptionPlanService;
import com.cipherinfratech.lms.utils.ResponseModels;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/subscription-plans")
@AllArgsConstructor
public class SubscriptionPlanController {

    private SubscriptionPlanService subscriptionPlanService;
    private OrganizationSubscriptionService organizationSubscriptionService;

    @GetMapping("/public")
    public ResponseEntity<Object> getPublicPlans() {

        return ResponseModels.successWithPayload(
                "Plans fetched successfully",
                subscriptionPlanService.getPublicPlans()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Object> getAllPlans() {

        return ResponseModels.successWithPayload(
                "Plans fetched successfully",
                subscriptionPlanService.getAllPlans()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Object> createPlan(@Valid @RequestBody SubscriptionPlanRequest request) {

        return ResponseModels.createWithPayload(
                "Plan created successfully",
                subscriptionPlanService.createPlan(request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{planId}")
    public ResponseEntity<Object> updatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody SubscriptionPlanRequest request) {

        return ResponseModels.successWithPayload(
                "Plan updated successfully",
                subscriptionPlanService.updatePlan(planId, request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{planId}")
    public ResponseEntity<Object> deletePlan(@PathVariable Long planId) {

        subscriptionPlanService.deletePlan(planId);

        return ResponseModels.deleted("Plan deleted successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{planId}/toggle-status")
    public ResponseEntity<Object> togglePlanStatus(
            @PathVariable Long planId,
            @RequestParam boolean activate) {

        return ResponseModels.successWithPayload(
                activate ? "Plan enabled successfully" : "Plan disabled successfully",
                subscriptionPlanService.togglePlanStatus(planId, activate)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign")
    public ResponseEntity<Object> assignCustomPlan(@Valid @RequestBody AdminAssignPlanRequest request) {

        return ResponseModels.successWithPayload(
                "Plan assigned successfully",
                organizationSubscriptionService.assignPlan(request)
        );
    }

    /**
     * Manually triggers the same maintenance the daily scheduled job runs
     * (applying due downgrades, expiring lapsed subscriptions to FREE) -
     * useful for ops to run on demand instead of waiting for the next cron tick.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/run-maintenance")
    public ResponseEntity<Object> runMaintenance() {

        organizationSubscriptionService.applyPendingDowngrades();
        organizationSubscriptionService.expireLapsedSubscriptions();

        return ResponseModels.success("Subscription maintenance run completed");
    }

}
