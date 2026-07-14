package com.cipherinfratech.lms.subscription.services;

import com.cipherinfratech.lms.handlers.NotFoundException;
import com.cipherinfratech.lms.handlers.ValidationException;
import com.cipherinfratech.lms.organizations.entities.Organizations;
import com.cipherinfratech.lms.organizations.repositories.OrganizationsRepo;
import com.cipherinfratech.lms.subscription.dto.*;
import com.cipherinfratech.lms.subscription.entities.OrganizationSubscription;
import com.cipherinfratech.lms.subscription.entities.PaymentTransaction;
import com.cipherinfratech.lms.subscription.entities.SubscriptionPlan;
import com.cipherinfratech.lms.subscription.enums.BillingCycle;
import com.cipherinfratech.lms.subscription.enums.PaymentStatus;
import com.cipherinfratech.lms.subscription.enums.SubscriptionStatus;
import com.cipherinfratech.lms.subscription.repositories.OrganizationSubscriptionRepo;
import com.cipherinfratech.lms.subscription.repositories.PaymentTransactionRepo;
import com.cipherinfratech.lms.subscription.repositories.SubscriptionPlanRepo;
import com.cipherinfratech.lms.utils.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrganizationSubscriptionServiceImpl implements OrganizationSubscriptionService {

    private OrganizationsRepo organizationsRepo;
    private OrganizationSubscriptionRepo organizationSubscriptionRepo;
    private SubscriptionPlanRepo subscriptionPlanRepo;
    private PaymentTransactionRepo paymentTransactionRepo;
    private RazorpayService razorpayService;

    @Override
    public OrganizationSubscriptionResponse getCurrentSubscription(long organizationId) {
        SecurityUtil.assertCanManageOrganization(organizationId);
        return mapToResponse(getSubscriptionOrThrow(organizationId));
    }

    @Override
    @Transactional
    public CreateOrderResponse createUpgradeOrder(long organizationId, CreateOrderRequest request) {

        SecurityUtil.assertCanManageOrganization(organizationId);

        Organizations org = organizationsRepo.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        SubscriptionPlan newPlan = subscriptionPlanRepo.findById(request.getPlanId())
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        if (!newPlan.isStatus()) {
            throw new ValidationException("This plan is not currently available");
        }

        if (newPlan.getPrice() == null || newPlan.getPrice().signum() <= 0) {
            throw new ValidationException("Use the downgrade endpoint for free plans");
        }

        RazorpayService.RazorpayOrder order = razorpayService.createOrder(
                newPlan.getPrice(), newPlan.getCurrency(), "upg_" + organizationId + "_" + UUID.randomUUID()
        );

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrganization(org);
        transaction.setSubscriptionPlan(newPlan);
        transaction.setProviderOrderId(order.orderId());
        transaction.setAmount(newPlan.getPrice());
        transaction.setCurrency(newPlan.getCurrency());
        transaction.setPaymentStatus(PaymentStatus.PENDING);
        paymentTransactionRepo.save(transaction);

        return new CreateOrderResponse(order.orderId(), order.amount(), order.currency(), newPlan.getPlanId());
    }

    @Override
    @Transactional
    public OrganizationSubscriptionResponse verifyUpgrade(long organizationId, UpgradeVerifyRequest request) {

        SecurityUtil.assertCanManageOrganization(organizationId);

        PaymentTransaction transaction = paymentTransactionRepo.findByProviderOrderId(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (transaction.getOrganization() == null
                || transaction.getOrganization().getOrganizationId() != organizationId) {
            throw new ValidationException("Order does not belong to this organization");
        }

        if (transaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new ValidationException("This payment has already been verified");
        }

        boolean valid = razorpayService.verifySignature(
                request.getOrderId(), request.getPaymentId(), request.getSignature()
        );

        if (!valid) {
            transaction.setPaymentStatus(PaymentStatus.FAILED);
            transaction.setProviderPaymentId(request.getPaymentId());
            transaction.setProviderSignature(request.getSignature());
            paymentTransactionRepo.save(transaction);
            throw new ValidationException("Payment verification failed");
        }

        transaction.setProviderPaymentId(request.getPaymentId());
        transaction.setProviderSignature(request.getSignature());
        transaction.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentTransactionRepo.save(transaction);

        OrganizationSubscription subscription = getSubscriptionOrThrow(organizationId);
        applyImmediatePlanChange(subscription, transaction.getSubscriptionPlan());

        return mapToResponse(subscription);
    }

    @Override
    @Transactional
    public OrganizationSubscriptionResponse requestDowngrade(long organizationId, DowngradeRequest request) {

        SecurityUtil.assertCanManageOrganization(organizationId);

        SubscriptionPlan newPlan = subscriptionPlanRepo.findById(request.getPlanId())
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        OrganizationSubscription subscription = getSubscriptionOrThrow(organizationId);

        SubscriptionPlan currentPlan = subscription.getSubscriptionPlan();

        if (currentPlan != null && currentPlan.getPrice() != null && newPlan.getPrice() != null
                && newPlan.getPrice().compareTo(currentPlan.getPrice()) > 0) {
            throw new ValidationException("This is an upgrade, not a downgrade - use the upgrade endpoint instead");
        }

        subscription.setPendingPlan(newPlan);
        subscription.setPendingPlanEffectiveDate(subscription.getSubscriptionEndDate());
        organizationSubscriptionRepo.save(subscription);

        return mapToResponse(subscription);
    }

    @Override
    @Transactional
    public OrganizationSubscriptionResponse cancelSubscription(long organizationId) {

        SecurityUtil.assertCanManageOrganization(organizationId);

        OrganizationSubscription subscription = getSubscriptionOrThrow(organizationId);
        subscription.setAutoRenew(false);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        organizationSubscriptionRepo.save(subscription);

        return mapToResponse(subscription);
    }

    @Override
    @Transactional
    public CreateOrderResponse createRenewOrder(long organizationId) {

        SecurityUtil.assertCanManageOrganization(organizationId);

        OrganizationSubscription subscription = getSubscriptionOrThrow(organizationId);
        SubscriptionPlan plan = subscription.getSubscriptionPlan();

        if (plan.getPrice() == null || plan.getPrice().signum() <= 0) {
            throw new ValidationException("Free plan does not require renewal payment");
        }

        Organizations org = organizationsRepo.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        RazorpayService.RazorpayOrder order = razorpayService.createOrder(
                plan.getPrice(), plan.getCurrency(), "renew_" + organizationId + "_" + UUID.randomUUID()
        );

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrganization(org);
        transaction.setSubscriptionPlan(plan);
        transaction.setProviderOrderId(order.orderId());
        transaction.setAmount(plan.getPrice());
        transaction.setCurrency(plan.getCurrency());
        transaction.setPaymentStatus(PaymentStatus.PENDING);
        paymentTransactionRepo.save(transaction);

        return new CreateOrderResponse(order.orderId(), order.amount(), order.currency(), plan.getPlanId());
    }

    @Override
    @Transactional
    public OrganizationSubscriptionResponse verifyRenewal(long organizationId, UpgradeVerifyRequest request) {
        // A renewal is just "buy the same plan again for another billing cycle" -
        // same verification + plan-application mechanics as an upgrade.
        return verifyUpgrade(organizationId, request);
    }

    @Override
    @Transactional
    public OrganizationSubscriptionResponse assignPlan(AdminAssignPlanRequest request) {

        if (!SecurityUtil.isAdmin()) {
            throw new ValidationException("Only an ADMIN can assign a custom plan");
        }

        SubscriptionPlan plan = subscriptionPlanRepo.findById(request.getPlanId())
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        OrganizationSubscription subscription = getSubscriptionOrThrow(request.getOrganizationId());
        applyImmediatePlanChange(subscription, plan);

        return mapToResponse(subscription);
    }

    @Override
    @Transactional
    public void applyPendingDowngrades() {

        List<OrganizationSubscription> due = organizationSubscriptionRepo
                .findByPendingPlanIsNotNullAndPendingPlanEffectiveDateBefore(new Date());

        for (OrganizationSubscription subscription : due) {
            SubscriptionPlan pending = subscription.getPendingPlan();
            subscription.setPendingPlan(null);
            subscription.setPendingPlanEffectiveDate(null);
            applyImmediatePlanChange(subscription, pending);
        }
    }

    @Override
    @Transactional
    public void expireLapsedSubscriptions() {

        SubscriptionPlan freePlan = subscriptionPlanRepo.findByPlanCodeIgnoreCase("FREE").orElse(null);

        List<OrganizationSubscription> lapsedActive = organizationSubscriptionRepo
                .findByStatusAndSubscriptionEndDateBefore(SubscriptionStatus.ACTIVE, new Date());

        for (OrganizationSubscription subscription : lapsedActive) {

            boolean alreadyFree = freePlan != null
                    && subscription.getSubscriptionPlan().getPlanId().equals(freePlan.getPlanId());

            if (freePlan == null || alreadyFree) {
                // Already on the free plan (or no free plan configured) - just roll the cycle forward.
                extendCycle(subscription, subscription.getSubscriptionPlan());
                continue;
            }

            applyImmediatePlanChange(subscription, freePlan);
        }

        if (freePlan != null) {

            List<OrganizationSubscription> lapsedCancelled = organizationSubscriptionRepo
                    .findByStatusAndSubscriptionEndDateBefore(SubscriptionStatus.CANCELLED, new Date());

            for (OrganizationSubscription subscription : lapsedCancelled) {
                applyImmediatePlanChange(subscription, freePlan);
            }
        }
    }

    private void applyImmediatePlanChange(OrganizationSubscription subscription, SubscriptionPlan newPlan) {

        subscription.setSubscriptionPlan(newPlan);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setPaymentStatus(PaymentStatus.SUCCESS);
        subscription.setAutoRenew(true);
        extendCycle(subscription, newPlan);
    }

    private void extendCycle(OrganizationSubscription subscription, SubscriptionPlan plan) {

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(plan.getBillingCycle() == BillingCycle.YEARLY ? Calendar.YEAR : Calendar.MONTH, 1);

        subscription.setSubscriptionStartDate(now);
        subscription.setSubscriptionEndDate(calendar.getTime());
        organizationSubscriptionRepo.save(subscription);
    }

    private OrganizationSubscription getSubscriptionOrThrow(long organizationId) {
        return organizationSubscriptionRepo.findByOrganization_OrganizationId(organizationId)
                .orElseThrow(() -> new NotFoundException("Subscription not found for this organization"));
    }

    private OrganizationSubscriptionResponse mapToResponse(OrganizationSubscription subscription) {

        OrganizationSubscriptionResponse response = new OrganizationSubscriptionResponse();

        response.setSubscriptionId(subscription.getSubscriptionId());
        response.setOrganizationId(subscription.getOrganization().getOrganizationId());

        SubscriptionPlan plan = subscription.getSubscriptionPlan();
        if (plan != null) {
            response.setPlanId(plan.getPlanId());
            response.setPlanName(plan.getPlanName());
            response.setPlanCode(plan.getPlanCode());
            response.setPrice(plan.getPrice());
            response.setCurrency(plan.getCurrency());
            response.setBillingCycle(plan.getBillingCycle() != null ? plan.getBillingCycle().name() : null);
            response.setMaxStudents(plan.getMaxStudents());
            response.setMaxTrainers(plan.getMaxTrainers());
            response.setMaxCourses(plan.getMaxCourses());
            response.setMaxGroups(plan.getMaxGroups());
            response.setMaxAdmins(plan.getMaxAdmins());
            response.setStorageGB(plan.getStorageGB());
            response.setAttendanceEnabled(plan.isAttendanceEnabled());
            response.setAssignmentEnabled(plan.isAssignmentEnabled());
            response.setCertificateEnabled(plan.isCertificateEnabled());
            response.setLiveClassEnabled(plan.isLiveClassEnabled());
            response.setDiscussionForumEnabled(plan.isDiscussionForumEnabled());
            response.setAiEnabled(plan.isAiEnabled());
            response.setBrandingEnabled(plan.isBrandingEnabled());
            response.setWhiteLabelEnabled(plan.isWhiteLabelEnabled());
            response.setCustomDomainEnabled(plan.isCustomDomainEnabled());
        }

        response.setStatus(subscription.getStatus() != null ? subscription.getStatus().name() : null);
        response.setSubscriptionStartDate(subscription.getSubscriptionStartDate());
        response.setSubscriptionEndDate(subscription.getSubscriptionEndDate());
        response.setAutoRenew(subscription.isAutoRenew());
        response.setPaymentStatus(subscription.getPaymentStatus() != null ? subscription.getPaymentStatus().name() : null);

        if (subscription.getPendingPlan() != null) {
            response.setPendingPlanCode(subscription.getPendingPlan().getPlanCode());
            response.setPendingPlanName(subscription.getPendingPlan().getPlanName());
            response.setPendingPlanEffectiveDate(subscription.getPendingPlanEffectiveDate());
        }

        return response;
    }
}
