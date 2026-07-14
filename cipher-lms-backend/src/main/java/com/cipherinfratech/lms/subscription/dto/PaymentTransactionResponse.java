package com.cipherinfratech.lms.subscription.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentTransactionResponse {

    private Long transactionId;
    private String planName;
    private String planCode;
    private String provider;
    private String providerOrderId;
    private String providerPaymentId;
    private BigDecimal amount;
    private String currency;
    private String paymentStatus;
    private Date createdDate;

}
