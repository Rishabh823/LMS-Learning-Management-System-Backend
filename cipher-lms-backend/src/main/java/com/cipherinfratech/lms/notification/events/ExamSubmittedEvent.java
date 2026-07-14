package com.cipherinfratech.lms.notification.events;

import java.math.BigDecimal;
import java.util.List;

public record ExamSubmittedEvent(

        Long examId,
        String examName,

        Long courseId,
        String courseName,

        String studentEmail,
        BigDecimal result,

        String trainerEmail,
        String organizationEmail,

        List<String> adminEmails

) {}