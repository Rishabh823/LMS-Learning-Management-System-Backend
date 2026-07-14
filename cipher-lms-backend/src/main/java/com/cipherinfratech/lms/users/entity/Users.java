package com.cipherinfratech.lms.users.entity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.cipherinfratech.lms.organizations.entities.Organizations;
import com.cipherinfratech.lms.utils.Tracker;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cipherinfratech.lms.utils.Messages;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@ToString(exclude = {"password"})
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email_id"))
public class Users extends Tracker implements UserDetails {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID userId;

	@Column(name = "email_id")
	@NotBlank(message = Messages.emailValidation)
	@Email(message = "Email id is not valid")
	private String emailId;

	@NotNull(message = Messages.nameValidation)
	private String name;

	@NotNull(message = Messages.genderValidation)
	private String gender;

	@Column(columnDefinition = "varchar(10) default 'USER'")
	@Enumerated(EnumType.STRING)
	private Roles role;
	
	@Column(nullable = false, updatable = false)
	@NotBlank(message = "phone no should not be null")
	@Size(min = 10, max = 10, message = "Phone Number should be contains 10 digits")
	private String contactNo;

	private String profilePic;

	@JsonIgnore
	private String profilePicType;

	@Lob
	@JsonIgnore
	@Column(columnDefinition = "LONGBLOB")
	private byte[] profilePicFile;

	@NotNull(message = Messages.passwordValidation)
	private String password;

	private boolean status =true;

	@JsonManagedReference
	@OneToOne(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private UsersProfile usersProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orgId")
	@JsonIgnore
	private Organizations organizations;

	private String instructType;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	public String getUsername() {
		return emailId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return status;
	}

	public boolean getStatus() {
		return status;
	}
}
