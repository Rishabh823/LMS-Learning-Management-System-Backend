package com.cipherinfratech.lms.notification.events;

import java.util.List;

public record OrganizationCreatedEvent(

        Long organizationId,
        String organizationName,
        String ownerName,
        String organizationEmail,
        List<String> adminEmails
) {}