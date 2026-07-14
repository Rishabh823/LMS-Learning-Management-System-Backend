package com.cipherinfratech.lms.subscription.entities;

import com.cipherinfratech.lms.organizations.entities.Organizations;
import com.cipherinfratech.lms.subscription.enums.PaymentStatus;
import com.cipherinfratech.lms.utils.Tracker;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "payment_transaction")
public class PaymentTransaction extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    // Nullable - order can be created for a prospective org before it exists;
    // populated once the organization is created after successful verification.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    @JsonIgnoreProperties({"users", "courses", "liveTrainings", "groups"})
    private Organizations organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    private String provider = "RAZORPAY";

    private String providerOrderId;

    private String providerPaymentId;

    @Column(columnDefinition = "TEXT")
    private String providerSignature;

    private BigDecimal amount;

    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

}
