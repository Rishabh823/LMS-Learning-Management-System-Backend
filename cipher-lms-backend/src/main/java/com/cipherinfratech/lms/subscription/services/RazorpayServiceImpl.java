package com.cipherinfratech.lms.subscription.services;

import com.cipherinfratech.lms.handlers.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {

    private static final String ORDERS_URL = "https://api.razorpay.com/v1/orders";

    private final RestTemplate restTemplate;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    @Override
    public RazorpayOrder createOrder(BigDecimal amount, String currency, String receipt) {

        long amountInSmallestUnit = amount.multiply(BigDecimal.valueOf(100)).longValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(keyId, keySecret);

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amountInSmallestUnit);
        body.put("currency", currency);
        body.put("receipt", receipt);
        body.put("payment_capture", 1);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    ORDERS_URL, new HttpEntity<>(body, headers), Map.class);

            Map<?, ?> responseBody = response.getBody();

            if (responseBody == null || responseBody.get("id") == null) {
                throw new ValidationException("Failed to create Razorpay order");
            }

            return new RazorpayOrder((String) responseBody.get("id"), amountInSmallestUnit, currency);

        } catch (RestClientException e) {
            throw new ValidationException("Razorpay order creation failed: " + e.getMessage());
        }
    }

    @Override
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        return verifyHmac(orderId + "|" + paymentId, signature, keySecret);
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        return verifyHmac(payload, signature, webhookSecret);
    }

    private boolean verifyHmac(String payload, String signature, String secret) {

        if (signature == null || signature.isBlank()) {
            return false;
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computed = HexFormat.of().formatHex(hash);
            return computed.equalsIgnoreCase(signature);
        } catch (Exception e) {
            return false;
        }
    }
}
