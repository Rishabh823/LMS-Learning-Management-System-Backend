package com.cipherinfratech.lms.notification.events;

import java.util.List;

public record OrganizationGroupCreatedEvent(

        Long organizationId,
        String organizationName,
        String groupName,
        String organizationEmail,
        List<String> adminEmails

) {}
