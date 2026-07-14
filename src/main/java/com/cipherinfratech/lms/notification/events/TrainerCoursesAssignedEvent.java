package com.cipherinfratech.lms.notification.events;

import java.util.List;
import java.util.UUID;

public record TrainerCoursesAssignedEvent(

        UUID trainerId,
        String trainerName,
        String trainerEmail,

        List<Long> courseIds,

        List<String> adminEmails
) {}