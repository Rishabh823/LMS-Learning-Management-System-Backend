package com.cipherinfratech.lms.subscription.controllers;

import com.cipherinfratech.lms.subscription.dto.CreateOrderRequest;
import com.cipherinfratech.lms.subscription.dto.DowngradeRequest;
import com.cipherinfratech.lms.subscription.dto.UpgradeVerifyRequest;
import com.cipherinfratech.lms.subscription.services.OrganizationSubscriptionService;
import com.cipherinfratech.lms.utils.ResponseModels;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/organizations/{organizationId}/subscription")
@AllArgsConstructor
public class OrganizationSubscriptionController {

    private OrganizationSubscriptionService organizationSubscriptionService;

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @GetMapping
    public ResponseEntity<Object> getCurrentSubscription(@PathVariable long organizationId) {

        return ResponseModels.successWithPayload(
                "Subscription fetched successfully",
                organizationSubscriptionService.getCurrentSubscription(organizationId)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @PostMapping("/upgrade/create-order")
    public ResponseEntity<Object> createUpgradeOrder(
            @PathVariable long organizationId,
            @Valid @RequestBody CreateOrderRequest request) {

        return ResponseModels.successWithPayload(
                "Order created successfully",
                organizationSubscriptionService.createUpgradeOrder(organizationId, request)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @PostMapping("/upgrade/verify")
    public ResponseEntity<Object> verifyUpgrade(
            @PathVariable long organizationId,
            @Valid @RequestBody UpgradeVerifyRequest request) {

        return ResponseModels.successWithPayload(
                "Subscription upgraded successfully",
                organizationSubscriptionService.verifyUpgrade(organizationId, request)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @PutMapping("/downgrade")
    public ResponseEntity<Object> requestDowngrade(
            @PathVariable long organizationId,
            @Valid @RequestBody DowngradeRequest request) {

        return ResponseModels.successWithPayload(
                "Downgrade scheduled - it will take effect at the end of your current billing cycle",
                organizationSubscriptionService.requestDowngrade(organizationId, request)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @PutMapping("/cancel")
    public ResponseEntity<Object> cancelSubscription(@PathVariable long organizationId) {

        return ResponseModels.successWithPayload(
                "Subscription cancelled - you'll keep access until the end of your current billing cycle",
                organizationSubscriptionService.cancelSubscription(organizationId)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @PostMapping("/renew/create-order")
    public ResponseEntity<Object> createRenewOrder(@PathVariable long organizationId) {

        return ResponseModels.successWithPayload(
                "Order created successfully",
                organizationSubscriptionService.createRenewOrder(organizationId)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @PostMapping("/renew/verify")
    public ResponseEntity<Object> verifyRenewal(
            @PathVariable long organizationId,
            @Valid @RequestBody UpgradeVerifyRequest request) {

        return ResponseModels.successWithPayload(
                "Subscription renewed successfully",
                organizationSubscriptionService.verifyRenewal(organizationId, request)
        );
    }

}
