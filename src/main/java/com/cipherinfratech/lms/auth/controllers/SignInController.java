package com.cipherinfratech.lms.auth.controllers;

import java.util.HashMap;
import java.util.Map;

import com.cipherinfratech.lms.handlers.NotFoundException;
import com.cipherinfratech.lms.organizations.entities.OrganizationApproval;
import com.cipherinfratech.lms.organizations.enums.ApprovalStatus;
import com.cipherinfratech.lms.organizations.repositories.OrganizationApprovalRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.cipherinfratech.lms.security.JwtService;

@RestController
@CrossOrigin
@AllArgsConstructor
public class SignInController {

	private AuthenticationManager authenticationManager;

	private JwtService jwtService;

	private OrganizationApprovalRepo organizationApprovalRepo;

	PasswordEncoder passwordEncoder;


	@GetMapping("/test")
	ResponseEntity<String> testTheApplication() {

		return ResponseEntity.ok("LMS Application is working fine");
	}
}
