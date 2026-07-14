package com.cipherinfratech.lms.subscription.controllers;

import com.cipherinfratech.lms.subscription.dto.CreateOrderRequest;
import com.cipherinfratech.lms.subscription.dto.VerifyPaymentRequest;
import com.cipherinfratech.lms.subscription.services.PaymentService;
import com.cipherinfratech.lms.utils.ResponseModels;
import com.cipherinfratech.lms.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {

    private PaymentService paymentService;

    /**
     * Public - creates a Razorpay order for a paid plan signup.
     * Frontend opens Razorpay Checkout with the returned orderId.
     */
    @PostMapping("/create-order")
    public ResponseEntity<Object> createOrder(@Valid @RequestBody CreateOrderRequest request) {

        return ResponseModels.successWithPayload(
                "Order created successfully",
                paymentService.createOrder(request)
        );
    }

    /**
     * Public - verifies the Razorpay signature and, on success, creates the
     * organization + admin + subscription + payment record, then returns a JWT
     * just like /login.
     */
    @PostMapping("/verify")
    public ResponseEntity<Object> verifyPayment(@Valid @RequestBody VerifyPaymentRequest request) {

        return ResponseModels.successWithPayload(
                "Payment verified. Organization created successfully.",
                paymentService.verifyPayment(request)
        );
    }

    /**
     * Public (Razorpay calls this directly) - validated via X-Razorpay-Signature,
     * not JWT. Reconciles payment status as a reliability net alongside /verify.
     */
    @PostMapping("/webhook")
    public ResponseEntity<Object> webhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        paymentService.handleWebhook(payload, signature);

        return ResponseModels.success("Webhook processed");
    }

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @GetMapping("/history")
    public ResponseEntity<Object> getPaymentHistory(@RequestParam long organizationId) {

        SecurityUtil.assertCanManageOrganization(organizationId);

        return ResponseModels.successWithPayload(
                "Payment history fetched successfully",
                paymentService.getPaymentHistory(organizationId)
        );
    }

}
