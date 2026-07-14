package com.cipherinfratech.lms.subscription.services;

import com.cipherinfratech.lms.handlers.NotFoundException;
import com.cipherinfratech.lms.handlers.ValidationException;
import com.cipherinfratech.lms.organizations.entities.Organizations;
import com.cipherinfratech.lms.organizations.repositories.OrganizationsRepo;
import com.cipherinfratech.lms.subscription.dto.*;
import com.cipherinfratech.lms.subscription.entities.PaymentTransaction;
import com.cipherinfratech.lms.subscription.entities.SubscriptionPlan;
import com.cipherinfratech.lms.subscription.enums.PaymentStatus;
import com.cipherinfratech.lms.subscription.repositories.PaymentTransactionRepo;
import com.cipherinfratech.lms.subscription.repositories.SubscriptionPlanRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private SubscriptionPlanRepo subscriptionPlanRepo;
    private PaymentTransactionRepo paymentTransactionRepo;
    private OrganizationsRepo organizationsRepo;
    private RazorpayService razorpayService;
    private OrganizationRegistrationService organizationRegistrationService;

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {

        SubscriptionPlan plan = subscriptionPlanRepo.findById(request.getPlanId())
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        if (!plan.isStatus()) {
            throw new ValidationException("This plan is not currently available");
        }

        if (plan.getPrice() == null || plan.getPrice().signum() <= 0) {
            throw new ValidationException("This plan does not require payment - register directly instead");
        }

        RazorpayService.RazorpayOrder order = razorpayService.createOrder(
                plan.getPrice(), plan.getCurrency(), "reg_" + UUID.randomUUID()
        );

        PaymentTransaction transaction = new PaymentTransaction();
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
    public OrganizationRegistrationResponse verifyPayment(VerifyPaymentRequest request) {

        PaymentTransaction transaction = paymentTransactionRepo.findByProviderOrderId(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (transaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new ValidationException("This payment has already been verified");
        }

        if (!transaction.getSubscriptionPlan().getPlanId().equals(request.getPlanId())) {
            throw new ValidationException("Plan does not match the order");
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

        OrganizationRegistrationResponse response = organizationRegistrationService.createOrganizationWithPlan(
                request.getOrganization(), request.getAdmin(), transaction.getSubscriptionPlan()
        );

        Organizations organization = organizationsRepo.findById(response.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        transaction.setOrganization(organization);
        paymentTransactionRepo.save(transaction);

        return response;
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {

        if (!razorpayService.verifyWebhookSignature(payload, signature)) {
            throw new ValidationException("Invalid webhook signature");
        }

        try {
            JsonNode root = new ObjectMapper().readTree(payload);
            JsonNode entity = root.path("payload").path("payment").path("entity");

            String orderId = entity.path("order_id").asText(null);
            String event = root.path("event").asText("");

            if (orderId == null) {
                return;
            }

            paymentTransactionRepo.findByProviderOrderId(orderId).ifPresent(transaction -> {

                if (transaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
                    return;
                }

                if (event.contains("captured")) {
                    transaction.setPaymentStatus(PaymentStatus.SUCCESS);
                    transaction.setProviderPaymentId(entity.path("id").asText(transaction.getProviderPaymentId()));
                    paymentTransactionRepo.save(transaction);
                } else if (event.contains("failed")) {
                    transaction.setPaymentStatus(PaymentStatus.FAILED);
                    paymentTransactionRepo.save(transaction);
                }
            });

        } catch (Exception e) {
            throw new ValidationException("Unable to process webhook payload: " + e.getMessage());
        }
    }

    @Override
    public List<PaymentTransactionResponse> getPaymentHistory(long organizationId) {

        return paymentTransactionRepo.findByOrganization_OrganizationIdOrderByCreatedDateDesc(organizationId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentTransactionResponse mapToResponse(PaymentTransaction transaction) {

        PaymentTransactionResponse response = new PaymentTransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setPlanName(transaction.getSubscriptionPlan().getPlanName());
        response.setPlanCode(transaction.getSubscriptionPlan().getPlanCode());
        response.setProvider(transaction.getProvider());
        response.setProviderOrderId(transaction.getProviderOrderId());
        response.setProviderPaymentId(transaction.getProviderPaymentId());
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency());
        response.setPaymentStatus(transaction.getPaymentStatus() != null ? transaction.getPaymentStatus().name() : null);
        response.setCreatedDate(transaction.getCreatedDate());

        return response;
    }
}
