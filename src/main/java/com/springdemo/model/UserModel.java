package com.springdemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class UserModel{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank(message = "Email nem lehet üres!")
    @Email(message = "Érvénytelen email formátum!")
	@Column(unique = true, nullable = false, name = "email")
	private String email;
	@NotBlank(message = "Felhasználónév nem lehet üres!")
	@Column(unique = true, nullable = false, name = "username")
	private String username;
	@NotBlank(message = "Jelszó nem lehet üres!")
	@Column(nullable = false, name = "password")
	private String password;
	private String role;
	
	public UserModel() {}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}