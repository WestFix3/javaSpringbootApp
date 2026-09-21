package com.springdemo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import com.springdemo.dto.LoginRequestDTO;
import com.springdemo.dto.UserRequestDTO;
import com.springdemo.dto.UserResponseDTO;
import com.springdemo.dto.UserUpdateDTO;
import com.springdemo.exceptions.Exceptions.UserAlreadyExistsException;
import com.springdemo.exceptions.Exceptions.UserNotFoundException;
import com.springdemo.model.UserModel;
import com.springdemo.service.CustomUserDetailsService;
import com.springdemo.service.JwtService;
import com.springdemo.service.UserService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private JwtService jwtService;
	
	@MockBean
	private CustomUserDetailsService customUserDetailsService;
	
	@MockBean
	private Authentication authentication;
	
	@MockBean
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	void register_shouldReturnCreated_whenRegistrationSucceeds() throws Exception {
	    UserRequestDTO requestUser = new UserRequestDTO();
	    requestUser.setEmail("user@user.com");
	    requestUser.setUsername("user");
	    requestUser.setPassword("user");
	    UserResponseDTO responseUser = new UserResponseDTO(new UserModel("user@user.com", "user", "user", "USER"));

	    when(userService.register(any(UserRequestDTO.class))).thenReturn(responseUser);
	    String json = objectMapper.writeValueAsString(requestUser);

	    mockMvc.perform(
	            post("/users/register")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(json)
	    )
	    .andExpect(status().isCreated())
	    .andExpect(jsonPath("$.email").value("user@user.com"))
	    .andExpect(jsonPath("$.username").value("user"))
	    .andExpect(jsonPath("$.role").value("USER"));

	    verify(userService).register(any(UserRequestDTO.class));
	}
	
	@Test
	void register_shouldReturnConflict_whenUserAlreadyExists() throws Exception {
		UserRequestDTO requestUser = new UserRequestDTO();
	    requestUser.setEmail("user@user.com");
	    requestUser.setUsername("user");
	    requestUser.setPassword("user");
	    
	    when(userService.register(any(UserRequestDTO.class))).thenThrow(new UserAlreadyExistsException("Username or email already exists!"));
	    String json = objectMapper.writeValueAsString(requestUser);
	    
	    mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(json))
	    .andExpect(status().isConflict())
	    .andExpect(jsonPath("$.message").value("Username or email already exists!"));
	    
	    verify(userService).register(any(UserRequestDTO.class));
	}
	
	@Test
	void register_shouldReturnBadRequest_whenEmailIsEmpty() throws Exception {
		UserRequestDTO requestUser = new UserRequestDTO();
	    requestUser.setEmail("");
	    requestUser.setUsername("user");
	    requestUser.setPassword("user");
	    
	    String json = objectMapper.writeValueAsString(requestUser);
	    
	    mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(json))
	    .andExpect(status().isBadRequest())
	    .andExpect(jsonPath("$.status").value(400))
	    .andExpect(jsonPath("$.message").value("Email nem lehet üres!"));
	    
	    verify(userService, never()).register(any(UserRequestDTO.class));
	}
	
	@Test
	void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {
		LoginRequestDTO newUser = new LoginRequestDTO();
		newUser.setUsername("user");
		newUser.setPassword("user");
	
		when(userService.login(any(LoginRequestDTO.class))).thenReturn("GeneratedToken");
		
		String json = objectMapper.writeValueAsString(newUser);
		
		mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk())
		.andExpect(content().string("GeneratedToken"));
		
		verify(userService).login(any(LoginRequestDTO.class));
	}
	
	@Test
	void login_shouldReturnUnauthorized_whenCredentialsAreInvalid() throws Exception {
		LoginRequestDTO newUser = new LoginRequestDTO();
		newUser.setUsername("user");
		newUser.setPassword("user");
		
		when(userService.login(any(LoginRequestDTO.class))).thenThrow(new BadCredentialsException("Invalid username or password"));
		
		String json = objectMapper.writeValueAsString(newUser);
		
		mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$.status").value(401))
		.andExpect(jsonPath("$.message").value("Invalid username or password"));
		
		verify(userService).login(any(LoginRequestDTO.class));
	}
	
	@Test
	void login_shouldReturnBadRequest_whenPasswordIsEmpty() throws Exception {
		LoginRequestDTO newUser = new LoginRequestDTO();
		newUser.setUsername("user");
		newUser.setPassword("");
		
		String json = objectMapper.writeValueAsString(newUser);
		
		mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.message").value("Jelszó nem lehet üres!"));
		
		verify(userService, never()).login(any(LoginRequestDTO.class));
	}
	
	@Test
	void logout_shouldReturnMessage_whenUserIsAuthenticated() throws Exception {
		when(userService.logout(authentication)).thenReturn("User logged out successfully: user");
		
		mockMvc.perform(post("/users/logout").principal(authentication))
		.andExpect(status().isOk())
		.andExpect(content().string("User logged out successfully: user!"));
		
		verify(userService).logout(authentication);
	}
	
	@Test
	void profileSelf_shouldReturnUser_whenUserExists() throws Exception {
		UserResponseDTO responseUser = new UserResponseDTO(new UserModel("user@user.com", "user", "user", "USER"));
		when(userService.profileSelf(authentication)).thenReturn(responseUser);
		
		mockMvc.perform(get("/users/profile").principal(authentication))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.email").value("user@user.com"))
	    .andExpect(jsonPath("$.username").value("user"))
	    .andExpect(jsonPath("$.role").value("USER"));
		
		verify(userService).profileSelf(authentication);
	}
	
	@Test
	void profileSelf_shouldReturnNotFound_whenUserNotFound() throws Exception {
		when(userService.profileSelf(authentication)).thenThrow(new UserNotFoundException("User not found!"));
		
		mockMvc.perform(get("/users/profile").principal(authentication))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("User not found!"));
		
		verify(userService).profileSelf(authentication);
	}
	
	@Test
	void deleteSelf_shouldReturnNoContent_whenUserExists() throws Exception {
		mockMvc.perform(delete("/users/me").principal(authentication))
		.andExpect(status().isNoContent());
		
		verify(userService).deleteSelf(authentication);
	}
	
	@Test
	void deleteSelf_shouldReturnNotFound_whenUserNotFound() throws Exception {
		doThrow(new UserNotFoundException("User not found!"))
        .when(userService)
        .deleteSelf(authentication);
		
		mockMvc.perform(delete("/users/me").principal(authentication))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("User not found!"));
		
		verify(userService).deleteSelf(authentication);
	}
	
	//ADMIN FUNKCIÓK TESZTELÉSE
	
	@Test
	void deleteUser_shouldReturnNoContent_whenUserExists() throws Exception {
		mockMvc.perform(delete("/users/admin/delete/1"))
		.andExpect(status().isNoContent());
		
		verify(userService).deleteUser(1L);
	}
	
	@Test
	void deleteUser_shouldReturnNotFound_whenUserNotFound() throws Exception {
		doThrow(new UserNotFoundException("User not found!"))
		.when(userService)
		.deleteUser(1L);
		
		mockMvc.perform(delete("/users/admin/delete/1"))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("User not found!"));
		
		verify(userService).deleteUser(1L);
	}
	
	@Test
	void updateUser_shouldReturnUser_whenUserExists() throws Exception {
		UserUpdateDTO updateUser = new UserUpdateDTO();
		updateUser.setEmail("user@user.com");
		updateUser.setUsername("user");
		UserResponseDTO responseUser = new UserResponseDTO(new UserModel("user@user.com", "user", "user", "USER"));
		
		when(userService.updateUser(eq(1L), any(UserUpdateDTO.class))).thenReturn(responseUser);
		
		String json = objectMapper.writeValueAsString(updateUser);
		
		mockMvc.perform(put("/users/admin/update/1").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.email").value("user@user.com"))
	    .andExpect(jsonPath("$.username").value("user"))
	    .andExpect(jsonPath("$.role").value("USER"));
		
		verify(userService).updateUser(eq(1L), any(UserUpdateDTO.class));
	}
	
	@Test
	void updateUser_shouldReturnNotFound_whenUserNotFound() throws Exception {
		UserUpdateDTO updateUser = new UserUpdateDTO();
		updateUser.setEmail("user@user.com");
		updateUser.setUsername("user");
		when(userService.updateUser(eq(1L), any(UserUpdateDTO.class))).thenThrow(new UserNotFoundException("User not found!"));
		
		String json = objectMapper.writeValueAsString(updateUser);
		
		mockMvc.perform(put("/users/admin/update/1").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("User not found!"));
		
		verify(userService).updateUser(eq(1L), any(UserUpdateDTO.class));
	}
	
	@Test
	void updateUser_shouldReturnBadRequest_whenEmailIsEmpty() throws Exception {
		UserUpdateDTO updateUser = new UserUpdateDTO();
		updateUser.setEmail("");
		updateUser.setUsername("user");
		
		String json = objectMapper.writeValueAsString(updateUser);
		
		mockMvc.perform(put("/users/admin/update/1").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.message").value("Email nem lehet üres!"));
		
		verify(userService, never()).updateUser(eq(1L), any(UserUpdateDTO.class));
	}
	
	@Test
	void getUser_shouldReturnUser_whenUserExists() throws Exception {
		UserResponseDTO responseUser = new UserResponseDTO(new UserModel("user@user.com", "user", "user", "USER"));
		
		when(userService.getUser(1L)).thenReturn(responseUser);
		
		mockMvc.perform(get("/users/admin/search/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.email").value("user@user.com"))
	    .andExpect(jsonPath("$.username").value("user"))
	    .andExpect(jsonPath("$.role").value("USER"));
		
		verify(userService).getUser(1L);
	}
	
	@Test
	void getUser_shouldReturnNotFound_whenUserNotFound() throws Exception {
		when(userService.getUser(1L)).thenThrow(new UserNotFoundException("User not found!"));
		
		mockMvc.perform(get("/users/admin/search/1"))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("User not found!"));
		
		verify(userService).getUser(1L);
	}
	
	@Test
	void getAll_shouldReturnUsers() throws Exception {
		UserResponseDTO responseUser = new UserResponseDTO(new UserModel("user@user.com", "user", "user", "USER"));
		
		Page<UserResponseDTO> responsePage = new PageImpl<>(List.of(responseUser));
		
		when(userService.getAll(any(Pageable.class))).thenReturn(responsePage);
		
		mockMvc.perform(get("/users/admin/users?page=0&size=10"))
		.andExpect(status().isOk())
	    .andExpect(jsonPath("$.content[0].email").value("user@user.com"))
	    .andExpect(jsonPath("$.content[0].username").value("user"))
	    .andExpect(jsonPath("$.content[0].role").value("USER"))
	    .andExpect(jsonPath("$.totalElements").value(1));
		
		verify(userService).getAll(any(Pageable.class));
	}
	
	@Test
	void getAll_shouldReturnEmptyPage_whenNoUsersExist() throws Exception {
		Page<UserResponseDTO> responsePage = new PageImpl<>(List.of());
		
		when(userService.getAll(any(Pageable.class))).thenReturn(responsePage);
		
		mockMvc.perform(get("/users/admin/users?page=0&size=10"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content").isEmpty())
		.andExpect(jsonPath("$.totalElements").value(0));
		
		verify(userService).getAll(any(Pageable.class));
	}
}
