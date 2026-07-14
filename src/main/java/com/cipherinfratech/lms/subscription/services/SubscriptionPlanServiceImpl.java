package com.cipherinfratech.lms.subscription.services;

import com.cipherinfratech.lms.handlers.NotFoundException;
import com.cipherinfratech.lms.handlers.ValidationException;
import com.cipherinfratech.lms.subscription.dto.SubscriptionPlanRequest;
import com.cipherinfratech.lms.subscription.dto.SubscriptionPlanResponse;
import com.cipherinfratech.lms.subscription.entities.SubscriptionPlan;
import com.cipherinfratech.lms.subscription.enums.BillingCycle;
import com.cipherinfratech.lms.subscription.repositories.OrganizationSubscriptionRepo;
import com.cipherinfratech.lms.subscription.repositories.SubscriptionPlanRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private SubscriptionPlanRepo subscriptionPlanRepo;
    private OrganizationSubscriptionRepo organizationSubscriptionRepo;

    @Override
    @Transactional
    public SubscriptionPlanResponse createPlan(SubscriptionPlanRequest request) {

        if (subscriptionPlanRepo.existsByPlanCodeIgnoreCase(request.getPlanCode())) {
            throw new ValidationException("A plan already exists with code: " + request.getPlanCode());
        }

        SubscriptionPlan plan = new SubscriptionPlan();
        applyRequest(plan, request);

        return mapToResponse(subscriptionPlanRepo.save(plan));
    }

    @Override
    @Transactional
    public SubscriptionPlanResponse updatePlan(Long planId, SubscriptionPlanRequest request) {

        SubscriptionPlan plan = subscriptionPlanRepo.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        if (!plan.getPlanCode().equalsIgnoreCase(request.getPlanCode())
                && subscriptionPlanRepo.existsByPlanCodeIgnoreCase(request.getPlanCode())) {
            throw new ValidationException("A plan already exists with code: " + request.getPlanCode());
        }

        applyRequest(plan, request);

        return mapToResponse(subscriptionPlanRepo.save(plan));
    }

    @Override
    @Transactional
    public void deletePlan(Long planId) {

        if (!subscriptionPlanRepo.existsById(planId)) {
            throw new NotFoundException("Plan not found");
        }

        if (organizationSubscriptionRepo.existsBySubscriptionPlan_PlanId(planId)) {
            throw new ValidationException(
                    "Cannot delete a plan that organizations are currently subscribed to. Disable it instead."
            );
        }

        subscriptionPlanRepo.deleteById(planId);
    }

    @Override
    @Transactional
    public SubscriptionPlanResponse togglePlanStatus(Long planId, boolean activate) {

        SubscriptionPlan plan = subscriptionPlanRepo.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        plan.setStatus(activate);

        return mapToResponse(subscriptionPlanRepo.save(plan));
    }

    @Override
    public List<SubscriptionPlanResponse> getAllPlans() {
        return subscriptionPlanRepo.findAllByOrderByPriceAsc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionPlanResponse> getPublicPlans() {
        return subscriptionPlanRepo.findByStatusTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void applyRequest(SubscriptionPlan plan, SubscriptionPlanRequest request) {

        plan.setPlanName(request.getPlanName());
        plan.setPlanCode(request.getPlanCode());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());

        if (request.getCurrency() != null && !request.getCurrency().isBlank()) {
            plan.setCurrency(request.getCurrency());
        }

        if (request.getBillingCycle() != null && !request.getBillingCycle().isBlank()) {
            try {
                plan.setBillingCycle(BillingCycle.valueOf(request.getBillingCycle().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid billing cycle: " + request.getBillingCycle());
            }
        }

        plan.setMaxStudents(request.getMaxStudents());
        plan.setMaxTrainers(request.getMaxTrainers());
        plan.setMaxCourses(request.getMaxCourses());
        plan.setMaxGroups(request.getMaxGroups());
        plan.setMaxAdmins(request.getMaxAdmins());
        plan.setStorageGB(request.getStorageGB());

        plan.setAttendanceEnabled(request.isAttendanceEnabled());
        plan.setAssignmentEnabled(request.isAssignmentEnabled());
        plan.setCertificateEnabled(request.isCertificateEnabled());
        plan.setLiveClassEnabled(request.isLiveClassEnabled());
        plan.setDiscussionForumEnabled(request.isDiscussionForumEnabled());
        plan.setAiEnabled(request.isAiEnabled());
        plan.setBrandingEnabled(request.isBrandingEnabled());
        plan.setWhiteLabelEnabled(request.isWhiteLabelEnabled());
        plan.setCustomDomainEnabled(request.isCustomDomainEnabled());
    }

    private SubscriptionPlanResponse mapToResponse(SubscriptionPlan plan) {

        SubscriptionPlanResponse response = new SubscriptionPlanResponse();

        response.setPlanId(plan.getPlanId());
        response.setPlanName(plan.getPlanName());
        response.setPlanCode(plan.getPlanCode());
        response.setDescription(plan.getDescription());
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

        response.setStatus(plan.isStatus());

        return response;
    }
}
