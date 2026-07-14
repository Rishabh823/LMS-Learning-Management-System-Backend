package com.cipherinfratech.lms.notification.events;

import java.util.List;

public record CourseCreatedEvent(

        Long courseId,
        String courseName,

        String trainerEmail,
        String trainerName,

        String organizationEmail,
        String organizationName,

        List<String> adminEmails

) {}