package com.cipherinfratech.lms.notification.events;

import java.util.List;

public record CoursesAssignedToOrganizationEvent(

        Long organizationId,
        String organizationName,
        String organizationEmail,

        List<Long> courseIds,

        List<String> adminEmails
) {}