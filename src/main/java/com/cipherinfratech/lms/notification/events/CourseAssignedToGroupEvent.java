package com.cipherinfratech.lms.notification.events;

import java.util.List;

public record CourseAssignedToGroupEvent(

        Long groupId,
        String groupName,

        List<String> courseNames,

        String organizationEmail,
        String organizationName,

        List<String> adminEmails,
        List<String> studentEmails,
        String trainerEmail

) {}