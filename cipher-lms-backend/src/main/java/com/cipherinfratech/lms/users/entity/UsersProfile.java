package com.cipherinfratech.lms.users.entity;

import com.cipherinfratech.lms.utils.Tracker;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UsersProfile extends Tracker {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long userProfileId;

	private String dob;
	private String designation;
	private String department;

	// highest Education
	private String degreeName;
	private String passingYear;
	private String percentage;
	
	//Exprience
	private int totalExprience;

	// address
	private String address;
	private String cityId;
	
	//Social connections
	private String linkedIn;
	private String facebook;
	private String twitter;
	private String whatsapp;
	private String instagram;
	
	@JsonBackReference
	@OneToOne
	@JoinColumn(name = "user")
	private Users users;


}
