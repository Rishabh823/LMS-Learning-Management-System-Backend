package com.cipherinfratech.lms.notification.events;

import java.util.List;
import java.util.UUID;

public record TrainerAssignedToOrganizationEvent(

        Long organizationId,
        String organizationName,
        String organizationEmail,

        List<UUID> trainerIds,
        List<String> trainerEmails,

        List<String> adminEmails
) {}