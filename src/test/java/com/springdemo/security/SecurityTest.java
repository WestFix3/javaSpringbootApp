package com.springdemo.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springdemo.dto.LoginRequestDTO;
import com.springdemo.dto.UserRequestDTO;
import com.springdemo.dto.UserResponseDTO;
import com.springdemo.model.UserModel;
import com.springdemo.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {
	@MockBean
	private UserService userService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	void protectedEndpoint_shouldReturnUnauthorized_whenUserIsNotAuthenticated() throws Exception {
		mockMvc.perform(get("/users/profile"))
		.andExpect(status().isUnauthorized())
		.andExpect(content().string("Bejelentkezés szükséges!"));
	}
	
	@Test
	@WithMockUser(username="user", roles="USER")
	void adminEndpoint_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
		mockMvc.perform(delete("/users/admin/delete/1"))
		.andExpect(status().isForbidden())
		.andExpect(content().string("Nincs jogosultságod ehhez a művelethez!"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "ADMIN")
	void adminEndpoint_shouldAllowAccess_whenUserIsAdmin() throws Exception {
		mockMvc.perform(delete("/users/admin/delete/1"))
		.andExpect(status().isNoContent());
		
		verify(userService).deleteUser(1L);
	}
	
	@Test
	void loginEndpoint_shouldBeAccessible_withoutAuthentication() throws Exception {
		LoginRequestDTO newUser = new LoginRequestDTO();
		newUser.setUsername("user");	
		newUser.setPassword("user");
	
		when(userService.login(any(LoginRequestDTO.class))).thenReturn("GeneratedToken");
		
		String json = objectMapper.writeValueAsString(newUser);
		
		mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
		
		verify(userService).login(any(LoginRequestDTO.class));
	}
	
	@Test
	void registerEndpoint_shouldBeAccessible_withoutAuthentication() throws Exception {
		UserRequestDTO newUser = new UserRequestDTO();
	    newUser.setEmail("user@user.com");
	    newUser.setUsername("user");
	    newUser.setPassword("user");
	    
	    when(userService.register(any(UserRequestDTO.class))).thenReturn(
	    		new UserResponseDTO(new UserModel("user@user.com", "user", "user", "USER")));
	    
	    String json = objectMapper.writeValueAsString(newUser);
	    
	    mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(json))
	    .andExpect(status().isCreated())
	    .andExpect(jsonPath("$.email").value("user@user.com"))
	    .andExpect(jsonPath("$.username").value("user"))
	    .andExpect(jsonPath("$.role").value("USER"));
	    
	    verify(userService).register(any(UserRequestDTO.class));
	}
}
