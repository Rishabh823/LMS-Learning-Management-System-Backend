package com.cipherinfratech.lms.auth.controllers;

import com.cipherinfratech.lms.auth.dto.CreateOrgAdminRequest;
import com.cipherinfratech.lms.auth.dto.TrainerSignupRequest;
import com.cipherinfratech.lms.organizations.repositories.OrganizationsRepo;
import com.cipherinfratech.lms.utils.ResponseModels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.cipherinfratech.lms.users.entity.Roles;
import com.cipherinfratech.lms.users.entity.Users;
import com.cipherinfratech.lms.users.entity.UsersProfile;
import com.cipherinfratech.lms.users.services.UserService;

import jakarta.validation.Valid;

import java.security.Principal;

@RestController
@RequestMapping("/signup")
@CrossOrigin
public class SignupController {

	@Autowired
	private UserService userService;

	@Autowired
	private OrganizationsRepo organizationsRepo;

	@Autowired
	PasswordEncoder passwordEncoder;

	@PostMapping("/admin")
	public ResponseEntity<?> adminSignup(@Valid @RequestBody Users users) {

//		long count = userService.countByRole(Roles.ADMIN);
//
//		if(count != 0)
//			throw new ValidationException("Admin is already registered");

		UserDetails checkUser = userService.getUserByEmail(users.getEmailId());
		if (checkUser != null) {
			return ResponseEntity.ok("User already register with this email");
		} else {
			UsersProfile profile = new UsersProfile();
			users.setPassword(passwordEncoder.encode(users.getPassword()));
			users.setRole(Roles.ADMIN);
			profile.setUsers(users);
			users.setUsersProfile(profile);
			this.userService.saveNewUser(users);
			return ResponseModels.success("Admin registered");
		}

	}

	@PostMapping("/user")
	public ResponseEntity<?> userSignup(@RequestBody Users users,Principal principal) {

		UserDetails checkUser = userService.getUserByEmail(users.getEmailId());
		if (checkUser != null) {
			return ResponseEntity.ok("User already register with this email");
		} else {
			UsersProfile profile = new UsersProfile();
			users.setPassword(passwordEncoder.encode(users.getPassword()));
			users.setRole(Roles.STUDENT);
			profile.setDegreeName(users.getUsersProfile().getDegreeName());
			profile.setPassingYear(users.getUsersProfile().getPassingYear());
			profile.setPercentage(users.getUsersProfile().getPercentage());
			profile.setUsers(users);
			users.setCreatedBy(principal.getName());
			users.setUsersProfile(profile);
			this.userService.saveNewUser(users);
			return ResponseEntity.ok("user registered");
		}
	}

	@PreAuthorize("hasAnyRole('ORGANIZATION','ORG_ADMIN','ADMIN')")
	@PostMapping("/trainer")
	public ResponseEntity<Object> trainerSignup(
			@Valid @RequestBody TrainerSignupRequest request,
			Principal principal) {

		userService.createTrainer(request, principal.getName());

		return ResponseModels.success("Trainer registered");
	}

	/**
	 * Lets an organization owner (or an org admin they already created) create
	 * another admin scoped to their own organization - the org is derived from
	 * the authenticated caller, never trusted from client input.
	 */
	@PreAuthorize("hasAnyRole('ORGANIZATION','ORG_ADMIN')")
	@PostMapping("/org-admin")
	public ResponseEntity<Object> orgAdminSignup(
			@Valid @RequestBody CreateOrgAdminRequest request,
			Principal principal) {

		userService.createOrgAdmin(request, principal.getName());

		return ResponseModels.success("Organization admin registered");
	}

	@PostMapping("/organization")
	public ResponseEntity<?> organizationSignup(@RequestBody Users users,Principal principal) {
		UserDetails checkUser = userService.getUserByEmail(users.getEmailId());
		if (checkUser != null) {
			return ResponseEntity.ok("User already register with this email");
		} else {
			UsersProfile profile = new UsersProfile();
			profile.setDegreeName(users.getUsersProfile().getDegreeName());
			profile.setPassingYear(users.getUsersProfile().getPassingYear());
			profile.setTotalExprience(users.getUsersProfile().getTotalExprience());
			profile.setPercentage(users.getUsersProfile().getPercentage());
			users.setCreatedBy(principal.getName());
			users.setPassword(passwordEncoder.encode(users.getPassword()));
			users.setRole(Roles.ORGANIZATION);
			profile.setUsers(users);
			users.setUsersProfile(profile);
			this.userService.saveNewUser(users);
			return ResponseEntity.ok("Email id register as Organization");
		}
	}

}
