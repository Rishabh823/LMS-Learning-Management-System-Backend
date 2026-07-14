package com.cipherinfratech.lms.subscription.repositories;

import com.cipherinfratech.lms.subscription.entities.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByProviderOrderId(String providerOrderId);

    List<PaymentTransaction> findByOrganization_OrganizationIdOrderByCreatedDateDesc(long organizationId);
}
