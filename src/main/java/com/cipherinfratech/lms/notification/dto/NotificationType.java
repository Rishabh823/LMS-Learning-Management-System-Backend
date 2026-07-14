package com.cipherinfratech.lms.notification.dto;

public enum NotificationType {
    ORGANIZATION_CREATED,       // admin creates org
    ORGANIZATION_GROUP_CREATED,     // admin, org creates org groups
    TRAINER_CREATED,        // admin, org creates trainer
    TRAINER_ASSIGNED,       // admin assigns trainer to org
    ORGANIZATION_GROUP_USER_CREATED,        // admin, org creates students in org groups
    COURSE_CREATED,     // admin, org, trainer creates course
    ORGANIZATION_COURSE_ASSIGNED,       // admin assigns course to org
    TRAINER_COURSE_ASSIGNED,        // admin, org assigns course to trainer
    GROUP_COURSE_ASSIGNED,        // admin, org, trainer assigns course to org group
    EXAM_CREATED,       // admin, org, trainer creates exam per course
    QUIZ_CREATED,       // admin, org, trainer creates quiz per module
    LIVE_TRAINING_CREATED,      // admin, org, trainer creates live training per org group
    EXAM_SUBMITTED,     // student submits exam
    QUIZ_SUBMITTED,         // student submits quiz

    EXTERNAL_EXAM_SCHEDULED
}
