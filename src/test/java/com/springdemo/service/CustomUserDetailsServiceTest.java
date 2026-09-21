package com.springdemo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.springdemo.exceptions.Exceptions.UserNotFoundException;
import com.springdemo.model.UserModel;
import com.springdemo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;
	
	@Test
	void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
		String username = "user";
		UserModel userModel = new UserModel("user@user.com", "user", "user", "USER");
		when(userRepository.getUserModelByUsername(username)).thenReturn(
				Optional.of(userModel));
		
		UserDetails user = customUserDetailsService.loadUserByUsername(username);
		
		assertEquals(userModel.getUsername(), user.getUsername());
		assertEquals(userModel.getPassword(), user.getPassword());
		String authority = user.getAuthorities().iterator().next().getAuthority();
		assertEquals("ROLE_" + userModel.getRole(), authority);
	}
	
	@Test
	void loadUserByUsername_shouldThrowException_whenUserNotFound() {
		String username = "user";

	    when(userRepository.getUserModelByUsername(username))
	            .thenReturn(Optional.empty());

	    UserNotFoundException ex = assertThrows(
	            UserNotFoundException.class,
	            () -> customUserDetailsService.loadUserByUsername(username)
	    );

	    assertEquals("User not found!", ex.getMessage());
	}
}
