package com.cipherinfratech.lms.subscription.services;

import com.cipherinfratech.lms.handlers.ValidationException;
import com.cipherinfratech.lms.subscription.entities.SubscriptionPlan;
import com.cipherinfratech.lms.subscription.repositories.OrganizationSubscriptionRepo;
import com.cipherinfratech.lms.users.entity.Roles;
import com.cipherinfratech.lms.users.repositories.UsersRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Reused by user/course/group creation flows to enforce the organization's
 * current SubscriptionPlan limits before a new record is created.
 */
@Component
@AllArgsConstructor
public class SubscriptionLimitValidator {

    private OrganizationSubscriptionRepo organizationSubscriptionRepo;
    private UsersRepo usersRepo;

    public void assertCanAddStudent(long organizationId) {

        Integer max = getPlan(organizationId).getMaxStudents();

        if (max == null) {
            return;
        }

        int current = usersRepo.countByRoleAndOrganizationsOrganizationId(Roles.STUDENT, organizationId);

        if (current >= max) {
            throw new ValidationException("Student limit reached. Please upgrade your subscription.");
        }
    }

    public void assertCanAddTrainer(long organizationId) {

        Integer max = getPlan(organizationId).getMaxTrainers();

        if (max == null) {
            return;
        }

        int current = usersRepo.countByRoleAndOrganizationsOrganizationId(Roles.TRAINER, organizationId);

        if (current >= max) {
            throw new ValidationException("Trainer limit reached. Please upgrade your subscription.");
        }
    }

//    public void assertCanAddCourse(long organizationId) {
//
//        Integer max = getPlan(organizationId).getMaxCourses();
//
//        if (max == null) {
//            return;
//        }
//
//        long current = courseRepo.countByOrganizationsOrganizationId(organizationId);
//
//        if (current >= max) {
//            throw new ValidationException("Course limit reached. Please upgrade your subscription.");
//        }
//    }

    public void assertCanAddAdmin(long organizationId) {

        Integer max = getPlan(organizationId).getMaxAdmins();

        if (max == null) {
            return;
        }

        int current = usersRepo.countByRoleAndOrganizationsOrganizationId(Roles.ORGANIZATION, organizationId)
                + usersRepo.countByRoleAndOrganizationsOrganizationId(Roles.ORG_ADMIN, organizationId);

        if (current >= max) {
            throw new ValidationException("Admin limit reached. Please upgrade your subscription.");
        }
    }

    private SubscriptionPlan getPlan(long organizationId) {

        return organizationSubscriptionRepo.findByOrganization_OrganizationId(organizationId)
                .map(subscription -> {
                    if (subscription.getSubscriptionPlan() == null) {
                        throw new ValidationException("No active subscription plan found for this organization");
                    }
                    return subscription.getSubscriptionPlan();
                })
                .orElseThrow(() -> new ValidationException("No active subscription found for this organization"));
    }
}
