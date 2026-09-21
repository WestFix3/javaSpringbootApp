package com.springdemo.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springdemo.dto.LoginRequestDTO;
import com.springdemo.dto.UserRequestDTO;
import com.springdemo.dto.UserUpdateDTO;
import com.springdemo.model.UserModel;
import com.springdemo.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {
	
	@Autowired
	private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void register_shouldCreateUser_whenRequestIsValid() throws Exception {
    	UserRequestDTO request = new UserRequestDTO();
        request.setUsername("user");
        request.setEmail("user@user.com");
        request.setPassword("password");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.email").value("user@user.com"))
        .andExpect(jsonPath("$.role").value("USER"))
        .andExpect(jsonPath("$.id").isNumber());
        
        UserModel savedUser = userRepository.getUserModelByUsername("user").orElseThrow();

        assertEquals("user@user.com", savedUser.getEmail());
        assertEquals("user", savedUser.getUsername());
        assertEquals("USER", savedUser.getRole());
    }
    
    @Test
    void register_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
    	UserRequestDTO request = new UserRequestDTO();
        request.setUsername("");
        request.setEmail("user@user.com");
        request.setPassword("password");
        
        String json = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Felhasználónév nem lehet üres!"));
    }
    
    @Test
    void register_shouldReturnConflict_whenUserAlreadyExists() throws Exception {
    	UserRequestDTO request = new UserRequestDTO();
        request.setUsername("user");
        request.setEmail("user@user.com");
        request.setPassword("password");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isCreated());

        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isConflict());
    }
    
    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.email").value("user@user.com"))
        .andExpect(jsonPath("$.role").value("USER"))
        .andExpect(jsonPath("$.id").isNumber());
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk())
        .andExpect(content().string(not(emptyString())));
    }
    
    @Test
    void login_shouldReturnUnauthorized_whenPasswordIsInvalid() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.email").value("user@user.com"))
        .andExpect(jsonPath("$.role").value("USER"))
        .andExpect(jsonPath("$.id").isNumber());
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("asdwq4124g");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.message").value("Bad credentials"));
    }
    
    @Test
    void login_shouldReturnNotFound_whenUsernameDoesNotExist() throws Exception { 
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("User not found!"));
    }
    
    @Test
    void profile_shouldReturnUser_whenAuthenticated() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        mockMvc.perform(get("/users/profile").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.email").value("user@user.com"))
        .andExpect(jsonPath("$.role").value("USER"))
        .andExpect(jsonPath("$.id").isNumber());
    }
    
    @Test
    void profile_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/users/profile"))
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    void profile_shouldReturnUnauthorized_whenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/users/profile").header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    void deleteSelf_shouldReturnNoContent_whenAuthenticated() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        mockMvc.perform(delete("/users/me").header("Authorization", "Bearer " + token))
        .andExpect(status().isNoContent());
        
        assertTrue(userRepository.getUserModelByUsername("user").isEmpty());
    }
    
    @Test
    void deleteSelf_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
    	mockMvc.perform(delete("/users/me"))
    	.andExpect(status().isUnauthorized());
    }
    
    @Test
    void deleteUser_shouldReturnNoContent_whenAuthenticatedAsAdmin() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        UserRequestDTO registerRequest2 = new UserRequestDTO();
        registerRequest2.setUsername("admin");
        registerRequest2.setEmail("admin@admin.com");
        registerRequest2.setPassword("password");
        
        String registerJson2 = objectMapper.writeValueAsString(registerRequest2);
        
        mockMvc.perform(post("/users/register")
        		.contentType(MediaType.APPLICATION_JSON).content(registerJson2))
        .andExpect(status().isCreated());
        
        UserModel adminUser = userRepository.getUserModelByUsername("admin").orElseThrow();
        adminUser.setRole("ADMIN");
        userRepository.save(adminUser);
        
        LoginRequestDTO loginRequest2 = new LoginRequestDTO();
        loginRequest2.setUsername("admin");
        loginRequest2.setPassword("password");
        
        String loginJson2 = objectMapper.writeValueAsString(loginRequest2);
        
        MvcResult result2 = mockMvc.perform(post("/users/login")
        		.contentType(MediaType.APPLICATION_JSON).content(loginJson2))
        .andExpect(status().isOk()).andReturn();
        
        String token2 = result2.getResponse().getContentAsString();
        
        UserModel simpleUser = userRepository.getUserModelByUsername("user").orElseThrow();
        
        mockMvc.perform(delete("/users/admin/delete/" + simpleUser.getId())
        		.header("Authorization", "Bearer " + token2))
        .andExpect(status().isNoContent());
        
        assertTrue(userRepository.getUserModelByUsername("user").isEmpty());
    }
    
    @Test
    void deleteUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("admin");
        registerRequest.setEmail("admin@admin.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register")
        		.contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        UserModel adminUser = userRepository.getUserModelByUsername("admin").orElseThrow();
        adminUser.setRole("ADMIN");
        userRepository.save(adminUser);
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login")
        		.contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        mockMvc.perform(delete("/users/admin/delete/999999")
        		.header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("User not found!"));
        
        assertTrue(userRepository.findById(adminUser.getId()).isPresent());
        assertTrue(userRepository.findById(999999L).isEmpty());
    }
    
    @Test
    void deleteUser_shouldReturnForbidden_whenAuthenticatedAsUser() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register")
        		.contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login")
        		.contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        mockMvc.perform(delete("/users/admin/delete/2")
        		.header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
    }
    
    @Test
    void deleteUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
    	mockMvc.perform(delete("/users/admin/delete/1"))
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    void updateUser_shouldUpdateUser_whenAuthenticatedAsAdmin() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        UserRequestDTO registerRequest2 = new UserRequestDTO();
        registerRequest2.setUsername("admin");
        registerRequest2.setEmail("admin@admin.com");
        registerRequest2.setPassword("password");
        
        String registerJson2 = objectMapper.writeValueAsString(registerRequest2);
        
        mockMvc.perform(post("/users/register")
        		.contentType(MediaType.APPLICATION_JSON).content(registerJson2))
        .andExpect(status().isCreated());
        
        UserModel adminUser = userRepository.getUserModelByUsername("admin").orElseThrow();
        adminUser.setRole("ADMIN");
        userRepository.save(adminUser);
        
        LoginRequestDTO loginRequest2 = new LoginRequestDTO();
        loginRequest2.setUsername("admin");
        loginRequest2.setPassword("password");
        
        String loginJson2 = objectMapper.writeValueAsString(loginRequest2);
        
        MvcResult result2 = mockMvc.perform(post("/users/login")
        		.contentType(MediaType.APPLICATION_JSON).content(loginJson2))
        .andExpect(status().isOk()).andReturn();
        
        String token2 = result2.getResponse().getContentAsString();
        
        UserModel simpleUser = userRepository.getUserModelByUsername("user").orElseThrow();
        
        UserUpdateDTO updatedUser = new UserUpdateDTO();
        updatedUser.setEmail("user2@user2.com");
        updatedUser.setUsername("user2");
        
        String json = objectMapper.writeValueAsString(updatedUser);
        
        mockMvc.perform(put("/users/admin/update/" + simpleUser.getId())
        		.header("Authorization", "Bearer " + token2)
        		.contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(updatedUser.getEmail()))
        .andExpect(jsonPath("$.username").value(updatedUser.getUsername()));
        
        UserModel user2 = userRepository.getUserModelByUsername("user2").orElseThrow();
        
        assertEquals(updatedUser.getEmail(), user2.getEmail());
        assertEquals(updatedUser.getUsername(), user2.getUsername());
    }
    
    @Test
    void updateUser_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("admin");
        registerRequest.setEmail("admin@admin.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register")
        		.contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        UserModel adminUser = userRepository.getUserModelByUsername("admin").orElseThrow();
        adminUser.setRole("ADMIN");
        userRepository.save(adminUser);
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login")
        		.contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        UserUpdateDTO updatedUser = new UserUpdateDTO();
        updatedUser.setEmail("user2@user2.com");
        updatedUser.setUsername("");
        
        String json = objectMapper.writeValueAsString(updatedUser);
        
        mockMvc.perform(put("/users/admin/update/3")
        		.header("Authorization", "Bearer " + token)
        		.contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Felhasználónév nem lehet üres!"));
    }
    
    @Test
    void updateUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("admin");
        registerRequest.setEmail("admin@admin.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register")
        		.contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        UserModel adminUser = userRepository.getUserModelByUsername("admin").orElseThrow();
        adminUser.setRole("ADMIN");
        userRepository.save(adminUser);
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login")
        		.contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        UserUpdateDTO updatedUser = new UserUpdateDTO();
        updatedUser.setEmail("user2@user2.com");
        updatedUser.setUsername("user2");
        
        String json = objectMapper.writeValueAsString(updatedUser);
        
        mockMvc.perform(put("/users/admin/update/999999")
        		.header("Authorization", "Bearer " + token)
        		.contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("User not found!"));
        
        assertTrue(userRepository.findById(adminUser.getId()).isPresent());
    }
    
    @Test
    void updateUser_shouldReturnForbidden_whenAuthenticatedAsUser() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        UserUpdateDTO updatedUser = new UserUpdateDTO();
        updatedUser.setEmail("user2@user2.com");
        updatedUser.setUsername("user2");
        
        String json = objectMapper.writeValueAsString(updatedUser);
        
        mockMvc.perform(put("/users/admin/update/999999")
        		.header("Authorization", "Bearer " + token)
        		.contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isForbidden());
    }
    
    @Test
    void updateUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
    	UserUpdateDTO updatedUser = new UserUpdateDTO();
        updatedUser.setEmail("user2@user2.com");
        updatedUser.setUsername("user2");
        
        String json = objectMapper.writeValueAsString(updatedUser);
        
    	mockMvc.perform(put("/users/admin/update/1")
        		.contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    void getUser_shouldReturnUser_whenAuthenticatedAsAdmin() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        UserRequestDTO registerRequest2 = new UserRequestDTO();
        registerRequest2.setUsername("admin");
        registerRequest2.setEmail("admin@admin.com");
        registerRequest2.setPassword("password");
        
        String registerJson2 = objectMapper.writeValueAsString(registerRequest2);
        
        mockMvc.perform(post("/users/register")
        		.contentType(MediaType.APPLICATION_JSON).content(registerJson2))
        .andExpect(status().isCreated());
        
        UserModel adminUser = userRepository.getUserModelByUsername("admin").orElseThrow();
        adminUser.setRole("ADMIN");
        userRepository.save(adminUser);
        
        LoginRequestDTO loginRequest2 = new LoginRequestDTO();
        loginRequest2.setUsername("admin");
        loginRequest2.setPassword("password");
        
        String loginJson2 = objectMapper.writeValueAsString(loginRequest2);
        
        MvcResult result2 = mockMvc.perform(post("/users/login")
        		.contentType(MediaType.APPLICATION_JSON).content(loginJson2))
        .andExpect(status().isOk()).andReturn();
        
        String token2 = result2.getResponse().getContentAsString();
        
        UserModel searchUser = userRepository.getUserModelByUsername("user").orElseThrow();
        
        mockMvc.perform(get("/users/admin/search/" + searchUser.getId())
        		.header("Authorization", "Bearer " + token2))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(registerRequest.getEmail()))
        .andExpect(jsonPath("$.username").value(registerRequest.getUsername()))
        .andExpect(jsonPath("$.role").value("USER"));
    }
    
    @Test
    void getUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("admin");
        registerRequest.setEmail("admin@admin.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register")
        		.contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        UserModel adminUser = userRepository.getUserModelByUsername("admin").orElseThrow();
        adminUser.setRole("ADMIN");
        userRepository.save(adminUser);
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login")
        		.contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        mockMvc.perform(get("/users/admin/search/999999")
        		.header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("User not found!"));
    }
    
    @Test
    void getUser_shouldReturnForbidden_whenAuthenticatedAsUser() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        mockMvc.perform(get("/users/admin/search/999999")
        		.header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
    }
    
    @Test
    void getUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
    	mockMvc.perform(get("/users/admin/search/1"))
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    void getAll_shouldReturnUsers_whenAuthenticatedAsAdmin() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        UserRequestDTO registerRequest2 = new UserRequestDTO();
        registerRequest2.setUsername("admin");
        registerRequest2.setEmail("admin@admin.com");
        registerRequest2.setPassword("password");
        
        String registerJson2 = objectMapper.writeValueAsString(registerRequest2);
        
        mockMvc.perform(post("/users/register")
        		.contentType(MediaType.APPLICATION_JSON).content(registerJson2))
        .andExpect(status().isCreated());
        
        UserModel adminUser = userRepository.getUserModelByUsername("admin").orElseThrow();
        adminUser.setRole("ADMIN");
        userRepository.save(adminUser);
        
        LoginRequestDTO loginRequest2 = new LoginRequestDTO();
        loginRequest2.setUsername("admin");
        loginRequest2.setPassword("password");
        
        String loginJson2 = objectMapper.writeValueAsString(loginRequest2);
        
        MvcResult result2 = mockMvc.perform(post("/users/login")
        		.contentType(MediaType.APPLICATION_JSON).content(loginJson2))
        .andExpect(status().isOk()).andReturn();
        
        String token2 = result2.getResponse().getContentAsString();
        
        mockMvc.perform(get("/users/admin/users").header("Authorization", "Bearer " + token2)
        		.param("page", "0").param("size", "2").param("sort", "id,asc"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.length()").value(2))
		.andExpect(jsonPath("$.totalElements").value(2))
		.andExpect(jsonPath("$.totalPages").value(1))
		.andExpect(jsonPath("$.number").value(0))
		.andExpect(jsonPath("$.size").value(2));
    }
    
    @Test
    void getAll_shouldRespectPagination_whenAuthenticatedAsAdmin() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        UserRequestDTO registerRequest3 = new UserRequestDTO();
        registerRequest3.setUsername("user3");
        registerRequest3.setEmail("user3@user3.com");
        registerRequest3.setPassword("password");
        
        String registerJson3 = objectMapper.writeValueAsString(registerRequest3);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson3))
        .andExpect(status().isCreated());
        
        UserRequestDTO registerRequest2 = new UserRequestDTO();
        registerRequest2.setUsername("admin");
        registerRequest2.setEmail("admin@admin.com");
        registerRequest2.setPassword("password");
        
        String registerJson2 = objectMapper.writeValueAsString(registerRequest2);
        
        mockMvc.perform(post("/users/register")
        		.contentType(MediaType.APPLICATION_JSON).content(registerJson2))
        .andExpect(status().isCreated());
        
        UserModel adminUser = userRepository.getUserModelByUsername("admin").orElseThrow();
        adminUser.setRole("ADMIN");
        userRepository.save(adminUser);
        
        LoginRequestDTO loginRequest2 = new LoginRequestDTO();
        loginRequest2.setUsername("admin");
        loginRequest2.setPassword("password");
        
        String loginJson2 = objectMapper.writeValueAsString(loginRequest2);
        
        MvcResult result2 = mockMvc.perform(post("/users/login")
        		.contentType(MediaType.APPLICATION_JSON).content(loginJson2))
        .andExpect(status().isOk()).andReturn();
        
        String token2 = result2.getResponse().getContentAsString();
        
        mockMvc.perform(get("/users/admin/users").header("Authorization", "Bearer " + token2)
        		.param("page", "1").param("size", "2").param("sort", "id,asc"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.length()").value(1))
		.andExpect(jsonPath("$.totalElements").value(3))
		.andExpect(jsonPath("$.totalPages").value(2))
		.andExpect(jsonPath("$.number").value(1))
		.andExpect(jsonPath("$.size").value(2))
		.andExpect(jsonPath("$.content[0].username").value("admin"))
		.andExpect(jsonPath("$.content[0].email").value("admin@admin.com"));
    }
    
    @Test
    void getAll_shouldReturnForbidden_whenAuthenticatedAsUser() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        mockMvc.perform(get("/users/admin/users").header("Authorization", "Bearer " + token)
        		.param("page", "0").param("size", "2").param("sort", "id,asc"))
		.andExpect(status().isForbidden());
    }
    
    @Test
    void getAll_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
    	mockMvc.perform(get("/users/admin/users")
        		.param("page", "0").param("size", "2").param("sort", "id,asc"))
		.andExpect(status().isUnauthorized());
    }
    
    @Test
    void logout_shouldReturnOk_whenAuthenticated() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        mockMvc.perform(post("/users/logout").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(content().string("User logged out successfully: user!"));
    }
    
    @Test
    void logout_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
    	mockMvc.perform(post("/users/logout"))
        .andExpect(status().isUnauthorized());
    }
}