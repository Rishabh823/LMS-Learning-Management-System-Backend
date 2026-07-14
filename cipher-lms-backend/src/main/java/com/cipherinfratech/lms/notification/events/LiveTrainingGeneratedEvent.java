package com.cipherinfratech.lms.notification.events;

import java.util.List;

public record LiveTrainingGeneratedEvent(

        Long liveTrainingId,
        String trainingName,

        String meetLink,

        String trainerEmail,
        String organizationEmail,

        List<String> adminEmails,
        List<String> studentEmails

) {}
