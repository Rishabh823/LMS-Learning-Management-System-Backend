package com.cipherinfratech.lms.users.controllers;

import java.util.*;

import com.cipherinfratech.lms.organizations.services.OrganizationsService;
import com.cipherinfratech.lms.users.dto.UserUpdateRequest;
import com.cipherinfratech.lms.users.entity.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.cipherinfratech.lms.users.services.UserService;
import com.cipherinfratech.lms.utils.FileFormats;
import com.cipherinfratech.lms.utils.FileUtils;
import com.cipherinfratech.lms.utils.Messages;
import com.cipherinfratech.lms.utils.ResponseModels;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@CrossOrigin
@AllArgsConstructor
public class UsersController {
	private UserService userService;
	private OrganizationsService organizationsService;
	private PasswordEncoder passwordEncoder;

	@GetMapping()
	public ResponseEntity<Object> getAllInOne() {

		try {
			List<Users> getAll = this.userService.allUsers();
			return ResponseModels.successWithPayload("All Existing user List", getAll);

		} catch (Exception e) {
			return ResponseModels.unknownError();
		}
	}


	@PutMapping("/update")
	public ResponseEntity<Object> updateUserDetails(@RequestBody UserUpdateRequest updatedData) {
		try {
			UserDetailsResponse response = userService.updateUserDetails(updatedData);

			if (response == null) {
				return ResponseModels.error("User not found");
			}

			return ResponseModels.successWithPayload(Messages.updatedMessage, response);
		} catch (Exception e) {
			return ResponseModels.exceptionError(e);
		}
	}

	@PutMapping("/updateProfilePic")
	public ResponseEntity<?> uploadCategoryImage(@RequestParam("file") MultipartFile file,
			@RequestParam("userId") UUID userId) {
		try {
			Users user = this.userService.getUserByUserId(userId);
			if (user == null) {
				return ResponseModels.error("User not found");
			}

			String fileType = file.getContentType();
			if (file.isEmpty()) {
				return ResponseModels.error("Select a file");
			}
			if (!FileFormats.userProfilePictureFormat().contains(fileType)) {
				return ResponseModels.unsupportedMediaType("Unsupported Media type");
			}

			user.setProfilePic(file.getOriginalFilename());
			user.setProfilePicFile(FileUtils.compressFile(file.getBytes()));
			user.setProfilePicType(fileType);
			this.userService.updateUser(user);

			return ResponseModels.update("Profile Pic changed successfully");
		} catch (Exception e) {
			return ResponseModels.exceptionError(e);
		}
	}

	@GetMapping("/updateProfilePic")
	public ResponseEntity<Object> getProfilePic(@RequestParam UUID userId) {
		try {
			Users user = this.userService.getUserByUserId(userId);
			if (user == null) {
				return ResponseModels.error("User not found");
			}

			if (user.getProfilePicFile() == null || user.getProfilePicType() == null) {
				return ResponseModels.successWithPayload("No profile picture found for the user", (Object) null);
			}

			return ResponseModels.sendMedia(user.getProfilePicFile(), user.getProfilePicType());
		} catch (Exception e) {
			return ResponseModels.exceptionError(e);
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
	@PutMapping("/toggleStatus/{userId}")
	public ResponseEntity<?> toggleUserStatus(
			@PathVariable UUID userId,
			@RequestParam boolean activate
	) {

		userService.toggleUserStatus(userId, activate);

		return ResponseModels.success(
				activate
						? "User activated successfully"
						: "User deactivated successfully"
		);
	}

	@PutMapping("/updatePassword")
	public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String currentUserEmail = authentication.getName();
			Users currentUser = userService.getUserByEmail(currentUserEmail);

			if (currentUser == null) {
				return ResponseModels.error("User not found");
			}
			if (!passwordEncoder.matches(passwordUpdateRequest.getCurrentPassword(), currentUser.getPassword())) {
				return ResponseModels.error("Current password is incorrect");
			}
			if (!passwordUpdateRequest.getNewPassword().equals(passwordUpdateRequest.getConfirmPassword())) {
				return ResponseModels.error("New password and confirm password do not match");
			}
			currentUser.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
			userService.updateUser(currentUser);
			return ResponseModels.success("Password updated successfully");
		} catch (Exception e) {
			return ResponseModels.exceptionError(e);
		}
	}

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN','TRAINER','STUDENT')")
    @GetMapping("/details/{userId}")
    public ResponseEntity<Object> getUserDetails(
            @PathVariable UUID userId) {
        try {
            UserDetailsResponse response =
                    userService.getUserDetails(userId);
            if (response == null) {
                return ResponseModels.error("User not found.");
            }
            return ResponseModels.successWithPayload(
                    "User details fetched successfully.",
                    response
            );
        } catch (Exception e) {
            return ResponseModels.exceptionError(e);
        }
    }
}

