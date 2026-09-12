package com.springdemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserUpdateDTO {
	@NotBlank(message = "Email nem lehet üres!")
    @Email(message = "Érvénytelen email formátum!")
	private String email;
	@NotBlank(message = "Felhasználónév nem lehet üres!")
	private String username;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
}
