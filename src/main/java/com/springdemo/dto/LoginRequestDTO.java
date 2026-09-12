package com.springdemo.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {
	@NotBlank(message = "Felhasználónév nem lehet üres!")
	private String username;
	@NotBlank(message = "Jelszó nem lehet üres!")
	private String password;
	
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
}
