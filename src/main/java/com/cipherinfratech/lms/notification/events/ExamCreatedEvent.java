package com.cipherinfratech.lms.notification.events;

import java.util.List;

public record ExamCreatedEvent(

        Long examId,
        String examName,

        Long courseId,
        String courseName,

        String organizationEmail,
        String organizationName,

        List<String> adminEmails,
        List<String> studentEmails,
        String trainerEmail

) {}
