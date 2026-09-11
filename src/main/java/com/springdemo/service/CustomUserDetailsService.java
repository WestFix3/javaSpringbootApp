package com.springdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import com.springdemo.model.UserModel;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.springdemo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserModel user = userRepository.getUserModelByUsername(username).orElse(null); //Később kell HIBA KEZELÉS
		
		return User.builder().username(user.getUsername())
				.password(user.getPassword())
				.roles(user.getRole())
				.build();
	}
	
	public UserRepository getUserRepository() {
		return userRepository;
	}

	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
}
