package com.springdemo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springdemo.dto.LoginRequestDTO;
import com.springdemo.dto.TaskRequestDTO;
import com.springdemo.dto.UserRequestDTO;
import com.springdemo.model.TaskModel;
import com.springdemo.model.UserModel;
import com.springdemo.repository.TaskRepository;
import com.springdemo.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void cleanDatabase() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }
    
    @Test
    void createTask_shouldCreateTask_whenRequestIsValid() throws Exception {
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
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isCreated())
	    .andExpect(jsonPath("$.title").value("Feladat"))
	    .andExpect(jsonPath("$.description").value("Leiras"))
	    .andExpect(jsonPath("$.priority").value("Fontos"));
	    
	    UserModel user = userRepository.getUserModelByUsername("user").orElseThrow();
	    List<TaskModel> tasks = taskRepository.findByUser(user);
	    
	    assertEquals(1, tasks.size());
	    assertEquals("Feladat", tasks.get(0).getTitle());
	    assertEquals("Leiras", tasks.get(0).getDescription());
	    assertEquals("Fontos", tasks.get(0).getPriority());
    }
    
    @Test
    void createTask_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
    	TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isUnauthorized());
    }
    
    @Test
    void createTask_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
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
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isBadRequest())
	    .andExpect(jsonPath("$.status").value(400))
	    .andExpect(jsonPath("$.message").value("A cím nem lehet üres!"));
	    
	    assertEquals(0, taskRepository.count());
    }
    
    @Test
    void getByUser_shouldReturnTasks_whenAuthenticated() throws Exception {
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
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isCreated());
	    
	    mockMvc.perform(get("/tasks/all").header("Authorization", "Bearer " + token))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.length()").value(1))
	    .andExpect(jsonPath("$[0].title").value("Feladat"))
	    .andExpect(jsonPath("$[0].description").value("Leiras"))
	    .andExpect(jsonPath("$[0].priority").value("Fontos"));
    }
    
    @Test
    void getByUser_shouldReturnEmptyList_whenUserHasNoTasks() throws Exception {
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
        
        mockMvc.perform(get("/tasks/all").header("Authorization", "Bearer " + token))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.length()").value(0));
    }
    
    @Test
    void getByUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
    	mockMvc.perform(get("/tasks/all"))
	    .andExpect(status().isUnauthorized());
    }
    
    @Test
    void updateTask_shouldUpdateTask_whenAuthenticated() throws Exception {
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
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isCreated());
	    
	    UserModel user = userRepository.getUserModelByUsername("user").orElseThrow();
	    TaskModel task = taskRepository.findByUser(user).get(0);
	    
	    request.setTitle("Feladat2");
	    request.setDescription("Leiras2");
	    request.setPriority("Fontos2");
	    request.setDueDate(new Date());
	    
	    requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(put("/tasks/" + task.getId()).header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.title").value("Feladat2"))
	    .andExpect(jsonPath("$.description").value("Leiras2"))
	    .andExpect(jsonPath("$.priority").value("Fontos2"));
	    
	    TaskModel updatedTask = taskRepository.findById(task.getId()).orElseThrow();
	    
	    assertEquals(1, taskRepository.findByUser(user).size());
	    assertEquals("Feladat2", updatedTask.getTitle());
	    assertEquals("Leiras2", updatedTask.getDescription());
	    assertEquals("Fontos2", updatedTask.getPriority());
    }
    
    @Test
    void updateTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
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
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(put("/tasks/1").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isNotFound())
	    .andExpect(jsonPath("$.status").value(404))
	    .andExpect(jsonPath("$.message").value("Task not found!"));
    }
    
    @Test
    void updateTask_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
    	TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
    	mockMvc.perform(put("/tasks/1")
	    .contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isUnauthorized());
    }
    
    @Test
    void updateTask_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
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
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isCreated());
	    
	    UserModel user = userRepository.getUserModelByUsername("user").orElseThrow();
	    TaskModel task = taskRepository.findByUser(user).get(0);
	    
	    request.setTitle("Feladat2");
	    request.setDescription("");
	    request.setPriority("Fontos2");
	    request.setDueDate(new Date());
	    
	    requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(put("/tasks/" + task.getId()).header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isBadRequest())
	    .andExpect(jsonPath("$.status").value(400))
	    .andExpect(jsonPath("$.message").value("A leírás nem lehet üres!"));
	    
	    TaskModel updatedTask = taskRepository.findById(task.getId()).orElseThrow();
	    
	    assertEquals("Feladat", updatedTask.getTitle());
	    assertEquals("Leiras", updatedTask.getDescription());
	    assertEquals("Fontos", updatedTask.getPriority());
    }
    
    @Test
    void updateTask_shouldReturnNotFound_whenTaskBelongsToAnotherUser() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user1");
        registerRequest.setEmail("user1@user1.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user1");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isCreated());
	    
	    UserRequestDTO registerRequest2 = new UserRequestDTO();
        registerRequest2.setUsername("user2");
        registerRequest2.setEmail("user2@user2.com");
        registerRequest2.setPassword("password");
        
        String registerJson2 = objectMapper.writeValueAsString(registerRequest2);
        
        mockMvc.perform(post("/users/register")
        .contentType(MediaType.APPLICATION_JSON).content(registerJson2))
        .andExpect(status().isCreated());
        
        LoginRequestDTO loginRequest2 = new LoginRequestDTO();
        loginRequest2.setUsername("user2");
        loginRequest2.setPassword("password");
        
        String loginJson2 = objectMapper.writeValueAsString(loginRequest2);
        
        MvcResult result2 = mockMvc.perform(post("/users/login")
        .contentType(MediaType.APPLICATION_JSON).content(loginJson2))
        .andExpect(status().isOk()).andReturn();
        
        String token2 = result2.getResponse().getContentAsString();
	    
	    UserModel user = userRepository.getUserModelByUsername("user1").orElseThrow();
	    TaskModel task = taskRepository.findByUser(user).get(0);
	    
	    request.setTitle("Feladat2");
	    request.setDescription("Leiras2");
	    request.setPriority("Fontos2");
	    request.setDueDate(new Date());
	    
	    requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(put("/tasks/" + task.getId()).header("Authorization", "Bearer " + token2)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isNotFound())
	    .andExpect(jsonPath("$.status").value(404))
	    .andExpect(jsonPath("$.message").value("Task not found!"));
	    
	    TaskModel unchangedTask = taskRepository.findById(task.getId()).orElseThrow();

	    assertEquals("Feladat", unchangedTask.getTitle());
	    assertEquals("Leiras", unchangedTask.getDescription());
	    assertEquals("Fontos", unchangedTask.getPriority());
    }
    
    @Test
    void deleteTask_shouldDeleteTask_whenAuthenticated() throws Exception {
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
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isCreated());
	    
	    UserModel user = userRepository.getUserModelByUsername("user").orElseThrow();
	    TaskModel task = taskRepository.findByUser(user).get(0);
	    
	    mockMvc.perform(delete("/tasks/" + task.getId()).header("Authorization", "Bearer " + token))
	    .andExpect(status().isNoContent());
	    
	    Optional<TaskModel> isDeleted = taskRepository.findById(task.getId());
	   
	   assertTrue(isDeleted.isEmpty());
    }
    
    @Test
    void deleteTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
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
        
	    mockMvc.perform(delete("/tasks/1").header("Authorization", "Bearer " + token))
	    .andExpect(status().isNotFound())
	    .andExpect(jsonPath("$.status").value(404))
	    .andExpect(jsonPath("$.message").value("Task not found!"));
    }
    
    @Test
    void deleteTask_shouldReturnNotFound_whenTaskBelongsToAnotherUser() throws Exception {
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
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isCreated());
	    
	    UserRequestDTO registerRequest2 = new UserRequestDTO();
        registerRequest2.setUsername("user2");
        registerRequest2.setEmail("user2@user2.com");
        registerRequest2.setPassword("password");
        
        String registerJson2 = objectMapper.writeValueAsString(registerRequest2);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON)
        .content(registerJson2))
        .andExpect(status().isCreated());
        
        LoginRequestDTO loginRequest2 = new LoginRequestDTO();
        loginRequest2.setUsername("user2");
        loginRequest2.setPassword("password");
        
        String loginJson2 = objectMapper.writeValueAsString(loginRequest2);
        
        MvcResult result2 = mockMvc.perform(post("/users/login")
        		.contentType(MediaType.APPLICATION_JSON).content(loginJson2))
        .andExpect(status().isOk()).andReturn();
        
        String token2 = result2.getResponse().getContentAsString();
	    
	    UserModel user = userRepository.getUserModelByUsername("user").orElseThrow();
	    TaskModel task = taskRepository.findByUser(user).get(0);
	    
	    mockMvc.perform(delete("/tasks/" + task.getId()).header("Authorization", "Bearer " + token2))
	    .andExpect(status().isNotFound());
	    
	    Optional<TaskModel> existingTask  = taskRepository.findById(task.getId());
	   
	    assertFalse(existingTask .isEmpty());
    }
    
    @Test
    void deleteTask_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
    	mockMvc.perform(delete("/tasks/1"))
	    .andExpect(status().isUnauthorized());
    }
    
    @Test
    void changeComplete_shouldUpdateTask_whenAuthenticated() throws Exception {
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
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isCreated());
	    
	    UserModel user = userRepository.getUserModelByUsername("user").orElseThrow();
	    TaskModel task = taskRepository.findByUser(user).get(0);
	    
	    mockMvc.perform(patch("/tasks/" + task.getId() + "/completed").param("completed", "true")
	    		.header("Authorization", "Bearer " + token))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.completed").value(true));
	    
	    TaskModel updatedTask = taskRepository.findById(task.getId()).orElseThrow();
	    
	    assertTrue(updatedTask.isCompleted());
    }
    
    @Test
    void changeComplete_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
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
	    
	    mockMvc.perform(patch("/tasks/1" + "/completed").param("completed", "true")
	    		.header("Authorization", "Bearer " + token))
	    .andExpect(status().isNotFound())
	    .andExpect(jsonPath("$.status").value(404))
	    .andExpect(jsonPath("$.message").value("Task not found!"));
    }
    
    @Test
    void changeComplete_shouldReturnBadRequest_whenCompletedParameterIsInvalid() throws Exception {
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
	    
	    mockMvc.perform(patch("/tasks/1/completed").param("completed", "something")
	    		.header("Authorization", "Bearer " + token))
	    .andExpect(status().isBadRequest());
    }
    
    @Test
    void changeComplete_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
    	mockMvc.perform(patch("/tasks/1/completed").param("completed", "true"))
    	.andExpect(status().isUnauthorized());
    }
    
    @Test
    void getAllTasks_shouldReturnTasks_whenAuthenticatedAsAdmin() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        UserModel user = userRepository.getUserModelByUsername("user").orElseThrow();
        user.setRole("ADMIN");
        userRepository.save(user);
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isCreated());
	    
	    mockMvc.perform(get("/tasks/admin/all").header("Authorization", "Bearer " + token)
	    		.param("page", "0").param("size", "2"))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.content.length()").value(1))
	    .andExpect(jsonPath("$.content[0].title").value("Feladat"));
	    
	    UserModel savedUser = userRepository.getUserModelByUsername("user").orElseThrow();
	    List<TaskModel> tasks = taskRepository.findByUser(savedUser);

	    assertEquals("ADMIN", savedUser.getRole());
	    assertEquals(1, tasks.size());
	    assertEquals("Feladat", tasks.get(0).getTitle());
    }
    
    @Test
    void getAllTasks_shouldReturnForbidden_whenAuthenticatedAsUser() throws Exception {
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
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isCreated());
	    
	    mockMvc.perform(get("/tasks/admin/all").header("Authorization", "Bearer " + token)
	    		.param("page", "0").param("size", "2"))
	    .andExpect(status().isForbidden());
    }
    
    @Test
    void getAllTasks_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
    	mockMvc.perform(get("/tasks/admin/all").param("page", "0").param("size", "2"))
	    .andExpect(status().isUnauthorized());
    }
    
    @Test
    void getAllTasks_shouldRespectPagination_whenAuthenticatedAsAdmin() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        UserModel user = userRepository.getUserModelByUsername("user").orElseThrow();
        user.setRole("ADMIN");
        userRepository.save(user);
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isCreated());
	    
	    TaskRequestDTO request2 = new TaskRequestDTO();
	    request2.setTitle("Feladat2");
	    request2.setDescription("Leiras2");
	    request2.setPriority("Fontos2");
	    request2.setDueDate(new Date());
	    
	    String requestJson2 = objectMapper.writeValueAsString(request2);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson2))
	    .andExpect(status().isCreated());
	    
	    TaskRequestDTO request3 = new TaskRequestDTO();
	    request3.setTitle("Feladat3");
	    request3.setDescription("Leiras3");
	    request3.setPriority("Fontos3");
	    request3.setDueDate(new Date());
	    
	    String requestJson3 = objectMapper.writeValueAsString(request3);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson3))
	    .andExpect(status().isCreated());
	    
	    mockMvc.perform(get("/tasks/admin/all").header("Authorization", "Bearer " + token)
	    		.param("page", "0").param("size", "2").param("sort", "id,asc"))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.content.length()").value(2))
	    .andExpect(jsonPath("$.totalElements").value(3))
	    .andExpect(jsonPath("$.totalPages").value(2))
	    .andExpect(jsonPath("$.number").value(0))
	    .andExpect(jsonPath("$.size").value(2))
	    .andExpect(jsonPath("$.content[0].title").value("Feladat"))
	    .andExpect(jsonPath("$.content[1].title").value("Feladat2"));
    }
    
    @Test
    void getAllTasks_shouldReturnSecondPage_whenAuthenticatedAsAdmin() throws Exception {
    	UserRequestDTO registerRequest = new UserRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@user.com");
        registerRequest.setPassword("password");
        
        String registerJson = objectMapper.writeValueAsString(registerRequest);
        
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(registerJson))
        .andExpect(status().isCreated());
        
        UserModel user = userRepository.getUserModelByUsername("user").orElseThrow();
        user.setRole("ADMIN");
        userRepository.save(user);
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");
        
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        
        MvcResult result = mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
        .andExpect(status().isOk()).andReturn();
        
        String token = result.getResponse().getContentAsString();
        
        TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    
	    String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson))
	    .andExpect(status().isCreated());
	    
	    TaskRequestDTO request2 = new TaskRequestDTO();
	    request2.setTitle("Feladat2");
	    request2.setDescription("Leiras2");
	    request2.setPriority("Fontos2");
	    request2.setDueDate(new Date());
	    
	    String requestJson2 = objectMapper.writeValueAsString(request2);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson2))
	    .andExpect(status().isCreated());
	    
	    TaskRequestDTO request3 = new TaskRequestDTO();
	    request3.setTitle("Feladat3");
	    request3.setDescription("Leiras3");
	    request3.setPriority("Fontos3");
	    request3.setDueDate(new Date());
	    
	    String requestJson3 = objectMapper.writeValueAsString(request3);
	    
	    mockMvc.perform(post("/tasks/create").header("Authorization", "Bearer " + token)
	    		.contentType(MediaType.APPLICATION_JSON).content(requestJson3))
	    .andExpect(status().isCreated());
	    
	    mockMvc.perform(get("/tasks/admin/all").header("Authorization", "Bearer " + token)
	    		.param("page", "1").param("size", "2").param("sort", "id,asc"))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.content.length()").value(1))
	    .andExpect(jsonPath("$.totalElements").value(3))
	    .andExpect(jsonPath("$.totalPages").value(2))
	    .andExpect(jsonPath("$.number").value(1))
	    .andExpect(jsonPath("$.size").value(2))
	    .andExpect(jsonPath("$.content[0].title").value("Feladat3"));
    }
}
