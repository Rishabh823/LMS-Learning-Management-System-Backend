package com.cipherinfratech.lms.users.services;

import java.util.List;
import java.util.UUID;

import com.cipherinfratech.lms.auth.dto.CreateOrgAdminRequest;
import com.cipherinfratech.lms.auth.dto.TrainerSignupRequest;
import com.cipherinfratech.lms.users.dto.UserUpdateRequest;
import com.cipherinfratech.lms.users.entity.UserDetailsResponse;
import jakarta.validation.Valid;

import com.cipherinfratech.lms.users.entity.Users;

public interface UserService {

	Users saveNewUser(Users user);

	Users updateUser(Users user);

	Users getUserByEmail(String userName);

	Users getUserByUserId(UUID userId);

	boolean updateProfilePic(UUID userId, String profilePic);
	
	List<Users> allUsers();

	boolean isAdmin(String userName);

	boolean existsByEmail(String emailId);

	void createTrainer(@Valid TrainerSignupRequest request, String name);

	void createOrgAdmin(@Valid CreateOrgAdminRequest request, String createdBy);

    void toggleUserStatus(UUID userId, boolean activate);

    UserDetailsResponse getUserDetails(UUID userId);

    UserDetailsResponse updateUserDetails(UserUpdateRequest request);
}
