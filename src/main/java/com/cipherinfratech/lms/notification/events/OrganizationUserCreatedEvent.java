package com.cipherinfratech.lms.notification.events;

import java.util.List;

public record OrganizationUserCreatedEvent(

        String userEmail,
        String userName,

        Long organizationId,
        String organizationEmail,

        Long groupId,
        String groupName,

        List<String> adminEmails

) {}