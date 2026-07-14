package com.cipherinfratech.lms.subscription.services;

import com.cipherinfratech.lms.subscription.dto.CreateOrderRequest;
import com.cipherinfratech.lms.subscription.dto.CreateOrderResponse;
import com.cipherinfratech.lms.subscription.dto.OrganizationRegistrationResponse;
import com.cipherinfratech.lms.subscription.dto.PaymentTransactionResponse;
import com.cipherinfratech.lms.subscription.dto.VerifyPaymentRequest;

import java.util.List;

public interface PaymentService {

    CreateOrderResponse createOrder(CreateOrderRequest request);

    OrganizationRegistrationResponse verifyPayment(VerifyPaymentRequest request);

    void handleWebhook(String payload, String signature);

    List<PaymentTransactionResponse> getPaymentHistory(long organizationId);

}
