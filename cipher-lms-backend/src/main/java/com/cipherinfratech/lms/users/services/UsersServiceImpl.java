package com.cipherinfratech.lms.users.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.cipherinfratech.lms.auth.dto.CreateOrgAdminRequest;
import com.cipherinfratech.lms.auth.dto.TrainerSignupRequest;
import com.cipherinfratech.lms.handlers.NotFoundException;
import com.cipherinfratech.lms.handlers.ValidationException;
import com.cipherinfratech.lms.notification.events.TrainerCreatedEvent;
import com.cipherinfratech.lms.notification.publisher.NotificationEventPublisher;
import com.cipherinfratech.lms.organizations.entities.Organizations;
import com.cipherinfratech.lms.organizations.repositories.OrganizationsRepo;
import com.cipherinfratech.lms.users.entity.*;
import com.cipherinfratech.lms.utils.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.cipherinfratech.lms.forms.repositories.FormSubmissionRepository;
import com.cipherinfratech.lms.forms.repositories.FormSubmissionValueRepository;
import com.cipherinfratech.lms.forms.repositories.FormFieldRepository;
import com.cipherinfratech.lms.forms.dto.FormAnswerRequest;
import com.cipherinfratech.lms.forms.dto.OnboardingFieldResponse;
import com.cipherinfratech.lms.users.dto.OnboardingResponse;
import com.cipherinfratech.lms.forms.dto.OnboardingSectionResponse;
import com.cipherinfratech.lms.forms.entities.FormField;
import com.cipherinfratech.lms.forms.entities.FormSubmission;
import com.cipherinfratech.lms.forms.entities.FormSubmissionValue;
import com.cipherinfratech.lms.users.dto.OrganizationResponse;
import com.cipherinfratech.lms.users.dto.UserUpdateRequest;
import com.cipherinfratech.lms.subscription.services.SubscriptionLimitValidator;

import java.util.LinkedHashMap;

import com.cipherinfratech.lms.users.repositories.UsersRepo;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UsersServiceImpl implements UserService {

	private UsersRepo usersRepo;

	private OrganizationsRepo organizationsRepo;

	private PasswordEncoder passwordEncoder;

	private NotificationEventPublisher publisher;

    private final FormSubmissionRepository formSubmissionRepository;

    private final FormSubmissionValueRepository formSubmissionValueRepository;

    private final FormFieldRepository formFieldRepository;

    private final SubscriptionLimitValidator subscriptionLimitValidator;

	@Override
	public Users saveNewUser(Users user) {
		return usersRepo.save(user);
	}
	@Override
	public Users updateUser(Users user) {
		return usersRepo.save(user);
	}

	@Override
	public Users getUserByUserId(UUID userId) {
		return this.usersRepo.findByUserId(userId);
	}

	@Override
	public Users getUserByEmail(String userName) {
		try {
			return this.usersRepo.findByEmail(userName);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean updateProfilePic(UUID userId, String profilePic) {
		try {
			this.usersRepo.updateProfilePic(userId, profilePic);
			return true;
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
	}

	// In UserService.java
	public List<Users> getAllTrainersByOrganizationId(Long orgId) {
		// Return all users with TRAINER role that belong to the specified organization
		return this.usersRepo.findByRoleAndOrganizations_OrganizationId(Roles.TRAINER, orgId);
	}

	@Override
	public List<Users> allUsers() {
		return this.usersRepo.findAllByStatusTrue();
	}

	@Override
	public boolean isAdmin(String userName) {
		Users getUser = this.usersRepo.findByEmail(userName);
		return getUser.getRole().equals(Roles.ADMIN);

	}

	@Override
	public boolean existsByEmail(String emailId) {
		return usersRepo.existsByEmailId(emailId);
	}

	@Override
	@Transactional
	public void createTrainer(TrainerSignupRequest request, String createdBy) {

		// check duplicate email
		Users existingUser = usersRepo.findByEmail(request.getEmailId());
		if (existingUser != null) {
			throw new ValidationException("User already registered with this email");
		}

		// fetch current user
		Users currUser = usersRepo.findByEmail(createdBy);
		if (currUser == null) {
			throw new ValidationException("Invalid creator user");
		}

		// map user
		Users user = new Users();
		user.setName(request.getName());
		user.setEmailId(request.getEmailId());
		user.setContactNo(request.getContactNo());
		user.setGender(request.getGender());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(Roles.TRAINER);

		// assign organization if needed
		if (currUser.getRole() == Roles.ORGANIZATION || currUser.getRole() == Roles.ORG_ADMIN) {
			Organizations organization = organizationsRepo.findByUsers(currUser);
			if (organization != null) {
				subscriptionLimitValidator.assertCanAddTrainer(organization.getOrganizationId());
				user.setOrganizations(organization);
				user.setInstructType("internal");
			}
		} else if (currUser.getRole() == Roles.ADMIN) {
			user.setInstructType("admin");
		}

		// map profile
		TrainerSignupRequest.UsersProfileDto dto = request.getUsersProfile();

		UsersProfile profile = new UsersProfile();
		profile.setDegreeName(dto.getDegreeName());
		profile.setPassingYear(dto.getPassingYear());
		profile.setPercentage(dto.getPercentage());
		profile.setTotalExprience(dto.getTotalExprience());

		profile.setUsers(user);
		user.setUsersProfile(profile);

		usersRepo.save(user);

		// fetch admins
		List<String> adminEmails = usersRepo.findEmailsByRole(Roles.ADMIN);

// fetch org (if exists)
		Organizations org = user.getOrganizations();

// 🔥 publish event
		publisher.publish(
				buildTrainerCreatedEvent(user, org, adminEmails)
		);
	}

	@Override
	@Transactional
	public void createOrgAdmin(CreateOrgAdminRequest request, String createdBy) {

		// check duplicate email
		Users existingUser = usersRepo.findByEmail(request.getEmailId());
		if (existingUser != null) {
			throw new ValidationException("User already registered with this email");
		}

		// fetch current user
		Users currUser = usersRepo.findByEmail(createdBy);
		if (currUser == null) {
			throw new ValidationException("Invalid creator user");
		}

		if (currUser.getRole() != Roles.ORGANIZATION && currUser.getRole() != Roles.ORG_ADMIN) {
			throw new ValidationException("Only an organization can create admins for itself");
		}

		Organizations organization = organizationsRepo.findByUsers(currUser);
		if (organization == null) {
			throw new ValidationException("Organization not mapped to user");
		}

		subscriptionLimitValidator.assertCanAddAdmin(organization.getOrganizationId());

		// map user
		Users user = new Users();
		user.setName(request.getName());
		user.setEmailId(request.getEmailId());
		user.setContactNo(request.getContactNo());
		user.setGender(request.getGender());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(Roles.ORG_ADMIN);
		user.setOrganizations(organization);

		// map profile
		UsersProfile profile = new UsersProfile();
		profile.setDesignation(request.getDesignation());
		profile.setDepartment(request.getDepartment());
		profile.setUsers(user);
		user.setUsersProfile(profile);

		usersRepo.save(user);
	}

	@Transactional
	@Override
	public void toggleUserStatus(UUID userId, boolean activate) {

		Users currentUser = SecurityUtil.getCurrentUser();

		if (currentUser == null) {
			throw new NotFoundException("Current user not found");
		}

		Roles currentUserRole = currentUser.getRole();

		Users targetUser = usersRepo.findByUserId(userId);

		if (targetUser == null) {
			throw new NotFoundException("User not found");
		}

		if (currentUserRole == Roles.ADMIN) {
			targetUser.setStatus(activate);
			usersRepo.save(targetUser);
			return;
		}

		if (currentUserRole == Roles.ORGANIZATION || currentUserRole == Roles.ORG_ADMIN) {

			Long organizationId =
					currentUser.getOrganizations().getOrganizationId();

			boolean allowed = usersRepo.canOrganizationManageUser(
					userId,
					organizationId
			);

			if (!allowed) {
				throw new ValidationException(
						"You are not allowed to modify this user"
				);
			}

			targetUser.setStatus(activate);
			usersRepo.save(targetUser);
			return;
		}

		throw new ValidationException(
				"You are not allowed to modify users"
		);
	}

	private TrainerCreatedEvent buildTrainerCreatedEvent(
			Users trainer,
			Organizations org,
			List<String> adminEmails
	) {
		return new TrainerCreatedEvent(
				trainer.getEmailId(),
				trainer.getName(),
				org != null ? org.getEmailId() : null,
				org != null ? org.getFullName() : null,
				adminEmails
		);
	}

    @Override
    @Transactional(readOnly = true)
    public UserDetailsResponse getUserDetails(UUID userId) {

        /*
         * Logged In User
         */
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Users currentUser =
                usersRepo.findByEmail(authentication.getName());

        /*
         * Requested User
         */
        Users user =
                usersRepo.findByUserId(userId);

        if (user == null) {
            return null;
        }

        /*
         * Authorization
         */

        if (currentUser.getRole() == Roles.STUDENT &&
                !currentUser.getUserId().equals(userId)) {

            throw new ValidationException(
                    "You are not authorized to view this user."
            );
        }

        if (currentUser.getRole() == Roles.ORGANIZATION || currentUser.getRole() == Roles.ORG_ADMIN) {

            if (user.getOrganizations() == null ||
                    currentUser.getOrganizations() == null ||
                    user.getOrganizations().getOrganizationId()
                            != currentUser.getOrganizations().getOrganizationId()) {

                throw new ValidationException(
                        "You are not authorized to view this user."
                );
            }
        }

        /*
         * Build Response
         */

        UserDetailsResponse response =
                new UserDetailsResponse(user);

        /*
         * Organization
         */

        if (user.getOrganizations() != null) {

            OrganizationResponse organization =
                    new OrganizationResponse();

            organization.setOrganizationId(
                    user.getOrganizations().getOrganizationId()
            );

            organization.setFullName(
                    user.getOrganizations().getFullName()
            );

            response.setOrganization(organization);
        }

        /*
         * Onboarding
         */

        FormSubmission submission =
                formSubmissionRepository
                        .findByUser_UserId(userId)
                        .orElse(null);

        if (submission == null) {
            return response;
        }

        OnboardingResponse onboarding =
                new OnboardingResponse();

        onboarding.setSubmitted(
                submission.isSubmitted()
        );

        onboarding.setSubmittedDate(
                submission.getCreatedDate()
        );

        Map<String, OnboardingSectionResponse> sectionMap =
                new LinkedHashMap<>();

        for (FormSubmissionValue value : submission.getValues()) {

            String sectionTitle =
                    value.getField()
                            .getSection()
                            .getTitle();

            OnboardingSectionResponse section =
                    sectionMap.computeIfAbsent(
                            sectionTitle,
                            key -> {

                                OnboardingSectionResponse s =
                                        new OnboardingSectionResponse();

                                s.setSectionTitle(key);

                                return s;

                            });

            OnboardingFieldResponse field =
                    new OnboardingFieldResponse();

            field.setFieldId(
                    value.getField().getId()
            );

            field.setLabel(
                    value.getField().getLabel()
            );

            field.setType(
                    value.getField()
                            .getFieldType()
                            .name()
            );

            field.setRequired(
                    value.getField().isRequired()
            );

            field.setValue(
                    value.getValue()
            );

            section.getFields().add(field);
        }

        onboarding.getSections().addAll(
                sectionMap.values()
        );

        response.setOnboarding(onboarding);

        return response;
    }

    @Override
    @Transactional
    public UserDetailsResponse updateUserDetails(UserUpdateRequest request) {

        Users user = usersRepo.findByUserId(request.getUserId());

        if (user == null) {
            return null;
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }

        if (request.getContactNo() != null) {
            user.setContactNo(request.getContactNo());
        }

        usersRepo.save(user);

        if (request.getOnboardingAnswers() != null &&
                !request.getOnboardingAnswers().isEmpty()) {

            FormSubmission submission = formSubmissionRepository
                    .findByUser_UserId(request.getUserId())
                    .orElse(null);

            if (submission != null) {

                for (FormAnswerRequest answer : request.getOnboardingAnswers()) {

                    FormSubmissionValue existingValue = submission.getValues().stream()
                            .filter(v -> v.getField().getId().equals(answer.getFieldId()))
                            .findFirst()
                            .orElse(null);

                    if (existingValue != null) {
                        existingValue.setValue(answer.getValue());
                        formSubmissionValueRepository.save(existingValue);
                        continue;
                    }

                    FormField field = formFieldRepository
                            .findById(answer.getFieldId())
                            .orElse(null);

                    if (field == null) {
                        continue;
                    }

                    FormSubmissionValue newValue = new FormSubmissionValue();
                    newValue.setSubmission(submission);
                    newValue.setField(field);
                    newValue.setValue(answer.getValue());
                    formSubmissionValueRepository.save(newValue);
                }
            }
        }

        return getUserDetails(request.getUserId());
    }

}