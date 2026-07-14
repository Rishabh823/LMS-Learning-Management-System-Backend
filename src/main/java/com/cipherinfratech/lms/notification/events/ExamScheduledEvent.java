package com.cipherinfratech.lms.notification.events;


import java.util.Date;
import java.util.UUID;

public record ExamScheduledEvent(

        UUID examId,
        String examName,

        String groupName,
        String institute,

        Date startDate,
        Date endDate,

        Integer duration,
        Double passingMarks,

//        List<ExamMailRecipient> recipients,
        String adminEmail

) {
}