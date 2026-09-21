package com.springdemo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.springdemo.dto.LoginRequestDTO;
import com.springdemo.dto.UserRequestDTO;
import com.springdemo.dto.UserResponseDTO;
import com.springdemo.dto.UserUpdateDTO;
import org.springframework.data.domain.PageRequest;
import com.springdemo.exceptions.Exceptions.UserAlreadyExistsException;
import com.springdemo.exceptions.Exceptions.UserNotFoundException;
import com.springdemo.model.UserModel;
import com.springdemo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private JwtService jwtService;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private Authentication authentication;
	@InjectMocks
	private UserService userService;
	
	//Elnevezés: metódus_shouldEredmény_whenFeltétel
	
	@Test
	void register_shouldCreateUserWithCorrectData() {

	    UserRequestDTO user = new UserRequestDTO();

	    user.setEmail("user@user.com");
	    user.setUsername("user");
	    user.setPassword("user");

	    when(userRepository.getUserModelByEmail("user@user.com"))
	        .thenReturn(Optional.empty());

	    when(userRepository.getUserModelByUsername("user"))
	        .thenReturn(Optional.empty());

	    when(passwordEncoder.encode("user"))
	        .thenReturn("hashedPassword");

	    UserResponseDTO newUser = userService.register(user);

	    assertEquals("user@user.com", newUser.getEmail());
	    assertEquals("user", newUser.getUsername());
	    assertEquals("USER", newUser.getRole());
	    
	    verify(userRepository).save(any(UserModel.class));
	    verify(passwordEncoder).encode("user");
	}
	
	@Test
	void register_shouldThrowException_whenUsernameAlreadyExists() {
		UserRequestDTO user = new UserRequestDTO();
		user.setEmail("user@user.com");
		user.setUsername("user");
		user.setPassword("user");
		
		UserModel existingUser = new UserModel();
		
		when(userRepository.getUserModelByUsername("user")).thenReturn(Optional.of(existingUser));
		
		UserAlreadyExistsException exception = assertThrows(
			    UserAlreadyExistsException.class,
			    () -> userService.register(user)
			);

			assertEquals(
			    "Username or email already exists!",
			    exception.getMessage()
			);
		
		verify(userRepository, never()).save(any(UserModel.class));
	}
	
	@Test
	void register_shouldThrowException_whenEmailAlreadyExists() {
		UserRequestDTO user = new UserRequestDTO();
		user.setEmail("user@user.com");
		user.setUsername("user");
		user.setPassword("user");
		
		UserModel existingUser = new UserModel();
		
		when(userRepository.getUserModelByEmail("user@user.com")).thenReturn(Optional.of(existingUser));
		
		UserAlreadyExistsException exception = assertThrows(
				UserAlreadyExistsException.class, 
				() -> userService.register(user));
		
		assertEquals("Username or email already exists!", exception.getMessage());
		verify(userRepository, never()).save(any(UserModel.class));
	}
	
	@Test
	void login_shouldReturnToken_whenCredentialsAreValid() {
		LoginRequestDTO user = new LoginRequestDTO();
		user.setUsername("user");
		user.setPassword("user");
		
		when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user", "user")))
		.thenReturn(authentication);
		when(authentication.getName()).thenReturn("user");
		when(jwtService.generateToken(authentication.getName())).thenReturn("token");
		
		assertEquals("token", userService.login(user));
		
		verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("user", "user"));
		verify(jwtService).generateToken("user");
	}
	
	@Test
	void login_shouldThrowException_whenAuthenticationFails() {
		LoginRequestDTO user = new LoginRequestDTO();
		user.setUsername("user");
		user.setPassword("user");
		
		when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user", "user")))
		.thenThrow(new BadCredentialsException("Invalid username or password"));
	
		BadCredentialsException ex = assertThrows(BadCredentialsException.class, () -> userService.login(user));
		
		assertEquals("Invalid username or password", ex.getMessage());
		
		verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("user", "user"));
		verify(jwtService, never()).generateToken("user");
	}
	
	@Test
	void profileSelf_shouldReturnUser_whenUsernameExists() {
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		user.setId(1L);
		
		when(authentication.getName()).thenReturn("user");
		when(userRepository.getUserModelByUsername("user")).thenReturn(Optional.of(user));
		
		UserResponseDTO response = userService.profileSelf(authentication);
		
		assertEquals(1L, response.getId());
		assertEquals("user@user.com", response.getEmail());
		assertEquals("user", response.getUsername());
		assertEquals("USER", response.getRole());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(user.getUsername());
	}
	
	@Test
	void profileSelf_shouldThrowException_whenUserNotFound() {
		when(authentication.getName()).thenReturn("user");
		when(userRepository.getUserModelByUsername("user")).thenReturn(Optional.empty());
			
		assertThrows(UserNotFoundException.class, () -> userService.profileSelf(authentication));
		
		verify(userRepository).getUserModelByUsername("user");
	}
	
	@Test
	void deleteSelf_shouldDeleteUser_whenUserExists() {
		UserModel user = new UserModel();
		when(authentication.getName()).thenReturn("user");
		when(userRepository.getUserModelByUsername("user")).thenReturn(Optional.of(user));
		
		userService.deleteSelf(authentication);
		
		verify(userRepository).delete(user);
		verify(userRepository).getUserModelByUsername("user");
	}
	
	@Test
	void deleteSelf_shouldThrowException_whenUserNotFound() {
		when(authentication.getName()).thenReturn("user");
		when(userRepository.getUserModelByUsername("user")).thenReturn(Optional.empty());
		
		UserNotFoundException ex = assertThrows(UserNotFoundException.class,
				() -> userService.deleteSelf(authentication));
		
		assertEquals("User not found!", ex.getMessage());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername("user");
		verify(userRepository, never()).delete(any(UserModel.class));
	}
	
	@Test
	void getUser_shouldReturnUser_whenUserExists(){
		UserModel Guser = new UserModel("user@user.com", "user", "user", "USER");
		Guser.setId(1L);
		UserResponseDTO responseUser = new UserResponseDTO(Guser);
		when(userRepository.findById(1L)).thenReturn(Optional.of(Guser));
		
		UserResponseDTO user = userService.getUser(1L);
		
		assertEquals(responseUser.getId(), user.getId());
		assertEquals(responseUser.getEmail(), user.getEmail());
		assertEquals(responseUser.getUsername(), user.getUsername());
		assertEquals(responseUser.getRole(), user.getRole());
		
		verify(userRepository).findById(1L);
	}
	
	@Test
	void getUser_shouldThrowException_whenUserNotFound() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());
		
		UserNotFoundException ex = assertThrows(UserNotFoundException.class, 
				() -> userService.getUser(1L));
		
		assertEquals("User not found!", ex.getMessage());
		
		verify(userRepository).findById(1L);
	}
	
	@Test
	void updateUser_shouldUpdateUser_whenUserExists() {
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		UserUpdateDTO updateUser = new UserUpdateDTO();
		updateUser.setEmail("user2@user.com");
		updateUser.setUsername("user2");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		
		UserResponseDTO responseUser = userService.updateUser(1L, updateUser);
		
		assertEquals(updateUser.getEmail(), responseUser.getEmail());
		assertEquals(updateUser.getUsername(), responseUser.getUsername());
		
		verify(userRepository).findById(1L);
		verify(userRepository).save(user);
	}
	
	@Test
	void updateUser_shouldThrowException_whenUserNotFound() {
		UserUpdateDTO updateUser = new UserUpdateDTO();
		updateUser.setEmail("user2@user.com");
		updateUser.setUsername("user2");
		
		when(userRepository.findById(1L)).thenReturn(Optional.empty());
		
		UserNotFoundException ex = assertThrows(UserNotFoundException.class, 
				() -> userService.updateUser(1L, updateUser));
		
		assertEquals("User not found!", ex.getMessage());
		
		verify(userRepository).findById(1L);
		verify(userRepository, never()).save(any(UserModel.class));
	}
	
	@Test
	void deleteUser_shouldDeleteUser_whenUserExists() {
		UserModel user = new UserModel();
		
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		
		userService.deleteUser(1L);
		
		verify(userRepository).findById(1L);
		verify(userRepository).delete(user);
	}
	
	@Test
	void deleteUser_shouldThrowException_whenUserNotFound() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());
		UserNotFoundException ex = assertThrows(UserNotFoundException.class, 
				() -> userService.deleteUser(1L));
		
		assertEquals("User not found!", ex.getMessage());
		
		verify(userRepository).findById(1L);
		verify(userRepository, never()).delete(any(UserModel.class));
	}
	
	@Test
	void getAll_shouldReturnUsers() {
		Pageable pageable = PageRequest.of(0, 10);
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
	    user.setId(1L);
	    
	    Page<UserModel> users = new PageImpl<>(List.of(user));
		when(userRepository.findAll(pageable)).thenReturn(users);
		Page<UserResponseDTO> response = userService.getAll(pageable);
		
		assertEquals(1, response.getTotalElements());
	    assertEquals("user", response.getContent().get(0).getUsername());

	    verify(userRepository).findAll(pageable);
	}
	
	@Test
	void getAll_shouldReturnEmptyPage_whenNoUsersExist() {
		Pageable pageable = PageRequest.of(0, 10);
		
		when(userRepository.findAll(pageable)).thenReturn(Page.empty());
		
		Page<UserResponseDTO> response = userService.getAll(pageable);
		
		assertEquals(0, response.getTotalElements());
		assertEquals(true, response.getContent().isEmpty());
		
		verify(userRepository).findAll(pageable);
	}
	
	@Test
	void logout_shouldReturnMessage_whenUserIsAuthenticated() {
		when(authentication.getName()).thenReturn("user");
		String logoutMessage = userService.logout(authentication);
		assertEquals("User logged out successfully: user", logoutMessage);
		
		verify(authentication).getName();
	}
}
