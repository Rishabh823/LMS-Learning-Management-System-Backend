package com.cipherinfratech.lms.notification.events;

import java.util.List;
import java.util.UUID;

public record QuizCreatedEvent(

        UUID quizId,
        String quizName,

        Long moduleId,
        String moduleName,

        Long courseId,
        String courseName,

        String organizationEmail,
        String organizationName,

        List<String> adminEmails,
        List<String> studentEmails,
        String trainerEmail

) {}
