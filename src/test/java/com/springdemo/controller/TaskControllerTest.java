package com.springdemo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springdemo.dto.TaskRequestDTO;
import com.springdemo.dto.TaskResponseDTO;
import com.springdemo.exceptions.Exceptions.TaskNotFoundException;
import com.springdemo.exceptions.Exceptions.UserNotFoundException;
import com.springdemo.model.TaskModel;
import com.springdemo.service.CustomUserDetailsService;
import com.springdemo.service.JwtService;
import com.springdemo.service.TaskService;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest {
	@MockBean
	private TaskService taskService;
	@MockBean
	private Authentication authentication;
	@MockBean
	private JwtService jwtService;
	@MockBean
	private CustomUserDetailsService customUserDetailsService;
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	void createTask_shouldReturnCreatedTask_whenRequestIsValid() throws Exception {
		Date dueDate = new Date();
		String expectedDueDate = objectMapper.writeValueAsString(dueDate).replace("\"", "");
	    TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(dueDate);
	    TaskResponseDTO response = new TaskResponseDTO(new TaskModel("Feladat","Leiras","Fontos",dueDate));

	    when(taskService.createTask(any(TaskRequestDTO.class), eq(authentication))).thenReturn(response);
	    String json = objectMapper.writeValueAsString(request);

	    mockMvc.perform(post("/tasks/create").principal(authentication)
	    .contentType(MediaType.APPLICATION_JSON).content(json))
	    .andExpect(status().isCreated())
	    .andExpect(jsonPath("$.title").value("Feladat"))
	    .andExpect(jsonPath("$.description").value("Leiras"))
	    .andExpect(jsonPath("$.priority").value("Fontos"))
	    .andExpect(jsonPath("$.dueDate").value(expectedDueDate));

	    verify(taskService).createTask(any(TaskRequestDTO.class), eq(authentication));
	}
	
	@Test
	void createTask_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
	    TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    String json = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(post("/tasks/create").principal(authentication).contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isBadRequest());
	    
	    verify(taskService, never()).createTask(any(TaskRequestDTO.class), eq(authentication));
	}
	
	@Test
	void createTask_shouldReturnNotFound_whenUserNotFound() throws Exception {
		TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    String json = objectMapper.writeValueAsString(request);
	    
	    when(taskService.createTask(any(TaskRequestDTO.class), eq(authentication))).thenThrow(new UserNotFoundException("User not found!"));
	    
	    mockMvc.perform(post("/tasks/create").principal(authentication).contentType(MediaType.APPLICATION_JSON).content(json))
	    .andExpect(status().isNotFound())
	    .andExpect(jsonPath("$.status").value(404))
	    .andExpect(jsonPath("$.message").value("User not found!"));
	    
	    verify(taskService).createTask(any(TaskRequestDTO.class), eq(authentication));
	}
	
	@Test
	void getByUser_shouldReturnTasks_whenRequestIsValid() throws Exception {
		Date dueDate = new Date();
		String expectedDueDate = objectMapper.writeValueAsString(dueDate).replace("\"", "");
		TaskResponseDTO response = new TaskResponseDTO(new TaskModel("Feladat","Leiras","Fontos", dueDate));
		List<TaskResponseDTO> responses = List.of(response);
		
		when(taskService.getByUser(authentication)).thenReturn(responses);
		
		mockMvc.perform(get("/tasks/all").principal(authentication))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.length()").value(1))
		.andExpect(jsonPath("$[0].title").value("Feladat"))
		.andExpect(jsonPath("$[0].description").value("Leiras"))
		.andExpect(jsonPath("$[0].priority").value("Fontos"))
		.andExpect(jsonPath("$[0].dueDate").value(expectedDueDate));
		
		verify(taskService).getByUser(authentication);
	}
	
	@Test
	void getByUser_shouldReturnNotFound_whenUserNotFound() throws Exception {
		when(taskService.getByUser(authentication)).thenThrow(new UserNotFoundException("User not found!"));
		
		mockMvc.perform(get("/tasks/all").principal(authentication))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
	    .andExpect(jsonPath("$.message").value("User not found!"));
		
		verify(taskService).getByUser(authentication);
	}
	
	@Test
	void updateTask_shouldReturnUpdatedTask_whenRequestIsValid() throws Exception {
		Date dueDate = new Date();
		String expectedDueDate = objectMapper.writeValueAsString(dueDate).replace("\"", "");
	    TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(dueDate);
	    TaskResponseDTO response = new TaskResponseDTO(new TaskModel("Feladat2","Leiras2","Fontos2",dueDate));
	    String json = objectMapper.writeValueAsString(request);
	    
	    when(taskService.updateTask(eq(1L), any(TaskRequestDTO.class), eq(authentication))).thenReturn(response);
	    
	    mockMvc.perform(put("/tasks/1").principal(authentication).contentType(MediaType.APPLICATION_JSON).content(json))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.title").value("Feladat2"))
		.andExpect(jsonPath("$.description").value("Leiras2"))
		.andExpect(jsonPath("$.priority").value("Fontos2"))
		.andExpect(jsonPath("$.dueDate").value(expectedDueDate));
	    
	    verify(taskService).updateTask(eq(1L), any(TaskRequestDTO.class), eq(authentication));
	}
	
	@Test
	void updateTask_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
	    TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    String json = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(put("/tasks/1").principal(authentication).contentType(MediaType.APPLICATION_JSON).content(json))
	    .andExpect(status().isBadRequest())
	    .andExpect(jsonPath("$.status").value(400))
	    .andExpect(jsonPath("$.message").value("A cím nem lehet üres!"));
	    
	    verify(taskService, never()).updateTask(anyLong(), any(TaskRequestDTO.class), eq(authentication));
	}
	
	@Test
	void updateTask_shouldReturnNotFound_whenTaskNotFound() throws Exception {
		TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    String json = objectMapper.writeValueAsString(request);
	    
		when(taskService.updateTask(eq(1L), any(TaskRequestDTO.class), eq(authentication)))
		.thenThrow(new TaskNotFoundException("Task not found!"));
		
		mockMvc.perform(put("/tasks/1").principal(authentication).contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("Task not found!"));
		
		verify(taskService).updateTask(eq(1L), any(TaskRequestDTO.class), eq(authentication));
	}
	
	@Test
	void updateTask_shouldReturnNotFound_whenUsernameNotFound() throws Exception {
		TaskRequestDTO request = new TaskRequestDTO();
	    request.setTitle("Feladat");
	    request.setDescription("Leiras");
	    request.setPriority("Fontos");
	    request.setDueDate(new Date());
	    String json = objectMapper.writeValueAsString(request);
	    
		when(taskService.updateTask(eq(1L), any(TaskRequestDTO.class), eq(authentication)))
		.thenThrow(new UserNotFoundException("User not found!"));
		
		mockMvc.perform(put("/tasks/1").principal(authentication).contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("User not found!"));
		
		verify(taskService).updateTask(eq(1L), any(TaskRequestDTO.class), eq(authentication));
	}
	
	@Test
	void deleteTask_shouldReturnNoContent_whenTaskIsDeleted() throws Exception {
		mockMvc.perform(delete("/tasks/1").principal(authentication))
		.andExpect(status().isNoContent());
		
		verify(taskService).deleteTask(1L, authentication);
	}
	
	@Test
	void deleteTask_shouldReturnNotFound_whenTaskNotFound() throws Exception {
		doThrow(new TaskNotFoundException("Task not found!")).when(taskService).deleteTask(1L, authentication);
		
		mockMvc.perform(delete("/tasks/1").principal(authentication))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("Task not found!"));
		
		verify(taskService).deleteTask(1L, authentication);	
	}
	
	@Test
	void deleteTask_shouldReturnNotFound_whenUsernameNotFound() throws Exception {
		doThrow(new UserNotFoundException("User not found!")).
		when(taskService).deleteTask(1L, authentication);
		
		mockMvc.perform(delete("/tasks/1").principal(authentication))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("User not found!"));
		
		verify(taskService).deleteTask(1L, authentication);	
	}
	
	@Test
	void changeComplete_shouldReturnUpdatedTask_whenRequestIsValid() throws Exception {
		TaskResponseDTO response = new TaskResponseDTO(new TaskModel("Feladat","Leiras","Fontos", new Date()));
	    
		when(taskService.changeComplete(1L, true, authentication)).thenReturn(response);
		
		mockMvc.perform(patch("/tasks/1/completed").param("completed", "true").principal(authentication))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.title").value("Feladat"))
	    .andExpect(jsonPath("$.description").value("Leiras"))
	    .andExpect(jsonPath("$.priority").value("Fontos"));
		
		verify(taskService).changeComplete(1L, true, authentication);
	}
	
	@Test
	void changeComplete_shouldReturnBadRequest_whenCompletedParameterIsMissing() throws Exception {
	    mockMvc.perform(patch("/tasks/1/completed").principal(authentication)).andExpect(status().isBadRequest());

	    verify(taskService, never()).changeComplete(anyLong(), anyBoolean(), eq(authentication));
	}
	
	@Test
	void changeComplete_shouldReturnNotFound_whenTaskNotFound() throws Exception {
		when(taskService.changeComplete(1L, true, authentication)).thenThrow(new TaskNotFoundException("Task not found!"));
		
		mockMvc.perform(patch("/tasks/1/completed").param("completed", "true").principal(authentication))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("Task not found!"));
		
		verify(taskService).changeComplete(1L, true, authentication);
	}
	
	@Test
	void changeComplete_shouldReturnNotFound_whenUsernameNotFound() throws Exception {
		when(taskService.changeComplete(1L, true, authentication)).thenThrow(new UserNotFoundException("User not found!"));
		
		mockMvc.perform(patch("/tasks/1/completed").param("completed", "true").principal(authentication))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("User not found!"));
		
		verify(taskService).changeComplete(1L, true, authentication);
	}
	
	@Test
	void changeComplete_shouldReturnBadRequest_whenCompletedParameterIsInvalid() throws Exception {
		mockMvc.perform(patch("/tasks/1/completed").param("completed", "abc").principal(authentication))
		.andExpect(status().isBadRequest());
		
		verify(taskService, never()).changeComplete(anyLong(), anyBoolean(), eq(authentication));
	}
	
	@Test
	void getAllTasks_shouldReturnTasks_whenRequestIsValid() throws Exception {
	    TaskResponseDTO response = new TaskResponseDTO(
	    		new TaskModel("Feladat", "Leiras", "Fontos", new Date()));

	    Page<TaskResponseDTO> responses = new PageImpl<>(List.of(response));

	    when(taskService.getAllTasks(any(Pageable.class))).thenReturn(responses);

	    mockMvc.perform(get("/tasks/admin/all").param("page", "0").param("size", "10"))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.content.length()").value(1))
	    .andExpect(jsonPath("$.content[0].title").value("Feladat"))
	    .andExpect(jsonPath("$.content[0].description").value("Leiras"))
	    .andExpect(jsonPath("$.content[0].priority").value("Fontos"));

	    verify(taskService).getAllTasks(any(Pageable.class));
	}
	
	@Test
	void getAllTasks_shouldPassPageableToService() throws Exception {
	    Page<TaskResponseDTO> responses = new PageImpl<>(List.of());
	    
	    when(taskService.getAllTasks(any(Pageable.class))).thenReturn(responses);
	    
	    mockMvc.perform(get("/tasks/admin/all").param("page", "1").param("size", "5"))
	    .andExpect(status().isOk());
	    
	    verify(taskService).getAllTasks(argThat(pageable -> 
	    					pageable.getPageNumber() == 1 && pageable.getPageSize() == 5));
	}
	
	@Test
	void getAllTasks_shouldReturnEmptyPage_whenNoTasksExist() throws Exception {
	    when(taskService.getAllTasks(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
	    
	    mockMvc.perform(get("/tasks/admin/all").param("page", "0").param("size", "2"))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.content.length()").value(0));
	    
	    verify(taskService).getAllTasks(any(Pageable.class));
	}
}
