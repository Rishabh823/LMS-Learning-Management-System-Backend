package com.cipherinfratech.lms.notification.events;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record QuizSubmittedEvent(

        UUID quizId,
        String quizName,

        Long courseId,
        String courseName,

        String studentEmail,
        BigDecimal result,

        String trainerEmail,
        String organizationEmail,

        List<String> adminEmails

) {}