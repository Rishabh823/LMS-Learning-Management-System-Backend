package com.cipherinfratech.lms.subscription.entities;

import com.cipherinfratech.lms.organizations.entities.Organizations;
import com.cipherinfratech.lms.subscription.enums.PaymentStatus;
import com.cipherinfratech.lms.subscription.enums.SubscriptionStatus;
import com.cipherinfratech.lms.utils.Tracker;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "organization_subscription")
public class OrganizationSubscription extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;

    @OneToOne
    @JoinColumn(name = "organization_id", nullable = false)
    @JsonBackReference
    private Organizations organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Temporal(TemporalType.TIMESTAMP)
    private Date subscriptionStartDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date subscriptionEndDate;

    private boolean autoRenew = true;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.SUCCESS;

    // Downgrade requested - applied by the scheduled job once the current
    // billing cycle (subscriptionEndDate) ends, not immediately.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pending_plan_id")
    private SubscriptionPlan pendingPlan;

    @Temporal(TemporalType.TIMESTAMP)
    private Date pendingPlanEffectiveDate;

}
