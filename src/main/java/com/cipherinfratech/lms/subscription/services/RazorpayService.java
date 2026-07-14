package com.cipherinfratech.lms.subscription.services;

import java.math.BigDecimal;

public interface RazorpayService {

    RazorpayOrder createOrder(BigDecimal amount, String currency, String receipt);

    boolean verifySignature(String orderId, String paymentId, String signature);

    boolean verifyWebhookSignature(String payload, String signature);

    record RazorpayOrder(String orderId, long amount, String currency) {
    }
}
